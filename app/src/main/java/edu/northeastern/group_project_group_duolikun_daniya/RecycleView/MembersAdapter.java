package edu.northeastern.group_project_group_duolikun_daniya.RecycleView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.group_project_group_duolikun_daniya.R;

public class MembersAdapter extends RecyclerView.Adapter<MyViewHolder> {
    List<String> membersList;

    public MembersAdapter(List<String> membersList) {
        this.membersList = membersList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nameView.setText(membersList.get(position));
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    public void updateData(List<String> newMembersList) {
        membersList = newMembersList;
        notifyDataSetChanged();
    }
}
