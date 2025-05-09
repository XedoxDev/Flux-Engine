package org.xedox.engine;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.io.Serializable;

public interface TouchListener extends InputProcessor, Serializable {
    public static Viewport[] viewport = new Viewport[1];
    
    void touchDown(float x, float y, int pointer);
    void touchMove(float x, float y, int pointer);
    void touchUp(float x, float y, int pointer);

    @Override default boolean keyDown(int keycode) { return false; }
    @Override default boolean keyTyped(char character) { return false; }
    @Override default boolean keyUp(int keycode) { return false; }
    @Override default boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override default boolean scrolled(float amountX, float amountY) { return false; }
    @Override default boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    default boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 touch = toWorldCoordinates(screenX, screenY);
        touchDown(touch.x, touch.y, pointer);
        return false;
    }

    @Override
    default boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector2 touch = toWorldCoordinates(screenX, screenY);
        touchMove(touch.x, touch.y, pointer);
        return false;
    }

    @Override
    default boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector2 touch = toWorldCoordinates(screenX, screenY);
        touchUp(touch.x, touch.y, pointer);
        return false;
    }
    
    static Vector2 toWorldCoordinates(int x, int y) {
        if (viewport == null) throw new IllegalStateException("Viewport not initialized");
        return viewport[0].unproject(new Vector2(x, y));
    }
}