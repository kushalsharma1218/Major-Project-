package com.example.expressblog.Models;

public class PostModel {
    private String PostID;
    public PostModel(){}
    public PostModel(String id)
    {
        this.PostID = id;
    }

    public String getPostID() {
        return PostID;
    }

    public void setPostID(String postID) {
        this.PostID = postID;
    }
}
