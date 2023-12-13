package edu.northeastern.group_project_group_duolikun_daniya.expense_recycle_view;

public class ExpenseItem {
    private String who;
    private String howMuch;
    private String what;
    public ExpenseItem(String what, String howMuch, String who) {
        this.what = what;
        this.howMuch = howMuch;
        this.who = who;
    }

    public ExpenseItem() {
    }


    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getHowMuch() {
        return howMuch;
    }

    public void setHowMuch(String howMuch) {
        this.howMuch = howMuch;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    @Override
    public String toString() {
        return "ExpenseItem{" +
                "who='" + who + '\'' +
                ", howMuch='" + howMuch + '\'' +
                ", what='" + what + '\'' +
                '}';
    }
}
