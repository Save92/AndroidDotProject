package com.sncf.itnovem.dotandroidapplication.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.sncf.itnovem.dotandroidapplication.Models.Commande;
import com.sncf.itnovem.dotandroidapplication.Models.Notification;
import com.sncf.itnovem.dotandroidapplication.Models.Settings;
import com.sncf.itnovem.dotandroidapplication.Models.User;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.Call;

/**
 * Created by Save92 on 28/07/15.
 */
public interface DotService {

    @GET("ping")
    Call<JsonObject> getPing(@Query("user_token") String token, @Query("user_email") String email);

    /**
     *
     * GESTION DU LOGIN ET INSCRIPTION
     *
     */
    //Get pour le login
    //@Headers("Content-Type: application/x-www-form-urlencoded; charset=UTF-8;")
    @POST("sign_in")
    Call<JsonObject> login(@Query("email") String email, @Query("password") String password);

    //void basicLogin(Callback<User> callback);
    @POST("users")
    Call<JsonObject> createUser(@Body User newUser);
    //void createUser(@Body User user, Callback<User> callback);
    @PUT("users/{id}")
    Call<JsonObject> updateUser(@Path("id") Integer id, @Body User user, @Query("user_token") String token, @Query("user_email") String email);
    @GET("users/{id}")
    Call<JsonObject> getUser(@Path("id") Integer id, @Query("user_token") String token, @Query("user_email") String email);
    @GET("users")
    Call<JsonObject> getUsers(@Query("user_token") String token, @Query("user_email") String email);


    /**
     * Télécommande
     */
    @PUT("settings/1")
    Call<JsonObject> actionTélécommande(@Body JsonObject mySettings, @Query("user_token") String token, @Query("user_email") String email);
    @GET("settings/1")
    Call<JsonObject> getSettings(@Query("user_token") String token, @Query("user_email") String email);
    /**
     * Récupération de la liste des commandes vocales
     */
    @GET("voice_commands")
    Call<JsonObject> getListCommandes(@Query("user_token") String token, @Query("user_email") String email);

    /**
     * GESTION DES NOTIFICATIONS
     */
    @GET("reminders")
    Call<JsonObject> getListNotifications(@Query("user_token") String token, @Query("user_email") String email);

    @GET("reminders/{id}")
    Call<JsonObject> getNotifications(@Field("id") Integer id, @Query("user_token") String token, @Query("user_email") String email);

    @POST("reminders")
    Call<JsonObject> createNotification(@Body Notification notif, @Query("user_token") String token, @Query("user_email") String email);

    @DELETE("reminders/{id}")
    Call<JsonObject> updateNotification(@Field("id") Integer id, @Body Notification notif, @Query("user_token") String token, @Query("user_email") String email);


}
