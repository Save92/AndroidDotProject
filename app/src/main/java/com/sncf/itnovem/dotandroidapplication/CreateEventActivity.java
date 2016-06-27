package com.sncf.itnovem.dotandroidapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
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
 * Created by Journaud Nicolas on 20/04/16.
 */
public class CreateEventActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener{
    private static final String TAG = "CREATE_EVENT";
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";

    private Activity activity;
    private ImageButton telecommandeBtn;
    private ImageButton listBtn;
    private ImageButton notificationsBtn;
    private EditText title;
    private EditText description;
    private SeekBar durationSeekBar;
    private Spinner type;
    private ImageButton datePickerBtn;
    private ImageButton timePickerBtn;
    private TextView dateLabel;
    private TextView dateResult;
    private TextView timeResult;
    private Button btnSubmit;
    private TextView seekBarValue;
    private RadioGroup radiogroup;
    private RadioButton radioButton;
    private DotService dotService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        activity = this;
        initToolbars();
        durationSeekBar = (SeekBar) findViewById(R.id.seekBarDuration);
        seekBarValue = (TextView) findViewById(R.id.seekBarValue);
        radiogroup =  (RadioGroup) findViewById(R.id.radioGroupPriority);
        initDate();
        initTextInput();
        initTypeSpinner();
        initSeekBar();
        onSave();
    }

    private void initDate() {
        dateLabel = (TextView) findViewById(R.id.date_picker_label);
        dateResult = (TextView) findViewById(R.id.date_notif);
        timeResult = (TextView) findViewById(R.id.time);
        datePickerBtn = (ImageButton) findViewById(R.id.datePickerBtn);
        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(CreateEventActivity.this);
                cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });
        datePickerBtn.setVisibility(View.GONE);
        dateResult.setVisibility(View.GONE);
        timePickerBtn = (ImageButton) findViewById(R.id.timePickerBtn);
        timePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(CreateEventActivity.this);
                rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
            }
        });
        timePickerBtn.setVisibility(View.GONE);
        timeResult.setVisibility(View.GONE);
        dateLabel.setVisibility(View.GONE);
    }

    private void initTextInput() {
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description_content);
    }

    private void onSave() {
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int error = 0;
                initError();
                if(title.getText() == null || title.getText().length() == 0) {
                    title.setError(getResources().getString(R.string.title_error));
                    error = 1;
                }
                if(description.getText() == null || description.getText().length() == 0) {
                    description.setError(getResources().getString(R.string.description_error));
                    error = 1;
                }
                if(seekBarValue.getText().length() == 0) {
                    seekBarValue.setError(getString(R.string.error_duration));
                    error = 1;
                }
                if(type.getSelectedItem() == "Alerte") {
                    if(dateResult.getText().toString().equals("jj/MM/aaaa") || dateResult.getText().length() == 0) {
                        dateResult.setError(getString(R.string.error_date));
                        datePickerBtn.setImageResource(R.drawable.ic_date_range_red_36dp);
                    }
                    if(timeResult.getText().toString().equals("HH:mm") || timeResult.getText().length() == 0) {
                        timeResult.setError(getString(R.string.error_time));
                        timePickerBtn.setImageResource(R.drawable.ic_access_time_red_36dp);
                    }
                }
                int selectedId = radiogroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedId);
                if(radioButton == null) {
                    TextView radioText = (TextView) findViewById(R.id.textView14);
                    radioText.setError(getString(R.string.error_priotity));
                    error = 1;
                }
                if(error == 0) {
                    saveNotification();
                    finish();
                }
            }

        });
    }

    private void saveNotification() {
        final Notification newNotif;
        if(type.getSelectedItem() == "Alerte") {
            String dateNotif = dateResult.getText().toString() + " " + timeResult.getText().toString();
            newNotif = new Notification(title.getText().toString(), dateNotif, type.getSelectedItem().toString(), CurrentUser.getCurrentUserId(), description.getText().toString(), durationSeekBar.getProgress(), Integer.parseInt(radioButton.getText().toString()));
        } else {
            newNotif = new Notification(title.getText().toString(), null, type.getSelectedItem().toString(), CurrentUser.getCurrentUserId(), description.getText().toString(), durationSeekBar.getProgress(), Integer.parseInt(radioButton.getText().toString()));
        }
        if (NetworkUtil.checkDeviceConnected(this)) {
            // Appel list notifications
            dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL);
            Call<JsonObject> call = dotService.createNotification(newNotif, CurrentUser.getToken(), CurrentUser.getEmail());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject myNotifJson = response.body().get("data").getAsJsonObject();
                        Notification myNotif = Notification.init(myNotifJson.get("id").getAsInt(), myNotifJson.get("attributes").getAsJsonObject());
                        Toast.makeText(activity, getString(R.string.success_created_notif), Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(activity, NotificationDetailActivity.class);
                        myIntent.putExtra("notification", myNotif);
                        startActivity(myIntent);
                    } else {
                        Toast.makeText(activity, "Error : " + getResources().getString(R.string.errorCreateReminders), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {}
            });
        } else {
            try {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle(R.string.info);

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

    private void initError() {
        title.setError(null);
        seekBarValue.setError(null);
        description.setError(null);
    }

    private void initSeekBar() {
        int max = 60;
        durationSeekBar.setMax(max);
        durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarValue.setError(null);
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int step = 5;
                if (progress < 0) {

                    int min = 1;
                    progress = 0;
                    seekBar.setProgress(min);
                }
                progress = ((int) Math.round(progress / step)) * step;
                seekBar.setProgress(progress);
                seekBarValue.setText(String.valueOf(progress) + getString(R.string.minutes_unit));
            }
        });
    }

    private void initTypeSpinner(){
        type = (Spinner) findViewById(R.id.spinner);
        List<String> typesList = new ArrayList<>();
        typesList.add("Mémo");
        typesList.add("Alerte");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, typesList);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(dataAdapter);
        addListenerOnSpinnerItemSelection();
        addListenerOnButton();
    }

    private void displayDate(Boolean display) {
        if(display) {
            datePickerBtn.setVisibility(View.VISIBLE);
            dateResult.setVisibility(View.VISIBLE);
            timePickerBtn.setVisibility(View.VISIBLE);
            timeResult.setVisibility(View.VISIBLE);
            dateLabel.setVisibility(View.VISIBLE);
        } else {
            datePickerBtn.setVisibility(View.GONE);
            dateResult.setVisibility(View.GONE);
            timePickerBtn.setVisibility(View.GONE);
            timeResult.setVisibility(View.GONE);
            dateLabel.setVisibility(View.GONE);
        }
    }

    public void addListenerOnSpinnerItemSelection(){
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(type.getSelectedItem().toString().equals("Alerte")) {
                    displayDate(true);
                } else {
                    displayDate(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });
    }

    public void addListenerOnButton() {
        type = (Spinner) findViewById(R.id.spinner);
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
        TextView title = (TextView) toolbarTop.findViewById(R.id.app_bar_title);
        title.setText(R.string.title_activity_create_event);
        title.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        toolbarTop.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);
        toolbarTop.setTitle(null);
        setActionBar(toolbarTop);
        toolbarTop.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        String jour;
        String mois;
        if(Integer.toString(dayOfMonth).length() == 1) {
            jour = "0" + Integer.toString(dayOfMonth);
        } else {
            jour = Integer.toString(dayOfMonth);
        }
        if(Integer.toString(monthOfYear).length() == 1) {
            mois = "0" + Integer.toString(monthOfYear+1);
        } else {
            mois = Integer.toString(monthOfYear+1);
        }
        dateResult.setError(null);
        datePickerBtn.setImageResource(R.drawable.ic_date_range_green_36dp);
        dateResult.setText(getString(R.string.calendar_date_picker_result_values, year, mois, jour));
    }


    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        String heure;
        String minutes;
        if(Integer.toString(hourOfDay).length() == 1) {
            heure = "0" + Integer.toString(hourOfDay);
        } else {
            heure = Integer.toString(hourOfDay);
        }
        if(Integer.toString(minute).length() == 1) {
            minutes = "0" + Integer.toString(minute);
        } else {
            minutes = Integer.toString(minute);
        }
        timeResult.setError(null);
        timePickerBtn.setImageResource(R.drawable.ic_access_time_green_36dp);
        timeResult.setText(getString(R.string.radial_time_picker_result_value, heure, minutes));
    }

    @Override
    public void onResume() {
        super.onResume();
        CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = (CalendarDatePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialogFragment != null) {
            calendarDatePickerDialogFragment.setOnDateSetListener(this);
        }
        RadialTimePickerDialogFragment rtpd = (RadialTimePickerDialogFragment) getSupportFragmentManager().findFragmentByTag(FRAG_TAG_TIME_PICKER);
        if (rtpd != null) {
            rtpd.setOnTimeSetListener(this);
        }
        initToolbars();
    }

}
