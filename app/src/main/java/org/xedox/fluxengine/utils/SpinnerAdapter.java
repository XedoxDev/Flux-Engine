package org.xedox.fluxengine.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.xedox.fluxengine.R;

public class SpinnerAdapter extends RecyclerView.Adapter<SpinnerAdapter.ViewHolder> {
    private List<Item> items;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public static class Item {
        private String title;

        public Item(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public SpinnerAdapter() {
        this.items = new ArrayList<>();
    }

    public SpinnerAdapter(List<Item> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spinner_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.textView.setText(item.getTitle());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position, item);
            }
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                return longClickListener.onItemLongClick(position, item);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(Item item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void add(int position, Item item) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void addAll(List<Item> items) {
        int startPosition = this.items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(startPosition, items.size());
    }

    public void remove(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(Item item) {
        int position = items.indexOf(item);
        if (position != -1) {
            remove(position);
        }
    }

    public void clear() {
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void update(int position, Item item) {
        items.set(position, item);
        notifyItemChanged(position);
    }

    public void setItems(List<Item> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }
    
    public int getPosition(Item item) {
        for(int i= 0; i< getItemCount(); i++) {
        	if(items.get(i).getTitle().equals(item.getTitle())) return i;
        }
        return -1;
    }

    public void setItems(Item[] newItems) {
        items.clear();
        items.addAll(Arrays.asList(newItems));
        notifyDataSetChanged();
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_text);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Item item);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(int position, Item item);
    }
}