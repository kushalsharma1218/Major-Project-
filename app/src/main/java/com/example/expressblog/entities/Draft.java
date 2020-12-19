package com.example.expressblog.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import  androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Drafts")
public class Draft implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name="title")
    private String title;

    @ColumnInfo(name = "date_time")
    private String dateTime;

    @ColumnInfo(name = "desc")
    private String desc;

    @ColumnInfo(name = "image_path")
    private String imagePath;

//    @ColumnInfo(name = "web_link")
//    private String webLink;

//    @ColumnInfo(name="tags")
//    private String tags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

//    public String getWebLink() {
//        return webLink;
//    }
//
//    public void setWebLink(String webLink) {
//        this.webLink = webLink;
//    }

//    public String getTags() {
//        return tags;
//    }
//
//    public void setTags(String tags) {
//        this.tags = tags;
//    }

    @NonNull
    @Override
    public String toString() {
        return title +":"+dateTime;
    }
}
