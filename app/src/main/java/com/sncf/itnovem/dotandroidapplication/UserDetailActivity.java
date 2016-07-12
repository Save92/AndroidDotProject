package com.sncf.itnovem.dotandroidapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.Models.User;
import com.sncf.itnovem.dotandroidapplication.services.API;
import com.sncf.itnovem.dotandroidapplication.services.DotService;
import com.sncf.itnovem.dotandroidapplication.services.ServiceGenerator;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;
import com.sncf.itnovem.dotandroidapplication.utils.NetworkUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Journaud Nicolas on 20/04/16.
 */
public class UserDetailActivity extends AppCompatActivity {

    static public String TAG = "USER_DETAIL";

    private Activity activity;
    private User currentUser;
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private CheckBox admin;
    private CheckBox approved;
    private Button saveBtn;

    private DotService dotService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        Intent myIntent = getIntent();
        currentUser = new User();
        currentUser =  myIntent.getParcelableExtra("user");
        activity = this;
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initToolbars();
    }

    private void initView() {
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastnameText);
        email = (EditText) findViewById(R.id.emailValue);
        admin = (CheckBox) findViewById(R.id.isAdmin);
        approved = (CheckBox) findViewById(R.id.isApproved);
        password = (EditText) findViewById(R.id.passwordValue);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        displayUser();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfValid()) {
                    saveUser();
                }
            }
        });
    }

    private void saveUser() {
        User user = currentUser;
        user.setAdmin(admin.isChecked());
        user.setApproved(approved.isChecked());
        user.setFirstname(firstname.getText().toString());
        user.setLastname(lastname.getText().toString());
        user.setEmail(email.getText().toString());
        if(password.getText() != null && !password.getText().toString().isEmpty()){
            user.setPassword(password.getText().toString().replaceAll("\"", ""));
        }
        if (NetworkUtil.checkDeviceConnected(this)) {
            dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL);
            Call<JsonObject> call = dotService.updateUser(user.getUserId(), user, CurrentUser.getToken(), CurrentUser.getEmail());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject myUserJson = response.body().get("data").getAsJsonObject();
                        User user = User.init(myUserJson.get("id").getAsInt(), myUserJson.get("attributes").getAsJsonObject());
                        currentUser = user;
                        finish();
                    } else {
                        Toast.makeText(activity, "Error : " + getResources().getString(R.string.error_get_user), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {}
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

    private Boolean checkIfValid() {
        int error = 0;
        if(email.getText() == null || !isValidEmail(email.getText().toString())) {
            email.setError(getResources().getString(R.string.error_invalid_email));
            error = 1;
        }
        if(firstname.getText() == null || firstname.getText().length() == 0) {
            firstname.setError(getResources().getString(R.string.error_firstname_required));
            error = 1;
        }
        if(lastname.getText() == null || lastname.getText().length() == 0) {
            lastname.setError(getResources().getString(R.string.error_lastname_required));
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

    private  void displayUser() {
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
        title.setText(R.string.user_detail_activity);
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
