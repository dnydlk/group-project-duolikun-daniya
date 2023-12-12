package edu.northeastern.group_project_group_duolikun_daniya.data;

public class Member {
    private String memberName;
    private Boolean isParticipation;

    public Member(String memberName) {
        this.memberName = memberName;
        this.isParticipation = true;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Boolean getParticipation() {
        return isParticipation;
    }

    public void setParticipation(Boolean participation) {
        isParticipation = participation;
    }
}
