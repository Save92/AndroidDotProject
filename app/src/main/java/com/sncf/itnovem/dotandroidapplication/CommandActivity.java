package com.sncf.itnovem.dotandroidapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.Models.Settings;
import com.sncf.itnovem.dotandroidapplication.services.API;
import com.sncf.itnovem.dotandroidapplication.services.DotService;
import com.sncf.itnovem.dotandroidapplication.services.ServiceGenerator;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;
import com.sncf.itnovem.dotandroidapplication.utils.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommandActivity extends Activity {

    private static final String COMMAND = "COMMAND_ACTIVITY";


    private Toolbar toolbar;
    private Toolbar bottomtoolbar;
    private Activity activity;
    private ImageButton telecommandeBtn;
    private ImageButton listBtn;
    private ImageButton notificationsBtn;

    private Settings currentSettings;

    // Boutons de la télécommande

    private ImageButton muteBtn;
    private ImageButton clientModeBtn;
    private ImageButton testModeBtn;
    private ImageButton remindersBtn;
    private ImageButton addNotificationBtn;
    private ImageButton busyBtn;
    private ImageButton twitterBtn;

    private TextView activateReminders;
    private TextView desactiveReminders;
    private TextView clientMode;
    private TextView normalMode;
    private TextView sleepText;
    private TextView wakeText;
    private TextView busyOn;
    private TextView busyOff;
    private TextView twitterOn;
    private TextView twitterOff;

    private String testText = "";


    private DotService dotService;

    // Pour les tests
    Boolean sarahEnabled;
    Boolean twitter_enabled;
    Boolean reminders_enabled;
    Boolean room_occupied;
    Boolean screen_guest_enabled;

    // JSON
    JsonObject gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        activity = this;
        initToolbars();
        muteBtn = (ImageButton) findViewById(R.id.muteBtn);
        sleepText = (TextView) findViewById(R.id.textView9);
        wakeText = (TextView) findViewById(R.id.textViewOutSleep);
        clientModeBtn = (ImageButton) findViewById(R.id.clientModeBtn);
        testModeBtn = (ImageButton) findViewById(R.id.testSarahBtn);
        remindersBtn = (ImageButton) findViewById(R.id.hearingBtn);
        addNotificationBtn = (ImageButton) findViewById(R.id.addNotifBtn);
        activateReminders = (TextView) findViewById(R.id.textView10);
        desactiveReminders = (TextView) findViewById(R.id.textViewDesactiver);
        clientMode = (TextView) findViewById(R.id.textView11);
        normalMode = (TextView) findViewById(R.id.textViewmodeNormal);
        busyBtn = (ImageButton) findViewById(R.id.busyBtn);
        busyOn = (TextView) findViewById(R.id.textViewBusyOn);
        busyOff = (TextView) findViewById(R.id.textViewBusyOff);
        twitterBtn = (ImageButton) findViewById(R.id.twitterBtn);
        twitterOff = (TextView) findViewById(R.id.textViewTwitterOff);
        twitterOn = (TextView) findViewById(R.id.textViewTwitterOn);
        gson = new JsonObject();
        dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL);
        getSettings();
        muteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gson = new JsonObject();
                gson.addProperty("sarah_enabled", !sarahEnabled);
                modifySettings(gson);
            }
        });

        clientModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gson = new JsonObject();
                gson.addProperty("screen_guest_enabled", !screen_guest_enabled);
                modifySettings(gson);
            }
        });

        testModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gson = new JsonObject();
                Log.v(COMMAND, "test name" + CurrentUser.getFirstName());
                openDialog();
                gson = new JsonObject();
                gson.addProperty("text", testText);
                sendTest(gson);
            }
        });

        remindersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gson = new JsonObject();
                gson.addProperty("reminders_enabled", !reminders_enabled);
                modifySettings(gson);
            }
        });

        addNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNotifIntent = new Intent(activity, CreateEventActivity.class);
                startActivity(addNotifIntent);
            }
        });

        busyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gson = new JsonObject();
                gson.addProperty("room_occupied", !room_occupied);
                modifySettings(gson);
            }
        });

        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gson = new JsonObject();
                gson.addProperty("twitter_enabled", !twitter_enabled);
                modifySettings(gson);
            }
        });

    }

    private void sendTest(JsonObject gson) {
        Call<JsonObject> call = dotService.testSarah(gson, CurrentUser.getToken(), CurrentUser.getEmail());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(activity, "Send", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(activity, "Error : " + getResources().getString(R.string.errorGetCommands), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.v(COMMAND, t.toString());

            }
        });
    }

    private void modifySettings(JsonObject json) {
        if (NetworkUtil.checkDeviceConnected(this)) {
            Call<JsonObject> call = dotService.actionTélécommande(json, CurrentUser.getToken(), CurrentUser.getEmail());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject settingsJson = response.body().get("data").getAsJsonObject();
                        currentSettings = Settings.init(settingsJson.get("attributes").getAsJsonObject());
                        Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show();
                        refreshView();
                    } else {
                        Toast.makeText(activity, "Error : " + getResources().getString(R.string.errorGetCommands), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.v(COMMAND, t.toString());

                }
            });
        } else {
            //TODO DIALOG
        }
    }

    private void getSettings() {
        if (NetworkUtil.checkDeviceConnected(this)) {
            dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL);
            Call<JsonObject> call = dotService.getSettings(CurrentUser.getToken(), CurrentUser.getEmail());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject settingsJson = response.body().get("data").getAsJsonObject();
                        currentSettings = Settings.init(settingsJson.get("attributes").getAsJsonObject());
                        Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show();
                        refreshView();
                    } else {
                        Toast.makeText(activity, "Error :" + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.v(COMMAND, t.toString());

                }
            });
        } else {
            try {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("Info");

                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage(getResources().getString(R.string.errorNetwork));
                final android.support.v7.app.AlertDialog alertDialog = builder.create();
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                builder.show();
            }
            catch(Exception e)
            {
                Log.d("COMMAND", "Show Dialog: "+e.getMessage());
            }
        }
    }

    private void refreshView() {
        sarahEnabled = currentSettings.getSarah_enabled();
        twitter_enabled = currentSettings.getTwitter_enabled();
        reminders_enabled = currentSettings.getReminders_enabled();
        room_occupied = currentSettings.getRoom_occupied();
        screen_guest_enabled = currentSettings.getScreen_guest_enabled();
        

        switchSarah(sarahEnabled);
        switchTwitter(twitter_enabled);
        switchReminders(reminders_enabled);
        switchGest(screen_guest_enabled);
        switchRoomOccupied(room_occupied);
    }

    private void switchSarah(Boolean b) {
        if(b) {
            muteBtn.setImageResource(R.drawable.ic_sleep_off_white_48dp);
            sleepText.setVisibility(View.VISIBLE);
            wakeText.setVisibility(View.GONE);
        } else {
            muteBtn.setImageResource(R.drawable.ic_sleep_white_48dp);
            sleepText.setVisibility(View.GONE);
            wakeText.setVisibility(View.VISIBLE);
        }
    }

    private void switchTwitter(Boolean b) {
        if(b) {
            twitterBtn.setImageResource(R.drawable.ic_twitter_white_48dp);
            twitterOn.setVisibility(View.VISIBLE);
            twitterOff.setVisibility(View.GONE);
        } else {
            twitterBtn.setImageResource(R.drawable.ic_twitter_off_white_48dp);
            twitterOn.setVisibility(View.GONE);
            twitterOff.setVisibility(View.VISIBLE);
        }
    }

    private void switchReminders(Boolean b) {
        if(b) {
            remindersBtn.setImageResource(R.drawable.ic_notifications_white_48dp);
            desactiveReminders.setVisibility(View.GONE);
            activateReminders.setVisibility(View.VISIBLE);
        } else {
            remindersBtn.setImageResource(R.drawable.ic_notifications_off_white_48dp);
            desactiveReminders.setVisibility(View.VISIBLE);
            activateReminders.setVisibility(View.GONE);
        }
    }


    private void switchGest(Boolean b) {
        if(b) {
            clientModeBtn.setImageResource(R.drawable.ic_slideshow_white_48dp);
            clientMode.setVisibility(View.VISIBLE);
            normalMode.setVisibility(View.GONE);
        } else {
            clientModeBtn.setImageResource(R.drawable.ic_cast_connected_white_48dp);
            clientMode.setVisibility(View.GONE);
            normalMode.setVisibility(View.VISIBLE);
        }
    }

    private void switchRoomOccupied(Boolean b) {
        if(b) {
            busyBtn.setImageResource(R.drawable.ic_led_on_white_48dp);
            busyOff.setVisibility(View.GONE);
            busyOn.setVisibility(View.VISIBLE);
        } else {
            busyBtn.setImageResource(R.drawable.ic_led_off_white_48dp);
            busyOff.setVisibility(View.VISIBLE);
            busyOn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        initToolbars();
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
                finish();
            }
        });

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listIntent = new Intent(activity, ListCommandActivity.class);
                startActivity(listIntent);
                finish();
            }
        });

        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eventIntent = new Intent(activity, EventsActivity.class);
                startActivity(eventIntent);
                finish();
            }
        });

        Toolbar toolbarTop = (Toolbar) findViewById(R.id.app_bar);
        if(CurrentUser.getAvatarPath() != null) {
            ImageButton profil = (ImageButton) toolbarTop.findViewById(R.id.profilBtn);
            profil.setBackground(CurrentUser.getCurrentAvatar(150));
            profil.setImageResource(0);
        }
        setActionBar(toolbarTop);
        toolbarTop.setLogo(R.drawable.ic_home_black_24dp);
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
        title.setText(R.string.title_activity_command);
        title.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        toolbarTop.setTitle(null);
        View logoView = getToolbarLogoIcon(toolbarTop);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logo clicked
                Intent homeIntent = new Intent(activity, MainActivity.class);
                startActivity(homeIntent);
                activity.finish();
            }
        });

        Toolbar toolbarBottom = (Toolbar) findViewById(R.id.bottomToolbar);
    }

    private void openDialog(){
        LayoutInflater inflater = LayoutInflater.from(CommandActivity.this);
        View subView = inflater.inflate(R.layout.test_alert_dialog, null);
        final EditText subEditText = (EditText)subView.findViewById(R.id.editTextTest);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test Sarah");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                testText = subEditText.getText().toString();
                testText = testText.replaceAll("\\.", " poin ");
                Toast.makeText(activity, testText, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CommandActivity.this, "Cancel", Toast.LENGTH_LONG).show();
            }
        });

        builder.show();
    }

    public static View getToolbarLogoIcon(Toolbar toolbar){
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews,contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if(potentialViews.size() > 0){
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if(hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }
}
