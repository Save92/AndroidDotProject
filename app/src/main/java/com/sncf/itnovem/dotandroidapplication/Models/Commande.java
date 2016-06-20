package com.sncf.itnovem.dotandroidapplication.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.utils.DateUtil;

/**
 * Created by Save92 on 13/04/16.
 */
public class Commande implements Parcelable {
    private Integer id;
    private String title;
    private String description;
    private String userName = "";

    public Commande(){

    }

    public static Commande init(Integer id, JsonObject myObject) {
        String title = "";
        if(!myObject.get("title").isJsonNull()) {
            title = myObject.get("title").getAsString();
        }

        return new Commande(id, title, myObject.get("user").getAsString(), myObject.get("content").getAsString());
    }

    public Commande(Integer id, String title, String description, String userName) {
        setId(id);
        setTitle(title);
        setDescription(description);
        setUserName(userName);
    }

    public Commande(String title, String description, String userName) {
        setTitle(title);
        setDescription(description);
        setUserName(userName);
    }

    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(getId());
        out.writeString(getTitle());
        out.writeString(getDescription());
        out.writeString(getUserName());
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<Commande> CREATOR = new Creator<Commande>() {
        public Commande createFromParcel(Parcel in) {
            return new Commande(in);
        }

        public Commande[] newArray(int size) {
            return new Commande[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Commande(Parcel in) {
        setId(in.readInt());
        setTitle(in.readString());
        setDescription(in.readString());
        setUserName(in.readString());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
