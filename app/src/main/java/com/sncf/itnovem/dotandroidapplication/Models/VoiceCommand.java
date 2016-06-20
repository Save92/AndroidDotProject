package com.sncf.itnovem.dotandroidapplication.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

/**
 * Created by Save92 on 28/05/16.
 */
public class VoiceCommand  implements Parcelable {
    private String name;
    private String description;

    public VoiceCommand(){

    }

    public static VoiceCommand init(JsonObject myObject) {
        String name = "";
        if(!myObject.get("name").isJsonNull()) {
            name = myObject.get("name").getAsString();
        }

        return new VoiceCommand(name, myObject.get("description").getAsString());
    }

    public VoiceCommand(String name, String description) {
        setName(name);
        setDescription(description);
    }

    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getName());
        out.writeString(getDescription());
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Creator<VoiceCommand> CREATOR = new Creator<VoiceCommand>() {
        public VoiceCommand createFromParcel(Parcel in) {
            return new VoiceCommand(in);
        }

        public VoiceCommand[] newArray(int size) {
            return new VoiceCommand[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private VoiceCommand(Parcel in) {
        setName(in.readString());
        setDescription(in.readString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
