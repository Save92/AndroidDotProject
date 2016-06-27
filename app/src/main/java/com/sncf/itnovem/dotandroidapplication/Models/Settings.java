package com.sncf.itnovem.dotandroidapplication.Models;

import com.google.gson.JsonObject;

/**
 * Created by Journaud Nicolas on 28/05/16.
 */
public class Settings {
    private Boolean sarah_enabled;
    private Boolean twitter_enabled;
    private Boolean reminders_enabled;
    private Boolean screen_guest_enabled;
    private Boolean room_occupied;

    public static Settings init(JsonObject myObject) {


        return new Settings(myObject.get("sarah-enabled").getAsBoolean(), myObject.get("twitter-enabled").getAsBoolean(),
                myObject.get("reminders-enabled").getAsBoolean(),
                myObject.get("room-occupied").getAsBoolean(), myObject.get("screen-guest-enabled").getAsBoolean());
    }

    public Settings(Boolean sarah_enabled, Boolean twitter_enabled,Boolean reminders_enabled, Boolean room_occupied, Boolean screen_guest_enabled) {
        setSarah_enabled(sarah_enabled);
        setTwitter_enabled(twitter_enabled);
        setReminders_enabled(reminders_enabled);
        setRoom_occupied(room_occupied);
        setScreen_guest_enabled(screen_guest_enabled);
    }

    public Boolean getSarah_enabled() {
        return sarah_enabled;
    }

    public void setSarah_enabled(Boolean sarah_enabled) {
        this.sarah_enabled = sarah_enabled;
    }

    public Boolean getTwitter_enabled() {
        return twitter_enabled;
    }

    public void setTwitter_enabled(Boolean twitter_enabled) {
        this.twitter_enabled = twitter_enabled;
    }

    public Boolean getReminders_enabled() {
        return reminders_enabled;
    }

    public void setReminders_enabled(Boolean reminders_enabled) {
        this.reminders_enabled = reminders_enabled;
    }

    public Boolean getScreen_guest_enabled() {
        return screen_guest_enabled;
    }

    public void setScreen_guest_enabled(Boolean screen_guest_enabled) {
        this.screen_guest_enabled = screen_guest_enabled;
    }

    public Boolean getRoom_occupied() {
        return room_occupied;
    }

    public void setRoom_occupied(Boolean room_occupied) {
        this.room_occupied = room_occupied;
    }
}
