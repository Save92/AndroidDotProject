package com.sncf.itnovem.dotandroidapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.Models.User;
import com.sncf.itnovem.dotandroidapplication.services.API;
import com.sncf.itnovem.dotandroidapplication.services.DotService;
import com.sncf.itnovem.dotandroidapplication.services.ServiceGenerator;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends AppCompatActivity {

    static public String TAG = "LOGINUSER_DETAIL";

    private Activity activity;
    private User currentUser;
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private CheckBox admin;
    private CheckBox approved;
    private FloatingActionButton saveBtn;

    private DotService dotService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        Intent myIntent = getIntent();
        currentUser = new User();
        currentUser =  myIntent.getParcelableExtra("user");
        activity = this;
        initToolbars();
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        initToolbars();
    }

    private void initView() {
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastnameText);
        email = (EditText) findViewById(R.id.emailValue);
        admin = (CheckBox) findViewById(R.id.isAdmin);
        approved = (CheckBox) findViewById(R.id.isApproved);
        password = (EditText) findViewById(R.id.passwordValue);
        saveBtn = (FloatingActionButton) findViewById(R.id.saveBtn);
        displayCurrentUser();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfValid()) {
                    saveCurrentUser();
                }
            }
        });
    }

    private void saveCurrentUser() {
        User myUser = currentUser;
        myUser.setAdmin(admin.isChecked());
        myUser.setApproved(approved.isChecked());
        myUser.setFirstname(firstname.getText().toString());
        myUser.setLastname(lastname.getText().toString());
        myUser.setEmail(email.getText().toString());
        if(password.getText() != null && !password.getText().toString().isEmpty()){
            myUser.setPassword(password.getText().toString().replaceAll("\"", ""));
        }
        dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL);
        Call<JsonObject> call = dotService.updateUser(myUser.getUserId(), myUser, CurrentUser.getToken(), CurrentUser.getEmail());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject myUserJson = response.body().get("data").getAsJsonObject();
                    User user = User.init(myUserJson.get("id").getAsInt(), myUserJson.get("attributes").getAsJsonObject());
                    currentUser = user;
                    finish();
                    //Log.v(TAG, response.body().getFirstname());
                } else {
                    Toast.makeText(activity, "Error : " + getResources().getString(R.string.errorGetUser), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.v(TAG, t.toString());

            }
        });
        }

    private Boolean checkIfValid() {
        int error = 0;
        if(email.getText() == null || !isValidEmail(email.getText().toString())) {
            email.setError(getResources().getString(R.string.errorInvalidEmail));
            error = 1;
        }
        if(firstname.getText() == null || firstname.getText().length() == 0) {
            firstname.setError(getResources().getString(R.string.errorFirstnameRequired));
            error = 1;
        }
        if(lastname.getText() == null || lastname.getText().length() == 0) {
            lastname.setError(getResources().getString(R.string.errorLastnameRequired));
            error = 1;
        }
        if(error == 0) {
            return true;
        }
        return false;

    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private  void displayCurrentUser() {
        firstname.setText(currentUser.getFirstname());
        lastname.setText(currentUser.getLastname());
        email.setText(currentUser.getEmail());
        admin.setChecked(currentUser.getAdmin());
        approved.setChecked(currentUser.getApproved());
    }

    private void initToolbars() {
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.app_bar_detail);
        if(CurrentUser.getAvatarPath() != null) {
            ImageButton profil = (ImageButton) toolbarTop.findViewById(R.id.profilBtn);
            profil.setBackground(CurrentUser.getCurrentAvatar(120));
            profil.setImageResource(0);
        }
        toolbarTop.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);
        TextView title = (TextView) toolbarTop.findViewById(R.id.app_bar_title);
        title.setText(R.string.notification_detail_activity);
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
