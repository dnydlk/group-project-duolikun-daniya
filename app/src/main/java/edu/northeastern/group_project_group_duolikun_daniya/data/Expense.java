package edu.northeastern.group_project_group_duolikun_daniya.data;

import java.util.Map;

public class Expense {
    private String groupId;
    private double amount;
    private String description;
    private String paidBy;
    private String date;
    private Map<String, String> whoPaid;

    public Expense() {
        // Default constructor required for Firebase
    }

    public Expense(String groupId, double amount, String description, String paidBy,
                   String splitType, String date, Map<String, Double> splitDetails) {
        this.groupId = groupId;
        this.amount = amount;
        this.description = description;
        this.paidBy = paidBy;
        this.date = date;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
