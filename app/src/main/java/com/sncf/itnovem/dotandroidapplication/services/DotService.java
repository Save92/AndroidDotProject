package com.sncf.itnovem.dotandroidapplication.services;

import com.google.gson.JsonObject;

import com.sncf.itnovem.dotandroidapplication.Models.Notification;
import com.sncf.itnovem.dotandroidapplication.Models.User;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.Call;

/**
 * Created by Journaud Nicolas on 28/07/15.
 */
public interface DotService {

    /**
	 *
	 * @param token
	 * @param email
	 */
	@GET("ping")
    Call<JsonObject> getPing(@Query("user_token") String token, @Query("user_email") String email);

    /**
	 * GESTION DU LOGIN ET INSCRIPTION
	 * @param email
	 * @param password
	 */
    @POST("sign_in")
    Call<JsonObject> login(@Query("email") String email, @Query("password") String password);

    /**
	 *
	 * @param newUser
	 */
	@POST("users")
    Call<JsonObject> createUser(@Body User newUser);

    /**
	 *
	 * @param id
	 * @param user
	 * @param token
	 * @param email
	 */
	@PUT("users/{id}")
    Call<JsonObject> updateUser(@Path("id") Integer id, @Body User user, @Query("user_token") String token, @Query("user_email") String email);

    /**
	 *
	 * @param id
	 * @param token
	 * @param email
	 */
	@GET("users/{id}")
    Call<JsonObject> getUser(@Path("id") Integer id, @Query("user_token") String token, @Query("user_email") String email);

    /**
	 *
	 * @param token
	 * @param email
	 */
	@GET("users")
    Call<JsonObject> getUsers(@Query("user_token") String token, @Query("user_email") String email);


    /**
	 * Télécommande
	 * @param mySettings
	 * @param token
	 * @param email
	 */
    @PUT("settings/1")
    Call<JsonObject> actionTélécommande(@Body JsonObject mySettings, @Query("user_token") String token, @Query("user_email") String email);

    /**
	 *
	 * @param token
	 * @param email
	 */
	@GET("settings/1")
    Call<JsonObject> getSettings(@Query("user_token") String token, @Query("user_email") String email);

    /**
	 *
	 * @param voice
	 * @param token
	 * @param email
	 */
	@POST("tests/voice")
    Call<JsonObject> testSarah(@Body JsonObject voice, @Query("user_token") String token, @Query("user_email") String email);

    /**
	 * Récupération de la liste des commandes vocales
	 * @param token
	 * @param email
	 */
    @GET("voice_commands")
    Call<JsonObject> getListCommandes(@Query("user_token") String token, @Query("user_email") String email);

    /**
	 * GESTION DES NOTIFICATIONS
	 * @param token
	 * @param email
	 */
    @GET("reminders")
    Call<JsonObject> getListNotifications(@Query("user_token") String token, @Query("user_email") String email);

    /**
	 *
	 * @param id
	 * @param token
	 * @param email
	 */
	@GET("reminders/{id}")
    Call<JsonObject> getNotifications(@Field("id") Integer id, @Query("user_token") String token, @Query("user_email") String email);

    /**
	 *
	 * @param notif
	 * @param token
	 * @param email
	 */
	@POST("reminders")
    Call<JsonObject> createNotification(@Body Notification notif, @Query("user_token") String token, @Query("user_email") String email);

    /**
	 *
	 * @param id
	 * @param notif
	 * @param token
	 * @param email
	 */
	@DELETE("reminders/{id}")
    Call<JsonObject> updateNotification(@Field("id") Integer id, @Body Notification notif, @Query("user_token") String token, @Query("user_email") String email);
}
