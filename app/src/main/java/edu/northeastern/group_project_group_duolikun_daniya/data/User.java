package edu.northeastern.group_project_group_duolikun_daniya.data;

import java.util.List;
import java.util.Map;

public class User {
    private String email;
    private Map<String, Boolean> groups;
    private String lastInteractedGroup;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, Map<String, Boolean> groups, String lastInteractedGroupID ) {
        this.email = email;
        this.groups = groups;
        this.lastInteractedGroup = lastInteractedGroupID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Boolean> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, Boolean> groups) {
        this.groups = groups;
    }

    public String getLastInteractedGroup() {
        return lastInteractedGroup;
    }

    public void setLastInteractedGroup(String lastInteractedGroup) {
        this.lastInteractedGroup = lastInteractedGroup;
    }
}
