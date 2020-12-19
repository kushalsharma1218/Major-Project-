package com.example.expressblog.Models;

public class UserModelClass {
    private String Name;
    private String UserName;
    private String Email;
    private String summary;
    private String uid;
    private String ImageURI;
    private String About;
    public UserModelClass(){}

    public UserModelClass(String Name,String Email, String uName,String summary,String imgUri,String uid,String about)
    {
        this.Name = Name;
        this.Email = Email;
        this.UserName = uName;
        this.ImageURI  = imgUri;
        this.summary = summary;
        this.uid = uid;
        this.About = about;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }



    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageURI() {
        return ImageURI;
    }

    public void setImageURI(String imageURI) {
        ImageURI = imageURI;
    }
}
