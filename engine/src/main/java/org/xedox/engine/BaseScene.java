package org.xedox.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.esotericsoftware.kryo.kryo5.io.Input;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import org.xedox.engine.objects.BaseObject;
import org.xedox.engine.objects.GameObject;

public class BaseScene implements Serializable, Screen {

    protected transient List<GameObject> objects = new ArrayList<>();
    protected int width;
    protected int height;
    protected String name;

    public BaseScene(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public void start() {
        for (GameObject obj : objects) {
            obj.start();
        }
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void add(GameObject object) {
        objects.add(object);
    }

    public void add(int index, GameObject object) {
        objects.add(index, object);
    }

    public boolean remove(GameObject object) {
        return objects.remove(object);
    }

    public GameObject remove(int index) {
        GameObject obj = objects.remove(index);
        return obj;
    }

    public GameObject get(int index) {
        return objects.get(index);
    }

    public boolean contains(GameObject object) {
        return objects.contains(object);
    }

    public void clear() {
        objects.clear();
    }

    public int size() {
        return objects.size();
    }

    public boolean isEmpty() {
        return objects.isEmpty();
    }

    public int indexOf(GameObject object) {
        return objects.indexOf(object);
    }

    public void forEach(Consumer<? super GameObject> action) {
        objects.forEach(action);
    }

    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    public GameObject set(int index, GameObject object) {
        GameObject old = objects.set(index, object);
        return old;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public GameObject findObjectByName(String name) {
        for (GameObject obj : objects) {
            if (name.equals(obj.getName())) {
                return obj;
            }
        }
        return null;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.valueOf("#000000"));
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void show() {
        for (GameObject obj : objects) {
            obj.call("show");
        }
    }

    @Override
    public void pause() {
        for (GameObject obj : objects) {
            obj.call("pause");
        }
    }

    @Override
    public void resume() {
        for (GameObject obj : objects) {
            obj.call("resume");
        }
    }

    @Override
    public void hide() {
        for (GameObject obj : objects) {
            obj.call("hide");
        }
    }

    @Override
    public void dispose() {
        if (objects != null) {
            for (GameObject obj : objects) {
                try {
                    if (obj != null) {
                        obj.call("dispose");
                    }
                } catch (Exception e) {
                    Gdx.app.error("Dispose", "Error calling dispose on object", e);
                }
            }

            for (GameObject obj : objects) {
                try {
                    if (obj != null) {
                        obj.dispose();
                    }
                } catch (Exception e) {
                    Gdx.app.error("Dispose", "Error disposing object", e);
                }
            }

            objects.clear();
        }
    }

    public void addAll(List<GameObject> objects) {
        this.objects.addAll(objects);
    }

    public static class Serializer extends com.esotericsoftware.kryo.kryo5.Serializer<BaseScene> {
        @Override
        public void write(Kryo kryo, Output out, BaseScene scene) {
            out.writeString(scene.name);
            out.writeInt(scene.width);
            out.writeInt(scene.height);

            kryo.writeClass(out, scene.getClass());

            out.writeInt(scene.objects.size());
            for (GameObject obj : scene.objects) {
                kryo.writeClass(out, obj.getClass());
                kryo.writeObject(out, obj);
            }
        }

        @Override
        public BaseScene read(Kryo kryo, Input in, Class<? extends BaseScene> type) {
            String name = in.readString();
            int width = in.readInt();
            int height = in.readInt();

            Class<? extends BaseScene> clazz = kryo.readClass(in).getType();
            BaseScene scene = kryo.newInstance(clazz);

            scene.name = name;
            scene.width = width;
            scene.height = height;
            scene.objects = new ArrayList<>();

            int objectCount = in.readInt();
            for (int i = 0; i < objectCount; i++) {
                Class<? extends GameObject> objClass = kryo.readClass(in).getType();
                GameObject obj = kryo.readObject(in, objClass);
                scene.objects.add(obj);
            }

            return scene;
        }
    }
}
