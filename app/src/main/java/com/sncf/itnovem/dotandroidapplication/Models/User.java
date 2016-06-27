package com.sncf.itnovem.dotandroidapplication.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;

/**
 * Created by Journaud Nicolas on 27/01/16.
 */
public class User implements Parcelable {

    private String email;
    private String password;
    private Boolean approved;
    private String avatar;
    private String firstname;
    private String lastname;
    private Integer userId;
    private String token;
    private Boolean admin;

    public User() {
    }


    public static User init(Integer id, JsonObject myObject) {
        String email = "";
        String avatar = "";
        String first = "";
        String last = "";
        String token = "";
        if(!myObject.get("email").isJsonNull()) {
            email = myObject.get("email").getAsString();
        }
        if(!myObject.get("avatar").isJsonNull()) {
            avatar = myObject.get("avatar").getAsString();
        }
        if(!myObject.get("firstname").isJsonNull()) {
            first = myObject.get("firstname").getAsString();
        }
        if(!myObject.get("lastname").isJsonNull()) {
            last = myObject.get("lastname").getAsString();
        }
        if(!myObject.get("token").isJsonNull()) {
            token = myObject.get("token").getAsString();
        }


        return new User(email, myObject.get("approved").getAsBoolean(), avatar, first, last,
                token, myObject.get("admin").getAsBoolean(), id);
    }

    public User(CurrentUser current) {
        this.email = current.getEmail();
        if(!current.getPassword().isEmpty()) {
            this.password = current.getPassword();
        }
        this.avatar = current.getAvatarPath();
        this.approved = true;
        this.firstname = current.getFirstName();
        this.lastname = current.getLastName();
        this.userId = current.getCurrentUserId();
        this.token = current.getToken();
        this.admin = current.getAdmin();

    }

    public User(String email, Boolean approved, String avatarPath,
                String firstname, String lastname, Integer userId, String token, Boolean admin ) {
        if(email.length() > 0 && approved != null && token.length() > 0 && userId != null) {
            this.email = email;
            if(avatarPath.length() > 0) {
                this.avatar = avatarPath;
            }
            if(lastname.length() > 0) {
                this.lastname = lastname;
            }
            if(firstname.length() > 0) {
                this.firstname = firstname;
            }
            this.approved = approved;
            this.token = token;
            this.setUserId(userId);
            this.setAdmin(admin);
        }
    }

    public User(String email,Boolean approved, String avatarPath,
                String firstname, String lastname,String token, Boolean admin, Integer userId  ) {
        if(email.length() > 0 && approved != null && token.length() > 0 && userId != null) {
            this.email = email;
            if(avatarPath.length() > 0) {
                this.avatar = avatarPath;
            }
            if(lastname.length() > 0) {
                this.lastname = lastname;
            }
            if(firstname.length() > 0) {
                this.firstname = firstname;
            }
            this.approved = approved;
            this.token = token;
            this.setUserId(userId);
            this.setAdmin(admin);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getEmail());
        out.writeString(getPassword());
        out.writeByte((byte) (getApproved() ? 1 : 0));
        out.writeString(getAvatarPath());
        out.writeString(getFirstname());
        out.writeString(getLastname());
        out.writeInt(getUserId());
        out.writeString(getToken());
        out.writeInt((byte) (getAdmin() ? 1 : 0));
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        setEmail(in.readString());
        setPassword(in.readString());
        setApproved(in.readByte() != 0);
        setAvatarPath(in.readString());
        setFirstname(in.readString());
        setLastname(in.readString());
        setUserId(in.readInt());
        setToken(in.readString());
        setAdmin(in.readByte() != 0);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        String pass = "";
        if(password != null && !password.isEmpty()) {
            pass = password.replaceAll("\"", "");
        }

        return pass;
    }

    public void setPassword(String password) {
        if(password != null &&!password.isEmpty()) {
            this.password = password.replaceAll("\"", "");
        }
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getAvatarPath() {
        return avatar;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatar = avatar;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
