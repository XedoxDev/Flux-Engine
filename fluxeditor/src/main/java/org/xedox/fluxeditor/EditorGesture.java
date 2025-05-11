package org.xedox.fluxeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import org.xedox.engine.objects.GameObject;

public class EditorGesture implements GestureDetector.GestureListener {

    private final EditorApplication editor;
    private GameObject selected;
    private boolean moveObject = false;

    public EditorGesture(EditorApplication editor) {
        this.editor = editor;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Vector3 worldCoords = editor.screenToWorld(x, y);
        if (selected != null && selected.isTouch(worldCoords.x, worldCoords.y)) {
            moveObject = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Vector3 worldCoords = editor.screenToWorld(x, y);

        for (GameObject obj : editor.getScene().getObjects()) {
            if (obj.isTouch(worldCoords.x, worldCoords.y)) {
                selected = obj;
                return true;
            }
        }

        selected = null;
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        float zoom = editor.getCameraZoom();
        if (moveObject && selected != null) {
            selected.setX(selected.getX() + deltaX * zoom);
            selected.setY(selected.getY() - deltaY * zoom);
            return true;
        } else {
            editor.addCameraPosition(-deltaX * zoom, deltaY * zoom);
            return true;
        }
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        moveObject = false;
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(
            Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        float initDist = initialPointer1.dst(initialPointer2);
        float dist = pointer1.dst(pointer2);
        float factor = initDist / dist;

        float smoothingFactor = 0.5f;
        factor = 1 + (factor - 1) * smoothingFactor;

        float currentZoom = editor.getCameraZoom();
        editor.zoomCamera(currentZoom * factor);
        return true;
    }

    @Override
    public void pinchStop() {}

    public GameObject getSelected() {
        return selected;
    }

    public void setSelected(GameObject selected) {
        this.selected = selected;
    }

    public boolean isMovingObject() {
        return moveObject;
    }
}
