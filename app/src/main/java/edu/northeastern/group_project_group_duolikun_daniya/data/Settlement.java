package edu.northeastern.group_project_group_duolikun_daniya.data;

import java.util.HashMap;
import java.util.Map;

public class Settlement {
    private Map<String, Double> owesTo;

    public Settlement() {
        // Default constructor required for calls to DataSnapshot.getValue(Settlement.class)
        this.owesTo = new HashMap<>();
    }

    public Map<String, Double> getOwesTo() {
        return owesTo;
    }

    public void setOwesTo(Map<String, Double> owesTo) {
        this.owesTo = owesTo;
    }
}
