package com.sncf.itnovem.dotandroidapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.sncf.itnovem.dotandroidapplication.Models.Notification;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;

/**
 * Created by Journaud Nicolas on 20/04/16.
 */
public class NotificationDetailActivity extends Activity {

    static public String TAG = "NOTIFICATION_DETAIL";

    private Activity activity;
    private Notification currentNotif;
    private TextView title;
    private TextView date;
    private TextView type;
    private TextView ownerName;
    private TextView description;
    private TextView duration;
    private TextView displayed;
    private TextView displayed_ago;
    private TextView displayed_ago_label;
    private TextView date_notif;
    private TextView date_notification_label;
    private TextView priority;
    private ImageView typeLogo;

    // bottom toolbar
    private ImageButton telecommandeBtn;
    private ImageButton listBtn;
    private ImageButton notificationsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);
        Intent myIntent = getIntent();
        currentNotif = new Notification();
        currentNotif =  myIntent.getParcelableExtra("notification");
        activity = this;
        initToolbars();

        title = (TextView) findViewById(R.id.titre);
        date = (TextView) findViewById(R.id.date_creation);
        type = (TextView) findViewById(R.id.type);
        ownerName = (TextView) findViewById(R.id.owner_name);
        description = (TextView) findViewById(R.id.description);
        date_notif = (TextView) findViewById(R.id.date_notif);
        date_notification_label = (TextView) findViewById(R.id.date_notification_label);
        duration = (TextView) findViewById(R.id.duration);
        typeLogo = (ImageView) findViewById(R.id.typeLogo);
        displayed = (TextView) findViewById(R.id.displayed_text);
        displayed_ago_label = (TextView) findViewById(R.id.displayed_ago_label);
        displayed_ago = (TextView) findViewById(R.id.displayed_ago_text);
        priority = (TextView) findViewById(R.id.priority);
        displayCurrentNotif();
    }

    private void displayCurrentNotif() {
        if(currentNotif != null) {
            if(currentNotif.getTitle() != null){
                title.setText(currentNotif.getTitle());
            } else {
                title.setText("");
                Toast.makeText(getApplicationContext(), "error title", Toast.LENGTH_SHORT).show();
            }
            if(currentNotif.getCreatedAt() != null){
                date.setText(currentNotif.getCreatedAt());
            } else {
                date.setText("");
                Toast.makeText(getApplicationContext(), "error date creation", Toast.LENGTH_SHORT).show();
            }
            if(currentNotif.getType() != null){
                type.setText(currentNotif.getType());
                if(currentNotif.getType().equals("memo")) {
                    date_notif.setVisibility(View.GONE);
                    date_notification_label.setVisibility(View.GONE);
                }
            } else {
                type.setText("");
                Toast.makeText(getApplicationContext(), "error type", Toast.LENGTH_SHORT).show();
            }
            if(currentNotif.getUser() != null){
                ownerName.setText(currentNotif.getUser());
            } else {

                ownerName.setText("");
                Toast.makeText(getApplicationContext(), "error owner_name", Toast.LENGTH_SHORT).show();
            }
            if(currentNotif.getContent() != null){
                description.setText(currentNotif.getContent());
            } else {
                description.setText("");
                Toast.makeText(getApplicationContext(), "error description", Toast.LENGTH_SHORT).show();
            }
            if(currentNotif.getDisplayAt() != null){
                date_notif.setText(currentNotif.getDisplayAt());
            } else {
                date_notif.setText("");
                Toast.makeText(getApplicationContext(), "error date notification", Toast.LENGTH_SHORT).show();
            }
            if(currentNotif.getDuration() != null){
                duration.setText(String.valueOf(currentNotif.getDuration()));
            } else {
                duration.setText("");
                Toast.makeText(getApplicationContext(), "error duration", Toast.LENGTH_SHORT).show();
            }
            if(currentNotif.getPriority() != null){
                priority.setText(String.valueOf(currentNotif.getPriority()));
            } else {
                priority.setText("");
                Toast.makeText(getApplicationContext(), "error priority", Toast.LENGTH_SHORT).show();
            }
            if(currentNotif.getType().equals("memo")) {
                typeLogo.setImageResource(R.drawable.ic_chat_white_24dp);
            } else {
                typeLogo.setImageResource(R.drawable.ic_notifications_white_24dp);
            }
            if(currentNotif.getDisplayed() != null) {
                if(currentNotif.getDisplayed()){
                    displayed.setText(getResources().getString(R.string.yes));
                    if(currentNotif.getDisplayed_ago() != null && !currentNotif.getDisplayed_ago().isEmpty()) {
                        displayed_ago_label.setVisibility(View.VISIBLE);
                        displayed_ago.setVisibility(View.VISIBLE);
                        displayed_ago.setText(currentNotif.getDisplayed_ago());
                    }
                } else {
                    displayed.setText(getResources().getString(R.string.no));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initToolbars();
    }

    private void initToolbars() {

        telecommandeBtn = (ImageButton) findViewById(R.id.telecommandeBtn);
        listBtn = (ImageButton) findViewById(R.id.listBtn);
        notificationsBtn = (ImageButton) findViewById(R.id.notificationsBtn);

        telecommandeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent telecomandeIntent = new Intent(activity, CommandActivity.class);
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

        Toolbar toolbarTop = (Toolbar) findViewById(R.id.app_bar_detail);
        if(CurrentUser.getAvatarPath() != null) {
            ImageButton profil = (ImageButton) toolbarTop.findViewById(R.id.profilBtn);
            profil.setBackground(CurrentUser.getCurrentAvatar(120));
            profil.setImageResource(0);
        }
        toolbarTop.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);
        TextView title = (TextView) toolbarTop.findViewById(R.id.app_bar_title);
        title.setText(R.string.notification_detail_activity);
        title.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        toolbarTop.setTitle(null);
        setActionBar(toolbarTop);
        toolbarTop.findViewById(R.id.profilBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilIntent = new Intent(activity, UpdateUserActivity.class);
                startActivity(profilIntent);
            }
        });
        toolbarTop.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}

