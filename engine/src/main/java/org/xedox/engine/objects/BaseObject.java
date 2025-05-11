package org.xedox.engine.objects;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;

import java.lang.reflect.InvocationTargetException;

public class BaseObject {
    protected final String name;
    protected final Vector2 position;
    protected final Vector2 scale;
    protected final Vector2 size;
    protected float angle;
    protected boolean visible = true;
    protected String scriptPath;

    public BaseObject(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
        this.position = new Vector2(0, 0);
        this.scale = new Vector2(1, 1);
        this.size = new Vector2(100, 100);
        this.angle = 0;
    }

    public String getName() {
        return name;
    }

    public Vector2 getPosition() {
        return position.cpy();
    }

    public Vector2 getScale() {
        return scale.cpy();
    }

    public Vector2 getSize() {
        return size.cpy();
    }

    public float getAngle() {
        return angle;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public void setX(float x) {
        position.x = x;
    }

    public void setY(float y) {
        position.y = y;
    }

    public float getWidth() {
        return size.x;
    }

    public float getHeight() {
        return size.y;
    }

    public void setWidth(float width) {
        size.x = width;
    }

    public void setHeight(float height) {
        size.y = height;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setPosition(Vector2 position) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        this.position.set(position);
    }

    public void setScale(Vector2 scale) {
        if (scale == null) {
            throw new IllegalArgumentException("Scale cannot be null");
        }
        this.scale.set(scale);
    }

    public void setSize(Vector2 size) {
        if (size == null) {
            throw new IllegalArgumentException("Size cannot be null");
        }
        this.size.set(size);
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public float getScaleX() {
        return scale.x;
    }

    public float getScaleY() {
        return scale.y;
    }

    public boolean isTouch(float x, float y) {
    float halfWidth = size.x;
    float halfHeight = size.y;
    
    return x >= position.x - halfWidth
            && x <= position.x + halfWidth
            && y <= position.y + halfHeight 
            && y >= position.y - halfHeight;
}
}
