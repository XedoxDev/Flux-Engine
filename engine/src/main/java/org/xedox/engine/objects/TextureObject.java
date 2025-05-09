package org.xedox.engine.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.esotericsoftware.kryo.kryo5.io.Input;
import java.lang.reflect.InvocationTargetException;
import org.luaj.vm2.LuaValue;
import org.xedox.engine.GameCore;

public class TextureObject extends GameObject {
    private String texturePath;
    private Texture texture;

    public TextureObject(String name) {
        super(name);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (texture == null) return;

        Vector2 position = getPosition();
        Vector2 scale = getScale();
        float rotation = getAngle();

        float width = texture.getWidth() * scale.x;
        float height = texture.getHeight() * scale.y;
        batch.draw(
                texture,
                position.x,
                position.y,
                width / 2,
                height / 2,
                width,
                height,
                scale.x,
                scale.y,
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
        script.set("loadTexture", args -> {
            String path = args.checkjstring(1);
            texturePath = GameCore.assetsPath + path;
            if (texture != null) texture.dispose();
            texture = new Texture(texturePath);
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
        }
    }
    
    public static class Serializer extends GameObject.Serializer{
        @Override
        public void write(Kryo kryo, Output out, GameObject obj) {
            super.write(kryo, out, obj);
            TextureObject obj2 = (TextureObject) obj;
            out.writeString(obj2.texturePath);
        }

        @Override
        public TextureObject read(Kryo kryo, Input in, Class<? extends GameObject> type) {
            String name = in.readString();
            String scriptPath = in.readString();
            Vector2 position = kryo.readObject(in, Vector2.class);
            Vector2 size = kryo.readObject(in, Vector2.class);
            Vector2 scale = kryo.readObject(in, Vector2.class);
            boolean visible = in.readBoolean();
            float angle = in.readFloat();
            String texturePath = in.readString();

            try {
                TextureObject object = (TextureObject) type.getDeclaredConstructor(String.class).newInstance(name);
                object.setPosition(position);
                object.setSize(size);
                object.setScale(scale);
                object.setVisible(visible);
                object.setAngle(angle);
                object.setScriptPath(scriptPath.isEmpty() ? null : scriptPath);
                object.setTexturePath(texturePath);
                return object;
            } catch (InstantiationException | IllegalAccessException | 
                    InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Failed to deserialize BaseObject", e);
            }
        }
    }
}