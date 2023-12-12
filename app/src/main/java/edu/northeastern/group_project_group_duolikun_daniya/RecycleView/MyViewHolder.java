package edu.northeastern.group_project_group_duolikun_daniya.RecycleView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.group_project_group_duolikun_daniya.R;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView nameView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.item_name_text_view);
    }

    public TextView getNameView() {
        return nameView;
    }
}
