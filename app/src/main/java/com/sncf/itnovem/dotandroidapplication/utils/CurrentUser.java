package com.sncf.itnovem.dotandroidapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.Models.User;
import com.sncf.itnovem.dotandroidapplication.R;


/**
 * Created by Journaud Nicolas on 30/07/15.
 */
public class CurrentUser {
    private static final String TAG = "CURRENT_USER";

    public static final String SESSION_FILENAME = "CurrentUserSession.xml";
    public static final String USERID_KEY = "currentCurrentUserId";
    public static final String TOKEN_KEY = "token";
    public static final String USERFIRSTNAME_KEY = "CurrentUserFirstname";
    public static final String USERLASTNAME_KEY = "CurrentUserLastname";
    public static final String USERAVATAR_KEY = "CurrentUserAvatarPath";
    public static final String EMAIL_KEY = "CurrentUserEmail";
    public static final String REMEMBER_KEY = "rememberCheck";
    public static final String IS_APPROVED_KEY = "isApproved";
    public static final String ADMIN = "admin";
    public static final String PASSWORD_KEY = "password";
    public static final String RECONNECT_KEY = "reconnect";
    public static final String LOGIN_SHOW_KEY = "login_dialog";




    private static CurrentUser CurrentUserInstance = null;

    private static String email;
    private static String avatarPath;
    private static String firstName;
    private static String lastName;
    private static int currentUserId;
    private static String token;
    private static String password;
    private static Boolean rememberMe;
    private static Boolean isApproved;
    private static Boolean admin;
    private static SharedPreferences session;
    private static SharedPreferences.Editor editor;
    private static Context context;


    private CurrentUser(Context context) {
        CurrentUser.context = context.getApplicationContext();
    }

    public static CurrentUser getInstance(Context context) {
        if (CurrentUserInstance == null) {
            Class clazz = CurrentUser.class;
            synchronized (clazz) {
                CurrentUserInstance = new CurrentUser(context);
            }
        }
        return CurrentUserInstance;
    }

    public static void init(Integer id, JsonObject myObject) {
        String avatar = "";
        String firstname = "";
        String lastname = "";
        if(!myObject.get("avatar").isJsonNull()) {
            avatar = myObject.get("avatar").getAsString();
        }
        if(!myObject.get("firstname").isJsonNull()) {
            firstname = myObject.get("firstname").getAsString();
        }
        if(!myObject.get("lastname").isJsonNull()) {
            lastname = myObject.get("lastname").getAsString();
        }

        User currentUser = new User(myObject.get("email").toString(), myObject.get("approved").getAsBoolean(), avatar,
                firstname, lastname,myObject.get("token").toString(),
                myObject.get("admin").getAsBoolean(), id);

        setCurrentUser(currentUser);
    }

    public static void setCurrentUser(User user){
        CurrentUser.setCurrentUserId(user.getUserId());
        CurrentUser.setEmail(user.getEmail().replaceAll("\"", ""));
        CurrentUser.setFirstName(user.getFirstname().replaceAll("\"", ""));
        CurrentUser.setLastName(user.getLastname().replaceAll("\"", ""));
        CurrentUser.setCurrentUserId(user.getUserId());
        CurrentUser.setToken(user.getToken().replaceAll("\"", ""));
        if(user.getPassword() != null) {
            CurrentUser.setPassword(user.getPassword().replaceAll("\"", ""));

        }
        CurrentUser.setIsApproved(user.getApproved());
        CurrentUser.setAdmin(user.getAdmin());
    }

    public static void saveCurrentUserSession() {
        session = context.getSharedPreferences(SESSION_FILENAME, 0);
        editor = session.edit();
        // ID du CurrentUser récupéré de l'api n'est pas bon
        if (CurrentUser.getCurrentUserId() == 0)
            return;

        // Mise à jour des Préférences si différent de l'existant
        if (session.getInt(USERID_KEY, 0) != CurrentUser.getCurrentUserId()) {
            if (CurrentUser.getEmail() != null && !CurrentUser.getEmail().isEmpty())
                editor.putString(EMAIL_KEY, CurrentUser.getEmail().replaceAll("\"", ""));
            if (CurrentUser.getCurrentUserId() != 0)
                editor.putInt(USERID_KEY, CurrentUser.getCurrentUserId());
            if (CurrentUser.getLastName() != null && !CurrentUser.getLastName().isEmpty())
                editor.putString(USERLASTNAME_KEY, CurrentUser.getLastName().replaceAll("\"", ""));
            if (CurrentUser.getFirstName() != null && !CurrentUser.getFirstName().isEmpty())
                editor.putString(USERFIRSTNAME_KEY, CurrentUser.getFirstName().replaceAll("\"", ""));
            if (CurrentUser.getToken() != null && !CurrentUser.getToken().isEmpty())
                editor.putString(TOKEN_KEY, CurrentUser.getToken().replaceAll("\"", ""));

            if(CurrentUser.getAvatarPath() != null && !CurrentUser.getAvatarPath().isEmpty())
                editor.putString(USERAVATAR_KEY, CurrentUser.getAvatarPath().replaceAll("\"", ""));
            if(CurrentUser.getRememberMe() != null) {
                editor.putBoolean(REMEMBER_KEY, CurrentUser.getRememberMe());
            }
            if(CurrentUser.getIsApproved() != null) {
                editor.putBoolean(IS_APPROVED_KEY, CurrentUser.getIsApproved());
            }
            if(CurrentUser.getAdmin() != null) {
                editor.putBoolean(ADMIN, CurrentUser.getAdmin());
            }
            editor.apply();
        }
    }

