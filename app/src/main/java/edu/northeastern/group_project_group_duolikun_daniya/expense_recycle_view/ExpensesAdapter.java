package edu.northeastern.group_project_group_duolikun_daniya.expense_recycle_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.group_project_group_duolikun_daniya.R;
import edu.northeastern.group_project_group_duolikun_daniya.member_recycle_view.MemberViewHolder;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpenseViewHolder> {
    List<ExpenseItem> expensesList;

    public ExpensesAdapter(List<ExpenseItem> expensesList) {
        this.expensesList = expensesList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_item_view, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.getWhatView().setText(expensesList.get(position).getWhat());
        holder.getHowMuchView().setText(expensesList.get(position).getHowMuch());
        holder.getWhoView().setText(expensesList.get(position).getWho());
    }

    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    public void updateData(List<ExpenseItem> newExpensesList) {
        expensesList = newExpensesList;
        notifyDataSetChanged();
    }
}
