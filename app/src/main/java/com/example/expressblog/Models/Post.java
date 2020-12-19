package com.example.expressblog.Models;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class Post {
    private String postKey;
    private String title;
    private String description;
    private String picture;
    private String userId;
    private Object timeStamp;
    private boolean requestImprove;
    private HashMap<String,String> Likes;
    private HashMap<String,String> DisLikes;

    public Post(String title, String description, String picture, String userId ,HashMap<String,String> likes,HashMap<String,String> dislikes) {
        this.title = title;
        this.description = description;
        if(picture == null){
            this.picture ="default";
        }
        else this.picture = picture;
        this.userId = userId;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.Likes = likes;
        this.DisLikes = dislikes;
        requestImprove = false;
    }


    public HashMap<String, String> getLikes() {
        return Likes;
    }

    public void setLikes(HashMap<String, String> likes) {
        Likes = likes;
    }

    public HashMap<String, String> getDisLikes() {
        return DisLikes;
    }

    public void setDisLikes(HashMap<String, String> disLikes) {
        DisLikes = disLikes;
    }

    // make sure to have an empty constructor inside ur model class
    public Post() {
    }


    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTitle() {
        return title;
    }

    public boolean isRequestImprove() {
        return requestImprove;
    }

    public void setRequestImprove(boolean requestImprove) {
        this.requestImprove = requestImprove;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }

    public String getUserId() {
        return userId;
    }


    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

}