    public static Boolean getUserByPreferences() {

        session = CurrentUser.context.getSharedPreferences(SESSION_FILENAME, 0);
        Boolean error = true;
        if(session.getInt(USERID_KEY, 0) == 0) {
            error = false;
        } else {
            CurrentUser.setCurrentUserId(session.getInt(USERID_KEY, 0));
        }
        if(session.getString(EMAIL_KEY, "").equals("")) {
            error = false;
        } else {
            CurrentUser.setEmail(session.getString(EMAIL_KEY, ""));
        }
        if(session.getString(TOKEN_KEY, "").equals("")) {
            error = false;
        } else {

            CurrentUser.setToken(session.getString(TOKEN_KEY, ""));
        }
        if(session.getString(USERLASTNAME_KEY, "").equals("")) {
            error = false;
        } else {
            CurrentUser.setLastName(session.getString(USERLASTNAME_KEY, ""));
        }
        if(session.getString(USERFIRSTNAME_KEY, "").equals("")) {
            error = false;
        } else {
            CurrentUser.setFirstName(session.getString(USERFIRSTNAME_KEY, ""));
        }
        if(session.getString(USERAVATAR_KEY, "").equals("")) {
            error = false;
        } else {
            CurrentUser.setAvatarPath(session.getString(USERAVATAR_KEY, ""));
        }
        if(!session.getBoolean(REMEMBER_KEY, false)) {
            error = false;
        } else {
            CurrentUser.setRememberMe(session.getBoolean(REMEMBER_KEY, false));
        }
        if(!session.getBoolean(IS_APPROVED_KEY, false)) {
            error = false;
        } else {
            CurrentUser.setIsApproved(session.getBoolean(IS_APPROVED_KEY, false));
        }
        if(!session.getBoolean(ADMIN, false)) {
            error = false;
        } else {
            CurrentUser.setAdmin(session.getBoolean(ADMIN, false));
        }
        if (CurrentUser.getEmail().isEmpty() || CurrentUser.getToken().isEmpty()) {
            error = false;
            editor.putInt(USERID_KEY, 0);
            editor.apply();
        }
        return  error;
    }


    public static void saveRemember(Boolean checked) {
        session = context.getSharedPreferences(SESSION_FILENAME, 0);
        editor = session.edit();
        editor.putBoolean(REMEMBER_KEY, checked);
        editor.apply();
    }

    public static void savePassword(String pass) {
        session = context.getSharedPreferences(SESSION_FILENAME, 0);
        editor = session.edit();
        editor.putString(PASSWORD_KEY, pass);
        editor.apply();
    }

    public static String getSavedPassword() {
        session = context.getSharedPreferences(SESSION_FILENAME, 0);
        return session.getString(PASSWORD_KEY, "");
    }

    public static RoundedAvatarDrawable getCurrentAvatar(int size) {
        if (CurrentUser.getAvatarPath() != null && CurrentUser.getAvatarPath().length() != 0) {
            Uri uriLoad = Uri.parse("file://" + CurrentUser.getAvatarPath());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriLoad);
                int maxWidth = bitmap.getWidth();
                int maxHeight = bitmap.getHeight();
                if (maxHeight > size) {
                    maxHeight = size;
                }
                if (maxWidth > size) {
                    maxWidth = size;
                }
                RoundedAvatarDrawable img = new RoundedAvatarDrawable(Images.scaleCenterCrop(bitmap, maxHeight, maxWidth));
                return img;
            } catch (IOException e) {
            }
        }
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_account_white_24dp);
        RoundedAvatarDrawable logo = new RoundedAvatarDrawable(Images.scaleCenterCrop(icon, size, size));
        return logo;
    }

    public static Boolean awake() {
        session = CurrentUser.context.getSharedPreferences(SESSION_FILENAME, 0);

        if(session.getBoolean(REMEMBER_KEY, false)) {
            if(CurrentUser.getUserByPreferences()) {
                return true;
            }
        }
        return false;
    }


    public static Boolean getIsApproved() {
        return isApproved;
    }

    public static void setIsApproved(Boolean isApproved) {
        CurrentUser.isApproved = isApproved;
    }

    public static Boolean getRememberMe() {
        return rememberMe;
    }

    public static void setRememberMe(Boolean rememberMe) {
        CurrentUser.rememberMe = rememberMe;
    }
    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        CurrentUser.password = password;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        CurrentUser.email = email;
    }

    public static String getAvatarPath() {
        return avatarPath;
    }

    public static void setAvatarPath(String avatarPath) {
        CurrentUser.avatarPath = avatarPath;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstName) {
        CurrentUser.firstName = firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        CurrentUser.lastName = lastName;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUserId(int currentUserId) {
        CurrentUser.currentUserId = currentUserId;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        CurrentUser.token = token;
    }

    public static Boolean getAdmin() {
        return admin;
    }

    public static void setAdmin(Boolean admin) {
        CurrentUser.admin = admin;
    }
}
