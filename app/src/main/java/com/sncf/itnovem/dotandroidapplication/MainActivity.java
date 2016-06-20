package com.sncf.itnovem.dotandroidapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sncf.itnovem.dotandroidapplication.Adapters.ListCommandRecyclerAdapter;
import com.sncf.itnovem.dotandroidapplication.Adapters.RecyclerAdapter;
import com.sncf.itnovem.dotandroidapplication.Adapters.SimpleDividerItemDecoration;
import com.sncf.itnovem.dotandroidapplication.Models.Commande;
import com.sncf.itnovem.dotandroidapplication.Models.Notification;
import com.sncf.itnovem.dotandroidapplication.Models.User;
import com.sncf.itnovem.dotandroidapplication.services.API;
import com.sncf.itnovem.dotandroidapplication.services.APIError;
import com.sncf.itnovem.dotandroidapplication.services.DotService;
import com.sncf.itnovem.dotandroidapplication.services.ErrorUtils;
import com.sncf.itnovem.dotandroidapplication.services.ServiceGenerator;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;
import com.sncf.itnovem.dotandroidapplication.utils.SecurityUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Save92 on 27/01/16.
 */
public class MainActivity extends Activity implements RecyclerAdapter.CallbackAdapter {


    static public String TAG = "LOGINMAIN";

    private Toolbar toolbar;
    private Toolbar bottomtoolbar;
    private Activity activity;
    private ImageButton telecommandeBtn;
    private ImageButton listBtn;
    private ImageButton notificationsBtn;
    private List<Notification> notificationList;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter adapter;
    private ProgressBar progressBar;
    private DotService dotService;
    private Call<JsonObject> call;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.main_activity_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        initToolbars();
        telecommandeBtn = (ImageButton) findViewById(R.id.telecommandeBtn);
        listBtn = (ImageButton) findViewById(R.id.listBtn);
        notificationsBtn = (ImageButton) findViewById(R.id.notificationsBtn);
        mRecyclerView = (RecyclerView) findViewById(R.id.notificationsRecycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        adapter = new RecyclerAdapter(activity, notificationList);
        mRecyclerView.setAdapter(adapter);
        getNotificationList();
        telecommandeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent telecomandeIntent = new Intent( activity, CommandActivity.class);
                startActivity(telecomandeIntent);
            }
        });

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listIntent = new Intent(activity, ListCommandActivity.class);
                startActivity(listIntent);
            }
        });

        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eventIntent = new Intent(activity, EventsActivity.class);
                startActivity(eventIntent);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        initToolbars();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        call.cancel();
    }

    private void getNotificationList() {
        notificationList = new ArrayList<>();

        // Appel list notifications
        dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL);
        Log.v(TAG, CurrentUser.getEmail());
        call = dotService.getListNotifications(CurrentUser.getToken(), CurrentUser.getEmail());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.v(TAG, response.raw().toString());
                    Log.v(TAG, "body: " +response.body().toString());
                    JsonArray myList = response.body().get("data").getAsJsonArray();
                    Log.v(TAG, myList.toString());
                    Log.v(TAG, "list SIze + " + myList.size());
                    for(int i = 0; i < myList.size(); i++) {
                        JsonObject myNotifJson = myList.get(i).getAsJsonObject();

                        Log.v(TAG, "MyNotifJson " + myNotifJson.toString());
                        Log.v(TAG, "MyNotifJson " + myNotifJson.get("id").toString());
                        Log.v(TAG, "MyNotifJson " + myNotifJson.get("attributes").toString());
                        Log.v(TAG, "MyNotifJson " + myNotifJson.get("attributes").getAsJsonObject().toString());

                        Notification myNotif = Notification.init(myNotifJson.get("id").getAsInt(), myNotifJson.get("attributes").getAsJsonObject());
                        notificationList.add(myNotif);
                        Log.v(TAG, "myNotif : " + myNotif.getTitle());
                    }
                    adapter = new RecyclerAdapter(activity, notificationList);
                    mRecyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(activity, "Error : " + getResources().getString(R.string.errorGetReminders), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.v(TAG, t.toString());

                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void initToolbars() {
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.app_bar);
        if(CurrentUser.getAvatarPath() != null) {
            ImageButton profil = (ImageButton) toolbarTop.findViewById(R.id.profilBtn);
            profil.setBackground(CurrentUser.getCurrentAvatar(150));
            profil.setImageResource(0);
        }
        toolbarTop.findViewById(R.id.profilBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilIntent = new Intent(activity, UpdateUserActivity.class);
                startActivity(profilIntent);
            }
        });
        if(CurrentUser.getAdmin()) {
            ImageButton adminBtn = (ImageButton) toolbarTop.findViewById(R.id.adminBtn);
            adminBtn.setVisibility(View.VISIBLE);
            adminBtn.setClickable(true);
            adminBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent adminIntent = new Intent(activity, AdminActivity.class);
                    startActivity(adminIntent);
                }
            });
        }
        TextView title = (TextView) toolbarTop.findViewById(R.id.app_bar_title);
        title.setText(R.string.title_activity_main);
        toolbarTop.setTitle(null);
        setActionBar(toolbarTop);

        Toolbar toolbarBottom = (Toolbar) findViewById(R.id.bottomToolbar);

    }


    /** methode de callback appelee quand le clic est declenche dans l adapter */
    @Override
    public void showDetail(Notification f) {
//        Fragment fragTest = getSupportFragmentManager().findFragmentByTag("detailFrag");
//        if(fragTest == null) {
//            CommandListDetailFragment frag = CommandListDetailFragment.newInstance(c);
//            getSupportFragmentManager().beginTransaction()
//                    /** Il faudra remplacer les deux drawables suivants pour y mettre tes propres animators */
//                    .setCustomAnimations(R.anim.slide_in_right, 0, 0,R.anim.slide_out_right)
//                    .replace(R.id.commande_container_frame, frag, "detailFrag")
//                    .addToBackStack("detailFrag")
//                    .commit();

            Intent myIntent = new Intent(this, NotificationDetailActivity.class);
            myIntent.putExtra("notification", f);

            startActivity(myIntent);
        }




}
