package org.xedox.fluxeditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import java.util.List;
import org.xedox.engine.objects.GameObject;

public class BoundsRenderer {
    private final ShapeRenderer shape;
    private Color boundsColor;
    private Color selectedColor;
    private final EditorApplication editor;

    public BoundsRenderer(EditorApplication editor) {
        this.editor = editor;
        this.shape = new ShapeRenderer();
        this.boundsColor = new Color(0, 1, 0, 0.5f);
        this.selectedColor = new Color(1, 0, 0, 0.8f);
    }

    public void render(List<GameObject> objects, Matrix4 combined) {
        if (objects == null || objects.isEmpty()) return;
        shape.setProjectionMatrix(combined);
        shape.begin(ShapeType.Line);
        
        for (GameObject obj : objects) {
            if (!obj.isVisible()) continue;
            
            shape.setColor(editor.isSelected(obj) ? selectedColor : boundsColor);
            renderObjectBounds(obj);
        }
        
        shape.end();
    }

    private void renderObjectBounds(GameObject obj) {
        float width = obj.getWidth()*2;
        float height = obj.getHeight()*2;
        float originX = width / 2; 
        float originY = height / 2;
        
        shape.rect(
            obj.getX() - originX,
            obj.getY() - originY, 
            originX,             
            originY,              
            width,               
            height,               
            obj.getScaleX(),     
            obj.getScaleY(),      
            obj.getAngle()       
        );
    }

    public Color getBoundsColor() {
        return boundsColor;
    }

    public void setBoundsColor(Color boundsColor) {
        this.boundsColor = boundsColor;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void dispose() {
        shape.dispose();
    }
}