package edu.northeastern.group_project_group_duolikun_daniya.data;

import java.util.Map;

public class Expense {
    private String name;
    private double totalAmount;
    private Map<String, Double> participants;
    private boolean isEvenSplit;

    public Expense() {
        // Default constructor required for calls to DataSnapshot.getValue(Expense.class)
    }

    public Expense(String name, double totalAmount, Map<String, Double> participants, boolean isEvenSplit) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.participants = participants;
        this.isEvenSplit = isEvenSplit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Map<String, Double> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Double> participants) {
        this.participants = participants;
    }

    public boolean isEvenSplit() {
        return isEvenSplit;
    }

    public void setEvenSplit(boolean evenSplit) {
        isEvenSplit = evenSplit;
    }
}
