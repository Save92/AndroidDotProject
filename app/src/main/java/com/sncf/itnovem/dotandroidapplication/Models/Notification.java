package com.sncf.itnovem.dotandroidapplication.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.Streams;
import com.sncf.itnovem.dotandroidapplication.utils.DateUtil;

/**
 * Created by Save92 on 24/03/16.
 */
// Rendre parcelable
public class Notification implements Parcelable{
    public static String TAG = "NOTIFMODEL";

    private Integer id;
    private String title;
    private String displayed_at;
    private String type;
    private String user;
    private Integer user_id;
    private String content;
    private Integer duration;
    private Integer priority;
    private String createdAt;
    private Boolean displayed;
    private String displayed_ago;

    public Notification() {

    }

    public static Notification init(Integer id, JsonObject myObject) {
        String title = "";
        String displayDate = "";
        String displayAgo = "";
        if(!myObject.get("title").isJsonNull()) {
            title = myObject.get("title").getAsString();
        }
        if(!myObject.get("displayed-at").isJsonNull()) {
            displayDate =  myObject.get("displayed-at").getAsString();
        }
        if(!myObject.get("displayed-ago").isJsonNull()) {
            displayAgo =  myObject.get("displayed-ago").getAsString();
        }

        return new Notification(id, title, displayDate,
                myObject.get("type").getAsString(), myObject.get("user").getAsString(), myObject.get("content").getAsString(),
                myObject.get("duration").getAsInt(),myObject.get("created-at").getAsString(), myObject.get("priority").getAsInt(),
                myObject.get("displayed").getAsBoolean(), displayAgo);
    }

    public Notification(String title,String dateNotif, String type, Integer ownerId, String description, Integer duration, Integer priority) {
        if(title == null) {
            this.setTitle("");
        } else {

            this.setTitle(title);
        }
        Log.v(TAG, "DATE : " +dateNotif);
        if(dateNotif == null) {
            this.setDisplayAt("");
        } else {
            this.setDisplayAt(DateUtil.parseForJsonDate(dateNotif));
            //this.setDisplayAt(DateUtil.dateFormatLocal.format(dateNotif));
            //this.setDisplayAt(dateNotif);
        }
        this.setType(type);
        this.setUser_id(ownerId);
        this.setContent(description);
        this.setDuration(duration);
        this.setPriority(priority);

    }

    public Notification(String title,String dateNotif, String type, String owner, String description, Integer duration, String dateAdded, Integer priority) {
        if(title == null) {
            this.setTitle("");
        } else {

            this.setTitle(title);
        }
        if(dateNotif == null) {
            this.setDisplayAt("");
        } else {
            //this.setDisplayAt(DateUtil.dateFormatLocal.format(dateNotif));
            this.setDisplayAt(dateNotif);
        }
        this.setType(type);
        this.setUser(owner);
        this.setContent(description);
        this.setDuration(duration);
//        this.setCreatedAt(DateUtil.dateFormatLocal.format(dateAdded));
        this.setCreatedAt(dateAdded);
        this.setPriority(priority);

    }

    public Notification(Integer id, String title,String dateNotif, String type, String owner, String description, Integer duration, String dateAdded, Integer priority,Boolean displayed, String displayed_ago) {
        this.setId(id);
        this.setTitle(title);
        if(dateNotif.equalsIgnoreCase("")) {
            this.setDisplayAt("");
        } else {
            this.setDisplayAt(DateUtil.parseJsonDate(dateNotif));
        }
        this.setType(type);
        this.setUser(owner);
        this.setContent(description);
        this.setDuration(duration);
        this.setCreatedAt(DateUtil.parseJsonDate(dateAdded));
        this.setPriority(priority);

        this.setDisplayed(displayed);
        this.setDisplayed_ago(displayed_ago);

    }


    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(getId());
        out.writeString(getTitle());
        out.writeString(getType());
        out.writeString(getDisplayAt());
        out.writeString(getUser());
        out.writeString(getContent());
        out.writeInt(getDuration());
        out.writeString(getCreatedAt());
        out.writeInt(getPriority());
        out.writeByte((byte) (getDisplayed() ? 1 : 0));
        out.writeString(getDisplayed_ago());
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Notification(Parcel in) {
        setId(in.readInt());
        setTitle(in.readString());
        setType(in.readString());
        setDisplayAt(in.readString());
        setUser(in.readString());
        setContent(in.readString());
        setDuration(in.readInt());
        setCreatedAt(in.readString());
        setPriority(in.readInt());
        setDisplayed(in.readByte() != 0);
        setDisplayed_ago(in.readString());

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }


    public String getDisplayAt() {
        return displayed_at;
    }

    public void setDisplayAt(String displayAt) {
        this.displayed_at = displayAt;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer userId) {
        this.user_id = userId;
    }

    public Boolean getDisplayed() {
        return displayed;
    }

    public void setDisplayed(Boolean displayed) {
        this.displayed = displayed;
    }

    public String getDisplayed_ago() {
        return displayed_ago;
    }

    public void setDisplayed_ago(String displayed_ago) {
        this.displayed_ago = displayed_ago;
    }
}
