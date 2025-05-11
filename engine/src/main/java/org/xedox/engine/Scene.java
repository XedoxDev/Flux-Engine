package org.xedox.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.xedox.engine.objects.GameObject;

public class Scene extends BaseScene implements Cloneable {

    private transient Viewport viewport;
    private transient OrthographicCamera camera;
    private transient SpriteBatch batch;

    public Scene(String name, int width, int height) {
        super(name, width, height);
    }

    public Scene() {
        this("default", 800, 600);
    }

    @Override
    public void start() {
        if (GameCore.init) {
            camera = new OrthographicCamera();
            viewport = new FitViewport(width, height, camera);
            batch = new SpriteBatch();
            TouchListener.viewport[0] = viewport;
        }
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
        viewport.apply();

        batch.begin();
        for (GameObject obj : objects) {
            obj.render(batch);
            obj.update(delta);
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

    public OrthographicCamera getCamera() {
        if (camera == null) {
            camera = new OrthographicCamera();
        }
        return camera;
    }

    public Viewport getViewport() {
        if (viewport == null) {
            viewport = new FitViewport(width, height, getCamera());
        }
        return viewport;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        objects.forEach(o -> buff.append("\n").append(o.toString()));

        return String.format(
                        """
                %s [%s]
                Size:          %d x %d
                Objects count: %d
                Objects:       %s""",
                        getClass().getSimpleName(),
                        getName(),
                        getWidth(),
                        getHeight(),
                        size(),
                        buff.toString())
                .trim();
    }

    @Override
    public Scene clone() {
        try {
            Scene clonedScene = new Scene(this.getName(), this.getWidth(), this.getHeight());

            clonedScene.camera = new OrthographicCamera();
            clonedScene.viewport = new FitViewport(getWidth(), getHeight(), clonedScene.camera);

            if (this.camera != null) {
                clonedScene.camera.position.set(this.camera.position);
                clonedScene.camera.zoom = this.camera.zoom;
                clonedScene.camera.update();
            }

            for (GameObject obj : this.objects) {
                if (obj != null) {
                    try {
                        GameObject clonedObj = obj.clone();
                        if (clonedObj != null) {
                            clonedScene.add(clonedObj);
                        }
                    } catch (Exception e) {
                        Gdx.app.error("Scene", "Failed to clone object", e);
                    }
                }
            }

            return clonedScene;
        } catch (Exception e) {
            Gdx.app.error("Scene", "Clone failed", e);
            throw new RuntimeException("Scene clone failed", e);
        }
    }
}
