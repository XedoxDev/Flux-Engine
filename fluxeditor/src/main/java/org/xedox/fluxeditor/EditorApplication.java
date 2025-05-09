package org.xedox.fluxeditor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EditorApplication implements ApplicationListener {

    private Color backgroundColor = Color.valueOf("#474747");
    private Color linesColor = Color.valueOf("#585858");
    private Color windowColor = Color.valueOf("#707070");

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private OrthographicCamera camera;
    private Viewport viewport;
    private int width = 1600, height = 720;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        camera = new OrthographicCamera();
        viewport = new FitViewport(width, height, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.zoom = 0.5f;
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void render() {
        ScreenUtils.clear(backgroundColor);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin();
        drawGrid();
        drawWindow();
        shapeRenderer.end();
    }

    private void drawGrid() {
        shapeRenderer.setColor(linesColor);
        int size = 20;

        float zoom = camera.zoom;
        float left = camera.position.x - camera.viewportWidth * zoom / 2;
        float right = camera.position.x + camera.viewportWidth * zoom / 2;
        float bottom = camera.position.y - camera.viewportHeight * zoom / 2;
        float top = camera.position.y + camera.viewportHeight * zoom / 2;

        float startX = (float) (Math.floor(left / size) * size);
        float startY = (float) (Math.floor(bottom / size) * size);

        for (float x = startX; x <= right; x += size) {
            shapeRenderer.line(x, bottom, x, top);
        }

        for (float y = startY; y <= top; y += size) {
            shapeRenderer.line(left, y, right, y);
        }
    }

    private void drawWindow() {
        shapeRenderer.setColor(windowColor);
        
        float borderThickness = 4f;
        float left = camera.position.x - width / 2;
        float right = camera.position.x + width / 2;
        float bottom = camera.position.y - height / 2;
        float top = camera.position.y + height / 2;

        shapeRenderer.rect(left, bottom, borderThickness, height);
        shapeRenderer.rect(right - borderThickness, bottom, borderThickness, height);
    
        shapeRenderer.rect(left, bottom, width, borderThickness);
        shapeRenderer.rect(left, top - borderThickness, width, borderThickness);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        if (font != null) {
            font.dispose();
        }
    }
}
