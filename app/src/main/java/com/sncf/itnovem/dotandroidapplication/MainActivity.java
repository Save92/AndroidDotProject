package com.sncf.itnovem.dotandroidapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private SharedPreferences session;
    private static SharedPreferences.Editor editor;
    private boolean doubleBackToExit = false;
    private boolean doubleLogoutToExit = false;
    private Toast toast;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.main_activity_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        toast=Toast.makeText(activity, "", Toast.LENGTH_LONG);
        mRecyclerView = (RecyclerView) findViewById(R.id.notificationsRecycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        adapter = new RecyclerAdapter(activity, notificationList);
        mRecyclerView.setAdapter(adapter);
        session = getSharedPreferences(CurrentUser.SESSION_FILENAME, MODE_PRIVATE);
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
                        Toast.makeText(activity, "Error : " + getResources().getString(R.string.error_get_reminders), Toast.LENGTH_SHORT).show();
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
                builder.setMessage(getResources().getString(R.string.error_network));
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
                toast.cancel();
            }
        });

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listIntent = new Intent(activity, ListCommandActivity.class);
                startActivity(listIntent);
                toast.cancel();
            }
        });

        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eventIntent = new Intent(activity, EventsActivity.class);
                startActivity(eventIntent);
                toast.cancel();
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
                toast.cancel();
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
                    toast.cancel();
                    Intent adminIntent = new Intent(activity, AdminActivity.class);
                    startActivity(adminIntent);
                }
            });
        }
        toolbarTop.setLogo(R.drawable.logout);
        View logoView = getToolbarLogoIcon(toolbarTop);
        logoView.getLayoutParams().height = 100;
        logoView.getLayoutParams().width = 100;
        logoView.setLayoutParams(logoView.getLayoutParams());
        logoView.setPadding(0, 0, 15, 0);
        logoView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                return openDialog();
            }
        });
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast.cancel();
                if(doubleLogoutToExit) {
                    toast.cancel();
                    activity.finish();
                } else {
                    doubleLogoutToExit = true;
                    toast = Toast.makeText(activity, getString(R.string.double_click_for_exit_or_long), Toast.LENGTH_LONG);
                    TextView toastView = (TextView) toast.getView().findViewById(android.R.id.message);
                    if( toastView != null) toastView.setGravity(Gravity.CENTER);
                    toast.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleLogoutToExit = false;
                        }
                    }, 2000);
                }

            }
        });
        TextView title = (TextView) toolbarTop.findViewById(R.id.app_bar_title);
        title.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        title.setText(R.string.title_activity_main);
        toolbarTop.setTitle(null);
        setActionBar(toolbarTop);
    }

    private Boolean openDialog(){
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View subView = inflater.inflate(R.layout.logout_alert_dialog, null);
        final CheckBox reconnect = (CheckBox) subView.findViewById(R.id.checkBox);
        final CheckBox changeUser = (CheckBox) subView.findViewById(R.id.checkBox2);
        final CheckBox remember = (CheckBox) subView.findViewById(R.id.checkBox3);
        reconnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked) {
                    changeUser.setChecked(false);
                    remember.setChecked(false);
                }
             }
        });
        changeUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked) {
                    reconnect.setChecked(false);
                    remember.setChecked(false);
                }
            }
        });
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked) {
                    reconnect.setChecked(false);
                    changeUser.setChecked(false);
                }
            }
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.logout_choice));
        builder.setView(subView);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                session = activity.getApplicationContext().getSharedPreferences(CurrentUser.SESSION_FILENAME, 0);

                if(reconnect.isChecked()) {
                    editor = session.edit();
                    editor.putBoolean(CurrentUser.RECONNECT_KEY, true);
                    editor.putBoolean(CurrentUser.REMEMBER_KEY, true);
                    editor.apply();
                }
                if(changeUser.isChecked()) {
                    editor = session.edit();
                    editor.putBoolean(CurrentUser.RECONNECT_KEY, false);
                    editor.putBoolean(CurrentUser.REMEMBER_KEY, false);
                    editor.putBoolean(CurrentUser.LOGIN_SHOW_KEY, true);
                    editor.apply();
                }
                if(remember.isChecked()) {
                    editor = session.edit();
                    editor.putBoolean(CurrentUser.RECONNECT_KEY, false);
                    editor.putBoolean(CurrentUser.REMEMBER_KEY, true);
                    editor.apply();
                }
                activity.finish();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        return true;
    }

    public static View getToolbarLogoIcon(Toolbar toolbar){
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        toolbar.findViewsWithText(potentialViews,contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        View logoIcon = null;
        if(potentialViews.size() > 0){
            logoIcon = potentialViews.get(0);
        }
        if(hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }

    /** methode de callback appelee quand le clic est declenche dans l adapter */
    @Override
    public void showDetail(Notification f) {
            toast.cancel();
            Intent myIntent = new Intent(this, NotificationDetailActivity.class);
            myIntent.putExtra("notification", f);
            startActivity(myIntent);
    }
    @Override
    public void onBackPressed() {
        toast.cancel();
        if(doubleBackToExit) {
            super.onBackPressed();
            toast.cancel();
            return;
        }
        this.doubleBackToExit = true;
        toast = Toast.makeText(activity, getString(R.string.double_click_for_exit), Toast.LENGTH_LONG);
        TextView toastView = (TextView) toast.getView().findViewById(android.R.id.message);
        if( toastView != null) toastView.setGravity(Gravity.CENTER);
        toast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExit = false;
            }
        }, 2000);
    }
}
