package edu.northeastern.group_project_group_duolikun_daniya.data;

import java.util.List;

public class User {
    private String email;
    private List<String> groups;
    private String lastInteractedGroup;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, List<String> groups, String lastInteractedGroup) {
        this.email = email;
        this.groups = groups;
        this.lastInteractedGroup = null;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getGroups() {
        return groups;
    }

    public String getLastInteractedGroup() {
        return lastInteractedGroup;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public void setLastInteractedGroup(String lastInteractedGroup) {
        this.lastInteractedGroup = lastInteractedGroup;
    }
}
