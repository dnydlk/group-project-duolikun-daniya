package edu.northeastern.group_project_group_duolikun_daniya.data;

import java.util.Map;

public class Expense {
    private String groupId;
    private double amount;
    private String description;
    private String paidBy;
    private String splitType;
    private String date;
    private Map<String, Double> splitDetails;

    public Expense() {
        // Default constructor required for Firebase
    }

    public Expense(String groupId, double amount, String description, String paidBy, String splitType, String date, Map<String, Double> splitDetails) {
        this.groupId = groupId;
        this.amount = amount;
        this.description = description;
        this.paidBy = paidBy;
        this.splitType = splitType;
        this.date = date;
        this.splitDetails = splitDetails;
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

    public String getSplitType() {
        return splitType;
    }

    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, Double> getSplitDetails() {
        return splitDetails;
    }

    public void setSplitDetails(Map<String, Double> splitDetails) {
        this.splitDetails = splitDetails;
    }
}
