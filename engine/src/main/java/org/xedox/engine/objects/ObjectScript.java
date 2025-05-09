package org.xedox.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.*;
import org.xedox.engine.GameCore;
import java.io.File;

public class ObjectScript implements Disposable {
    private final GameObject gameObject;
    private final Globals luaGlobals;
    private LuaValue scriptEnv;
    private String scriptPath;

    public interface LFunction {
        LuaValue invoke(Varargs args);
    }

    public ObjectScript(GameObject gameObject) {
        this.gameObject = gameObject;
        this.luaGlobals = JsePlatform.standardGlobals();
        this.luaGlobals.load(new BaseLib());
        this.luaGlobals.load(new PackageLib());
        this.luaGlobals.load(new Bit32Lib());
        this.luaGlobals.load(new TableLib());
        this.luaGlobals.load(new StringLib());
        this.luaGlobals.load(new CoroutineLib());
        this.luaGlobals.load(new JseBaseLib());
        this.luaGlobals.load(new JseMathLib());
        this.luaGlobals.load(new JseIoLib());
        this.luaGlobals.load(new JseOsLib());
        this.luaGlobals.set("this", CoerceJavaToLua.coerce(gameObject));
        setupBindings();
    }

    public void loadScriptPath(String scriptPath) {
        if (scriptPath == null || scriptPath.isEmpty()) {
            throw new IllegalArgumentException("Script path cannot be null or empty");
        }
        this.scriptPath = scriptPath;
        reloadScript();
    }

    public void reloadScript() {
        if (scriptPath == null) return;

        try {
            String fullPath = scriptPath;
            if (GameCore.assetsPath != null && !new File(scriptPath).isAbsolute()) {
                fullPath = GameCore.assetsPath + scriptPath;
            }

            File scriptFile = new File(fullPath);
            if (!scriptFile.exists()) {
                throw new RuntimeException("Script file not found: " + fullPath);
            }

            LuaValue chunk = luaGlobals.loadfile(fullPath);
            scriptEnv = new LuaTable();
            scriptEnv.setmetatable(luaGlobals);
            chunk.call(scriptEnv);
        } catch (Exception e) {
            Gdx.app.error("ObjectScript", "Failed to load script: " + scriptPath, e);
            scriptEnv = luaGlobals;
        }
    }

    public void call(String methodName, Object... args) {
        if (scriptEnv == null || methodName == null) return;
        
        try {
            LuaValue func = scriptEnv.get(methodName);
            if (func.isnil() || !func.isfunction()) return;
            
            LuaValue[] luaArgs = new LuaValue[args.length];
            for (int i = 0; i < args.length; i++) {
                luaArgs[i] = args[i] != null ? CoerceJavaToLua.coerce(args[i]) : LuaValue.NIL;
            }
            
            func.invoke(luaArgs);
        } catch (Exception e) {
            Gdx.app.error("ObjectScript", "Error calling Lua method: " + methodName, e);
        }
    }

    public void set(String name, LFunction function) {
        luaGlobals.set(name, new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return function.invoke(args);
            }
        });
    }

    @Override
    public void dispose() {
        if (luaGlobals != null) {
            try {
                LuaValue close = luaGlobals.get("close");
                if (close.isfunction()) {
                    close.call();
                }
            } catch (Exception e) {
                Gdx.app.error("ObjectScript", "Error disposing script", e);
            }
        }
    }

    private void setupBindings() {
        try {
            set("getX", args -> LuaValue.valueOf(gameObject.getX()));
            set("getY", args -> LuaValue.valueOf(gameObject.getY()));
            set("getAngle", args -> LuaValue.valueOf(gameObject.getAngle()));
            set("getWidth", args -> LuaValue.valueOf(gameObject.getWidth()));
            set("getHeight", args -> LuaValue.valueOf(gameObject.getHeight()));
            set("getName", args -> LuaValue.valueOf(gameObject.getName()));
            set("getVisible", args -> LuaValue.valueOf(gameObject.isVisible()));
            
            set("setX", args -> {
                gameObject.setX((float) args.checkdouble(1));
                return LuaValue.NIL;
            });
            
            set("setY", args -> {
                gameObject.setY((float) args.checkdouble(1));
                return LuaValue.NIL;
            });
            
            set("setAngle", args -> {
                gameObject.setAngle((float) args.checkdouble(1));
                return LuaValue.NIL;
            });
            
            set("setWidth", args -> {
                gameObject.setWidth((float) args.checkdouble(1));
                return LuaValue.NIL;
            });
            
            set("setHeight", args -> {
                gameObject.setHeight((float) args.checkdouble(1));
                return LuaValue.NIL;
            });
            
            set("setVisible", args -> {
                gameObject.setVisible(args.checkboolean(1));
                return LuaValue.NIL;
            });
        } catch (Exception e) {
            Gdx.app.error("ObjectScript", "Setup bindings failed", e);
        }
    }
}