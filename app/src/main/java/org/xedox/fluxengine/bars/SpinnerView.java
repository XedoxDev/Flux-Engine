package org.xedox.fluxengine.bars;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.xedox.fluxengine.R;
import org.xedox.fluxengine.utils.SpinnerAdapter;

public class SpinnerView extends FrameLayout {
    private RecyclerView recyclerView;
    private OnItemSelectedListener itemSelectedListener;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;
    private boolean isDropdownVisible = false;
    private TextView selectedTextView;
    private PopupWindow popupWindow;
    
    public SpinnerAdapter adapter;

    public SpinnerView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public SpinnerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpinnerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.spinner_layout, this, true);
        
        selectedTextView = findViewById(R.id.selected);
        
        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        
        popupWindow = new PopupWindow(recyclerView, 
                                    LayoutParams.WRAP_CONTENT, 
                                    LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(context.getDrawable(R.drawable.spinner_dropdown_bg));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        
        this.adapter = new SpinnerAdapter();
        recyclerView.setAdapter(adapter);
        
        adapter.setOnItemClickListener((position, item) -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(this, position, item);
            } else {
                performDefaultItemClick(position, item);
            }
        });
        
        adapter.setOnItemLongClickListener((position, item) -> {
            if (itemLongClickListener != null) {
                return itemLongClickListener.onItemLongClick(this, position, item);
            }
            return false;
        });
        
        setOnClickListener(v -> toggleDropdown());
        popupWindow.setOnDismissListener(() -> isDropdownVisible = false);
    }

    public void performDefaultItemClick(int position, SpinnerAdapter.Item item) {
        setSelectedItem(position, item);
        hideDropdown();
        
        if (itemSelectedListener != null) {
            itemSelectedListener.onItemSelected(position, item);
        }
    }

    public void setSelectedItem(int position) {
        SpinnerAdapter.Item item = adapter.getItem(position);
        setSelectedItem(position, item);
    }

    public void setSelectedItem(int position, SpinnerAdapter.Item item) {
        if (selectedTextView != null && item != null) {
            selectedTextView.setText(item.getTitle());
        }
    }

    public void setSelectedItem(SpinnerAdapter.Item item) {
        int position = adapter.getPosition(item);
        if (position != -1) {
            setSelectedItem(position, item);
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.itemSelectedListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public void showDropdown() {
        if (!isDropdownVisible) {
            recyclerView.measure(
                MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            
            popupWindow.showAsDropDown(this, 0, 0, Gravity.START);
            popupWindow.setWidth(getWidth());
            isDropdownVisible = true;
        }
    }

    public void hideDropdown() {
        if (isDropdownVisible) {
            popupWindow.dismiss();
            isDropdownVisible = false;
        }
    }

    public void toggleDropdown() {
        if (isDropdownVisible) {
            hideDropdown();
        } else {
            showDropdown();
        }
    }

    public SpinnerAdapter getAdapter() {
        return adapter;
    }

    public boolean isDropdownVisible() {
        return isDropdownVisible;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position, SpinnerAdapter.Item item);
    }

    public interface OnItemClickListener {
        void onItemClick(SpinnerView spinner, int position, SpinnerAdapter.Item item);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(SpinnerView spinner, int position, SpinnerAdapter.Item item);
    }
}