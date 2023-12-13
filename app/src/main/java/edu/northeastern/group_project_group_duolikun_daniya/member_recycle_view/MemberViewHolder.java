package edu.northeastern.group_project_group_duolikun_daniya.member_recycle_view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.group_project_group_duolikun_daniya.R;

public class MemberViewHolder extends RecyclerView.ViewHolder {
    TextView nameView;
    public MemberViewHolder(@NonNull View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.expense_item_who_text_view);
    }

    public TextView getNameView() {
        return nameView;
    }
}
