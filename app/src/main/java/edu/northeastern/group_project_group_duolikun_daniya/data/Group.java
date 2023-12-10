package edu.northeastern.group_project_group_duolikun_daniya.data;

import java.util.HashMap;
import java.util.Map;

public class Group {
    private String name;
    private Map<String, Boolean> participants;
    private Map<String, Expense> expenses;

    public Group() {
        // Default constructor required for calls to DataSnapshot.getValue(Group.class)
        this.participants = new HashMap<>();
        this.expenses = new HashMap<>();
    }

    // Create a new group with the given name
    public Group(String name) {
        this.name = name;
        this.participants = new HashMap<>();
        this.expenses = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Boolean> participants) {
        this.participants = participants;
    }

    public Map<String, Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(Map<String, Expense> expenses) {
        this.expenses = expenses;
    }
}
