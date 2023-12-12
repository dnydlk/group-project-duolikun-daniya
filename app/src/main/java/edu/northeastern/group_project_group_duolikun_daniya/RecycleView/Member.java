package edu.northeastern.group_project_group_duolikun_daniya.RecycleView;

public class Member {
    private String name;
    private int image;

    public Member(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
