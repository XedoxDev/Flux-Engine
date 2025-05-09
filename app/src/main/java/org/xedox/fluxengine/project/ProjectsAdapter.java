package org.xedox.fluxengine.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.xedox.fluxengine.R;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.VH> {

    private List<Project> projects;
    private Context context;
    private OnProjectClickListener clickListener;

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    public interface OnProjectMenuClickListener {
        void onProjectMenuClick(View view, Project project);
    }

    public ProjectsAdapter(Context context) {
        this.context = context;
        this.projects = new ArrayList<>();
    }

    public void setOnProjectClickListener(OnProjectClickListener listener) {
        this.clickListener = listener;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
        notifyDataSetChanged();
    }

    public void addProject(Project project) {
        this.projects.add(project);
        notifyItemInserted(projects.size() - 1);
    }

    public void removeProject(int position) {
        this.projects.remove(position);
        notifyItemRemoved(position);
    }
    
    public void removeProject(Project project) {
        int index = projects.indexOf(project);
        this.projects.remove(index);
        notifyItemRemoved(index);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.project_layout, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Project project = projects.get(position);

        holder.name.setText(project.getName());
        holder.path.setText(project.getPath());

        if (project.getIcon() != null) {
            holder.icon.setImageDrawable(project.getIcon());
        }

        holder.itemView.setOnClickListener(
                v -> {
                    if (clickListener != null) {
                        clickListener.onProjectClick(project);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView path;
        ImageButton more;

        public VH(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            path = itemView.findViewById(R.id.path);
            more = itemView.findViewById(R.id.more);
        }
    }
}
