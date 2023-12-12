package edu.northeastern.group_project_group_duolikun_daniya.data;

public class Transaction {
    private String payerId;
    private String receiverId;
    private double amount;
    private String groupId;
    private String date;
    private String status;

    public Transaction() {
        // Default constructor required for Firebase
    }

    public Transaction(String payerId, String receiverId, double amount, String groupId,
                       String date, String status) {
        this.payerId = payerId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.groupId = groupId;
        this.date = date;
        this.status = status;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
