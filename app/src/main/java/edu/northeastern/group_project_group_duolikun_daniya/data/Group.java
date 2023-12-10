package edu.northeastern.group_project_group_duolikun_daniya.data;

import java.util.HashMap;
import java.util.Map;

public class Group {
    private String groupName;
    private String groupID;
    private Map<String, Boolean> members;

    public Group() {
        // Default constructor required for calls to DataSnapshot.getValue(Group.class)
    }

    public Group(String groupName, String groupID, Map<String, Boolean> members) {
        this.groupName = groupName;
        this.groupID = groupID;
        this.members = members;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }
}
