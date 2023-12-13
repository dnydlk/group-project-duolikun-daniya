package edu.northeastern.group_project_group_duolikun_daniya.expense_recycle_view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.group_project_group_duolikun_daniya.R;

public class ExpenseViewHolder extends RecyclerView.ViewHolder {
    TextView whatView;
    TextView howMuchView;
    TextView whoView;

    public TextView getWhatView() {
        return whatView;
    }

    public void setWhatView(TextView whatView) {
        this.whatView = whatView;
    }

    public TextView getHowMuchView() {
        return howMuchView;
    }

    public void setHowMuchView(TextView howMuchView) {
        this.howMuchView = howMuchView;
    }

    public TextView getWhoView() {
        return whoView;
    }

    public void setWhoView(TextView whoView) {
        this.whoView = whoView;
    }

    public ExpenseViewHolder(@NonNull View itemView) {
        super(itemView);
        whatView = itemView.findViewById(R.id.expense_item_what_text_view);
        howMuchView = itemView.findViewById(R.id.expense_item_how_much_text_view);
        whoView = itemView.findViewById(R.id.expense_item_who_text_view);
    }

}
