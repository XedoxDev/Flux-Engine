package org.xedox.fluxengine.bars;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import org.xedox.fluxengine.R;

import java.util.ArrayList;
import java.util.List;

public class TransformSelectorView extends LinearLayout {

    private List<Item> items = new ArrayList<>();
    private int selectedIndex = -1;
    private OnItemSelectedListener listener;

    public TransformSelectorView(Context context) {
        super(context);
        init();
    }

    public TransformSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.transporm_selector_background);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.RIGHT);
    }

    public void addItem(Item item) {
        items.add(item);
        createButtonForItem(items.size() - 1);
    }

    public void setItems(List<Item> items) {
        this.items.clear();
        this.items.addAll(items);
        removeAllViews();
        for (int i = 0; i < items.size(); i++) {
            createButtonForItem(i);
        }
    }

    private void createButtonForItem(final int index) {
        Item item = items.get(index);
        ImageButton button = (ImageButton)LayoutInflater.from(getContext()).inflate(R.layout.transform_selector_item, this, false);
        button.setImageResource(item.iconResId);
        button.setBackgroundResource(R.drawable.selector_button_bg);
        button.setTag(index);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 10);
        button.setLayoutParams(params);
        
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedIndex(index);
                if (listener != null) {
                    listener.onItemSelected(index, items.get(index));
                }
            }
        });
        
        addView(button);
        updateButtonStates();
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < items.size() && selectedIndex != index) {
            selectedIndex = index;
            updateButtonStates();
        }
    }

    private void updateButtonStates() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ImageButton) {
                child.setSelected(i == selectedIndex);
            }
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public static class Item {
        private String id;
        private int iconResId;

        public Item(String id, int iconResId) {
            this.id = id;
            this.iconResId = iconResId;
        }

        public String getId() {
            return id;
        }

        public int getIconResId() {
            return iconResId;
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position, Item item);
    }
}