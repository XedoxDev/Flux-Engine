package org.xedox.engine.objects;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.badlogic.gdx.math.Vector2;
import java.lang.reflect.InvocationTargetException;
import org.xedox.engine.GameCore;
import org.xedox.engine.TouchListener;

public abstract class GameObject extends BaseObject implements Disposable, TouchListener {
    protected transient ObjectScript script;
    private boolean scriptInitialized = false;

    public GameObject(String name) {
        super(name);
    }

    protected void initScript() {
        if (!scriptInitialized) {
            this.script = new ObjectScript(this);
            scriptInitialized = true;
        }
    }

    public void start() {
        if (GameCore.init) {
            initScript();
            script.loadScriptPath(scriptPath);
            script.call("start");
            GameCore.multiplexer.addProcessor(this);
        }
    }

    public void loadScriptPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Script path cannot be null or empty");
        }
        setScriptPath(path);
        if (script != null) {
            script.loadScriptPath(path);
        }
    }

    public void reloadScript() {
        if (script != null && scriptPath != null && !scriptPath.isEmpty()) {
            script.loadScriptPath(scriptPath);
        }
    }

    public abstract void render(SpriteBatch batch);

    public abstract void update(float deltaTime);

    protected abstract void setupBindings();

    @Override
    public void touchUp(float x, float y, int pointer) {
        call("touchUp", x, y, pointer);
    }

    @Override
    public void touchMove(float x, float y, int pointer) {
        call("touchMove", x, y, pointer);
    }

    @Override
    public void touchDown(float x, float y, int pointer) {
        call("touchDown", x, y, pointer);
    }

    @Override
    public void setScriptPath(String scriptPath) {
        super.setScriptPath(scriptPath);
        reloadScript();
    }

    public ObjectScript getScript() {
        ensureScriptInitialized();
        return script;
    }

    public void call(String functionName, Object... args) {
        ensureScriptInitialized();
        if (script != null) {
            script.call(functionName, args);
        }
    }

    private void ensureScriptInitialized() {
        if (!scriptInitialized) {
            initScript();
        }
    }

    public abstract GameObject clone();

    @Override
    public void dispose() {
        if (script != null) {
            script.dispose();
        }
        GameCore.multiplexer.removeProcessor(this);
    }

    public static class Serializer extends com.esotericsoftware.kryo.kryo5.Serializer<GameObject> {
        @Override
        public void write(Kryo kryo, Output out, GameObject obj) {
            out.writeString(obj.name);
            out.writeString(obj.scriptPath != null ? obj.scriptPath : "");
            kryo.writeObject(out, obj.position);
            kryo.writeObject(out, obj.size);
            kryo.writeObject(out, obj.scale);
            out.writeBoolean(obj.visible);
            out.writeFloat(obj.angle);
        }

        @Override
        public GameObject read(Kryo kryo, Input in, Class<? extends GameObject> type) {
            String name = in.readString();
            String scriptPath = in.readString();
            Vector2 position = kryo.readObject(in, Vector2.class);
            Vector2 size = kryo.readObject(in, Vector2.class);
            Vector2 scale = kryo.readObject(in, Vector2.class);
            boolean visible = in.readBoolean();
            float angle = in.readFloat();

            try {
                GameObject object = type.getDeclaredConstructor(String.class).newInstance(name);
                object.setPosition(position);
                object.setSize(size);
                object.setScale(scale);
                object.setVisible(visible);
                object.setAngle(angle);
                object.setScriptPath(scriptPath.isEmpty() ? null : scriptPath);
                return object;
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize BaseObject", e);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        appendf(buffer, "%s [%s]\n", getClass().getSimpleName(), getName());
        appendf(buffer, "  Position: [%.2f; %.2f]\n", getX(), getY());
        appendf(buffer, "  Size: [%d; %d]\n", getWidth(), getHeight());
        appendf(buffer, "  Angle: %.2f\n", getAngle());
        appendf(buffer, "  Script: %s\n", getScriptPath());
        appendf(buffer, "  Scale: [%.2f; %.2f]\n", getScaleX(), getScaleY());
        appendf(buffer, "  Visible: %s\n", isVisible() ? "True" : "False");
        return buffer.toString();
    }

    protected void appendf(StringBuilder buffer, String pattern, Object... args) {
        buffer.append(String.format(pattern, args));
    }
}
