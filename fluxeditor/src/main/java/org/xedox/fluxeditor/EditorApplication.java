package org.xedox.fluxeditor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.xedox.engine.Scene;
import org.xedox.engine.objects.GameObject;

public class EditorApplication implements ApplicationListener {
    private static final String TAG = "EditorApplication";

    private final Color backgroundColor = Color.valueOf("#474747");
    private final Color linesColor = Color.valueOf("#585858");
    private final Color windowColor = Color.valueOf("#707070");

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private OrthographicCamera camera;
    private Viewport viewport;
    private int sceneWidth = 1600, sceneHeight = 720;
    private Scene scene;
    private boolean initialized = false;
    private Scene pendingScene;
    private BoundsRenderer boundsRenderer;
    private EditorGesture editorGesture;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        font = new BitmapFont();

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        camera.position.set(sceneWidth / 2f, sceneHeight / 2f, 0);
        camera.zoom = 1.5f;
        camera.update();

        initialized = true;

        if (pendingScene != null) {
            setScene(pendingScene);
            pendingScene = null;
        }

        boundsRenderer = new BoundsRenderer(this);
        editorGesture = new EditorGesture(this);
        GestureDetector detector = new GestureDetector(editorGesture);
        Gdx.input.setInputProcessor(detector);
        
    }

    public Vector3 screenToWorld(float screenX, float screenY) {
        Vector3 curr = new Vector3(screenX, screenY, 0);
        return camera.unproject(curr);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(sceneWidth / 2f, sceneHeight / 2f, 0);
        camera.update();
    }

    @Override
    public void render() {
        ScreenUtils.clear(backgroundColor);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        drawGrid();
        drawWindow();
        shapeRenderer.end();

        batch.begin();
        for (GameObject obj : scene.getObjects()) {
            obj.render(batch);
        }
        batch.end();

        boundsRenderer.render(scene.getObjects(), camera.combined);
        batch.begin();
        font.draw(batch, "Camera: " + camera.position + " Zoom: " + camera.zoom, 10, 20);
        font.draw(batch, "Objects: " + scene.getObjects().size(), 10, 40);
        batch.end();
    }

    private void drawGrid() {
        shapeRenderer.setColor(linesColor);
        float gridSize = 20f;
        float visibleWidth = camera.viewportWidth * camera.zoom;
        float visibleHeight = camera.viewportHeight * camera.zoom;

        float left = camera.position.x - visibleWidth / 2;
        float right = camera.position.x + visibleWidth / 2;
        float bottom = camera.position.y - visibleHeight / 2;
        float top = camera.position.y + visibleHeight / 2;

        float startX = (float) (Math.floor(left / gridSize) * gridSize);
        float startY = (float) (Math.floor(bottom / gridSize) * gridSize);

        for (float x = startX; x <= right; x += gridSize) {
            shapeRenderer.line(x, bottom, x, top);
        }

        for (float y = startY; y <= top; y += gridSize) {
            shapeRenderer.line(left, y, right, y);
        }

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.line(sceneWidth / 2f, bottom, sceneWidth / 2f, top);
        shapeRenderer.line(left, sceneHeight / 2f, right, sceneHeight / 2f);
    }

    private void drawWindow() {
        shapeRenderer.setColor(windowColor);
        shapeRenderer.rect(0, 0, sceneWidth, sceneHeight);
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (scene != null) scene.dispose();
        Gdx.input.setInputProcessor(null);
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        if (this.scene != null && this.scene.getName() != scene.getName()) {
            this.scene.dispose();
        }

        if (scene != null) {
            this.sceneWidth = scene.getWidth();
            this.sceneHeight = scene.getHeight();

            if (initialized) {
                this.scene = scene;
                scene.start();
                camera.position.set(sceneWidth / 2f, sceneHeight / 2f, 0);
                camera.zoom = 1.5f;
                camera.update();
            } else {
                pendingScene = scene;
            }
        }
    }

    public boolean isSelected(GameObject obj) {
        return editorGesture != null
                && editorGesture.getSelected() != null
                && editorGesture.getSelected().equals(obj);
    }

    public void moveCamera(float x, float y) {
        camera.position.set(x, y, 0);
    }

    public void addCameraPosition(float x, float y) {
        camera.position.add(x, y, 0);
    }

    public void zoomCamera(float zoom) {
        camera.zoom = MathUtils.clamp(zoom, 0.1f, 10f);
    }

    public float getCameraZoom() {
        return camera.zoom;
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
