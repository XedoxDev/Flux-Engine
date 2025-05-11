package org.xedox.engine.objects;

import com.badlogic.gdx.utils.Disposable;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.*;
import org.xedox.engine.GameCore;
import java.io.File;

public class ObjectScript implements Disposable {
    private final GameObject gameObject;
    private final Globals luaGlobals;
    private String scriptPath;

    public interface LFunction {
        LuaValue invoke(Varargs args);
    }

    public ObjectScript(GameObject gameObject) {
        this.gameObject = gameObject;
        this.luaGlobals = JsePlatform.standardGlobals();
        this.luaGlobals.set("self", CoerceJavaToLua.coerce(gameObject));
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

        String fullPath = scriptPath;
        if (GameCore.assetsPath != null && !new File(scriptPath).isAbsolute()) {
            fullPath = GameCore.assetsPath + scriptPath;
        }

        File scriptFile = new File(fullPath);
        if (!scriptFile.exists()) {
            throw new RuntimeException("Script file not found: " + fullPath);
        }

        LuaValue chunk = luaGlobals.loadfile(fullPath);
        chunk.call();
    }

    public void call(String methodName, Object... args) {
    LuaValue func = luaGlobals.get(methodName);
    if(!func.isfunction()) return;
    
    LuaValue[] luaArgs = new LuaValue[args.length];
    for (int i = 0; i < args.length; i++) {
        luaArgs[i] = args[i] != null ? CoerceJavaToLua.coerce(args[i]) : LuaValue.NIL;
    }
    Varargs varargs = LuaValue.varargsOf(luaArgs);
    func.invoke(varargs);
}

    public void bind(String luaFunctionName, java.util.function.Function<Varargs, LuaValue> function) {
        luaGlobals.set(luaFunctionName, new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                try {
                    LuaValue result = function.apply(args);
                    return result != null ? result : LuaValue.NIL;
                } catch (Exception e) {
                    return LuaValue.error("Error in " + luaFunctionName + ": " + e.getMessage());
                }
            }
        });
    }

    private void setupBindings() {
        bind("getX", args -> LuaValue.valueOf(gameObject.getX()));
        bind("getY", args -> LuaValue.valueOf(gameObject.getY()));
        bind("getAngle", args -> LuaValue.valueOf(gameObject.getAngle()));
        bind("getWidth", args -> LuaValue.valueOf(gameObject.getWidth()));
        bind("getHeight", args -> LuaValue.valueOf(gameObject.getHeight()));
        bind("getName", args -> LuaValue.valueOf(gameObject.getName()));
        bind("getVisible", args -> LuaValue.valueOf(gameObject.isVisible()));

        bind("setX", args -> {
            gameObject.setX((float) args.checkdouble(1));
            return LuaValue.NIL;
        });

        bind("setY", args -> {
            gameObject.setY((float) args.checkdouble(1));
            return LuaValue.NIL;
        });

        bind("setAngle", args -> {
            gameObject.setAngle((float) args.checkdouble(1));
            return LuaValue.NIL;
        });

        bind("setWidth", args -> {
            gameObject.setWidth((float) args.checkdouble(1));
            return LuaValue.NIL;
        });

        bind("setHeight", args -> {
            gameObject.setHeight((float) args.checkdouble(1));
            return LuaValue.NIL;
        });

        bind("setVisible", args -> {
            gameObject.setVisible(args.checkboolean(1));
            return LuaValue.NIL;
        });
    }

    @Override
    public void dispose() {
        LuaValue close = luaGlobals.get("close");
        if (close.isfunction()) {
            close.call();
        }
    }
}