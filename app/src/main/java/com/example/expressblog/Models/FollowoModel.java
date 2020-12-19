package com.example.expressblog.Models;

import androidx.loader.content.Loader;

public class FollowoModel
{
    private String uid;
    public FollowoModel(){}

    public FollowoModel(String uid)
    {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
