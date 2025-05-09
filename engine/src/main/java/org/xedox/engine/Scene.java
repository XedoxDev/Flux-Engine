package org.xedox.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.xedox.engine.objects.GameObject;

public class Scene extends BaseScene implements Cloneable {

    private transient Viewport viewport;
    private transient OrthographicCamera camera;
    private transient SpriteBatch batch;

    public Scene() {
        super("default", 800, 600);
    }

    public Scene(String name, int width, int height) {
        super(name, width, height);
    }

    @Override
    public void start() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(width, height, camera);
        batch = new SpriteBatch();
        TouchListener.viewport[0] = viewport;
        super.start();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        if (viewport != null) {
            viewport.setWorldSize(width, height);
            viewport.update(width, height, true);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (viewport == null || camera == null) {
            start();
        }
        viewport.apply();

        if (batch == null) {
            batch = new SpriteBatch();
        }
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        for (GameObject obj : objects) {
            if (obj != null) {
                obj.render(batch);
            }
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
            camera.update();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        camera = null;
        viewport = null;
    }

    @Override
    public Scene clone() {
        try {
            Scene scene = KryoEnvironment.deserialize(KryoEnvironment.serialize(this), Scene.class);
            scene.start();
            return scene;
        } catch (Exception e) {
            Gdx.app.error("Scene", "Clone failed", e);
            return null;
        }
    }

    public OrthographicCamera getCamera() {
        if (camera == null) {
            start();
        }
        return camera;
    }

    public Viewport getViewport() {
        if (viewport == null) {
            start();
        }
        return viewport;
    }

    public SpriteBatch getBatch() {
        if (batch == null) {
            batch = new SpriteBatch();
        }
        return batch;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        objects.forEach((o) -> buff.append("\n" + o.toString()));
        return String.format(
                        """
        %s [%s]
         ├─ Size:          %d x %d
         ├─ Objects count: %d
         └─ Objects:       %s
        """,
                        getClass().getSimpleName(),
                        getName(),
                        getWidth(),
                        getHeight(),
                        size(),
                        buff.toString())
                .trim();
    }
}
