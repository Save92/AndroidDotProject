package com.sncf.itnovem.dotandroidapplication.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by Journaud Nicolas on 28/05/16.
 */
public class VoiceCommand  implements Parcelable {
    private String name;
    private ArrayList<String> description;

    public VoiceCommand(){

    }

    public static VoiceCommand init(JsonObject myObject) {
        String name = "";
        if(!myObject.get("name").isJsonNull()) {
            name = myObject.get("name").getAsString();
        }
        ArrayList<String> descriptions = new ArrayList<>();
        JsonArray descriptionsJson = new JsonArray();
        if(myObject.get("description").isJsonArray()) {
            descriptionsJson = myObject.get("description").getAsJsonArray();
        } else {
            descriptionsJson.add(myObject.get("description").getAsString());
        }
            for (int i = 0; i < descriptionsJson.size(); i++) {
            if(!descriptionsJson.get(i).isJsonNull())
                descriptions.add(descriptionsJson.get(i).getAsString());
        }
        return new VoiceCommand(name, descriptions);
    }

    public VoiceCommand(String name, ArrayList<String> description) {
        setName(name);
        setDescription(description);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getName());
        out.writeStringList(getDescription());
    }

    public static final Creator<VoiceCommand> CREATOR = new Creator<VoiceCommand>() {
        public VoiceCommand createFromParcel(Parcel in) {
            return new VoiceCommand(in);
        }

        public VoiceCommand[] newArray(int size) {
            return new VoiceCommand[size];
        }
    };

    private VoiceCommand(Parcel in) {
        setName(in.readString());
        in.readStringList(description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }
}
