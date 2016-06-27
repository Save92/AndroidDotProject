package com.sncf.itnovem.dotandroidapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import com.sncf.itnovem.dotandroidapplication.Adapters.RecyclerAdapter;
import com.sncf.itnovem.dotandroidapplication.Adapters.SimpleDividerItemDecoration;
import com.sncf.itnovem.dotandroidapplication.Models.Notification;
import com.sncf.itnovem.dotandroidapplication.services.API;
import com.sncf.itnovem.dotandroidapplication.services.DotService;
import com.sncf.itnovem.dotandroidapplication.services.ServiceGenerator;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;
import com.sncf.itnovem.dotandroidapplication.utils.NetworkUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Journaud Nicolas on 27/01/16.
 */
public class MainActivity extends Activity implements RecyclerAdapter.CallbackAdapter {
    static public String TAG = "MAIN";

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
        mRecyclerView = (RecyclerView) findViewById(R.id.notificationsRecycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        adapter = new RecyclerAdapter(activity, notificationList);
        mRecyclerView.setAdapter(adapter);
        getNotificationList();
    }

    @Override
    public void onResume() {
        super.onResume();
        initToolbars();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        call.cancel();
    }

    private void getNotificationList() {
        if (NetworkUtil.checkDeviceConnected(this)) {
            notificationList = new ArrayList<>();

            // Appel list notifications
            dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL);
            call = dotService.getListNotifications(CurrentUser.getToken(), CurrentUser.getEmail());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonArray myList = response.body().get("data").getAsJsonArray();
                        for (int i = 0; i < myList.size(); i++) {
                            JsonObject myNotifJson = myList.get(i).getAsJsonObject();
                            Notification myNotif = Notification.init(myNotifJson.get("id").getAsInt(), myNotifJson.get("attributes").getAsJsonObject());
                            notificationList.add(myNotif);
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
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            try {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.info));
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage(getResources().getString(R.string.errorNetwork));
                final android.support.v7.app.AlertDialog alertDialog = builder.create();
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                builder.show();
            }
            catch(Exception e)
            {}
        }
    }

    private void initToolbars() {

        telecommandeBtn = (ImageButton) findViewById(R.id.telecommandeBtn);
        listBtn = (ImageButton) findViewById(R.id.listBtn);
        notificationsBtn = (ImageButton) findViewById(R.id.notificationsBtn);

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
        title.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        title.setText(R.string.title_activity_main);
        toolbarTop.setTitle(null);
        setActionBar(toolbarTop);
    }

    /** methode de callback appelee quand le clic est declenche dans l adapter */
    @Override
    public void showDetail(Notification f) {
            Intent myIntent = new Intent(this, NotificationDetailActivity.class);
            myIntent.putExtra("notification", f);
            startActivity(myIntent);
        }
}
