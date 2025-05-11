package org.xedox.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.esotericsoftware.kryo.kryo5.io.Input;
import org.luaj.vm2.LuaValue;
import org.xedox.engine.GameCore;

public class TextureObject extends GameObject {
    private String texturePath;
    private Texture texture;
    private final Vector2 tmpPosition = new Vector2();
    private final Vector2 tmpScale = new Vector2();

    public TextureObject(String name) {
        super(name);
    }

    @Override
    public void start() {
        super.start();
        texture = new Texture(Gdx.files.absolute(GameCore.getAssetsPath() + texturePath));
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture == null || !isVisible()) return;

        tmpPosition.set(getPosition());
        tmpScale.set(getScale());
        float rotation = getAngle();

        float width = texture.getWidth() * tmpScale.x;
        float height = texture.getHeight() * tmpScale.y;
        batch.draw(
                texture,
                tmpPosition.x - width / 2,
                tmpPosition.y - height / 2,
                width / 2,
                height / 2,
                width,
                height,
                1f,
                1f,
                rotation,
                0,
                0,
                texture.getWidth(),
                texture.getHeight(),
                false,
                false);
    }

    @Override
    public void update(float deltaTime) {
        call("update", deltaTime);
    }

    @Override
    protected void setupBindings() {
        script.bind(
                "loadTexture",
                args -> {
                    String path = args.checkjstring(1);
                    texturePath = path;
                    if (texture != null) {
                        texture.dispose();
                        texture = null;
                    }
                    texture = new Texture(Gdx.files.absolute(GameCore.assetsPath + path));
                    return LuaValue.NIL;
                });
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    public static class Serializer extends GameObject.Serializer {
        @Override
        public void write(Kryo kryo, Output out, GameObject obj) {
            super.write(kryo, out, obj);
            TextureObject obj2 = (TextureObject) obj;
            out.writeString(obj2.texturePath != null ? obj2.texturePath : "");
        }

        @Override
        public TextureObject read(Kryo kryo, Input in, Class<? extends GameObject> type) {
            TextureObject object = (TextureObject) super.read(kryo, in, type);
            String texturePath = in.readString();
            object.setTexturePath(texturePath.isEmpty() ? null : texturePath);
            return object;
        }
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        appendf(buffer, "  %s [%s]\n", getClass().getSimpleName(), getName());
        appendf(buffer, "    Position: [%.2f; %.2f]\n", getX(), getY());
        appendf(buffer, "    Size: [%.2f; %.2f]\n", getWidth(), getHeight());
        appendf(buffer, "    Angle: %.2f\n", getAngle());
        appendf(buffer, "    Script: %s\n", getScriptPath());
        appendf(buffer, "    Scale: [%.2f; %.2f]\n", getScaleX(), getScaleY());
        appendf(buffer, "    Visible: %s\n", isVisible() ? "True" : "False");
        appendf(buffer, "    Texture: %s\n", getTexturePath() != null ? getTexturePath() : "None");
        return buffer.toString();
    }

    @Override
    public TextureObject clone() {
        TextureObject obj = new TextureObject(getName());
        obj.setTexturePath(getTexturePath());
        obj.setSize(getSize());
        obj.setScale(getScale());
        obj.setPosition(getPosition());
        obj.setAngle(getAngle());
        obj.setScriptPath(getScriptPath());
        obj.setVisible(isVisible());
        return obj;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean haveTexture() {
        return texture != null;
    }
}
