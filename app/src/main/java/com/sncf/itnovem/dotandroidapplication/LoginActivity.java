package com.sncf.itnovem.dotandroidapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.Models.User;
import com.sncf.itnovem.dotandroidapplication.services.API;
import com.sncf.itnovem.dotandroidapplication.services.DotService;
import com.sncf.itnovem.dotandroidapplication.services.ServiceGenerator;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;
import com.sncf.itnovem.dotandroidapplication.utils.NetworkUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Journaud Nicolas on 25/01/16.
 */
public class LoginActivity extends Activity {

    public static final String TAG = "LOGIN";

    private android.widget.Toolbar toolbar;
    private TextView email;
    private String emailText;
    private TextView password;
    private String passwordText;
    private Button login;
    private Button signUp;
    private CheckBox rememberMe;
    private boolean errors;
    private ProgressDialog progress;
    private DotService dotService;
    private Activity activity;
    private SharedPreferences session;
    private static SharedPreferences.Editor editor;
    private CurrentUser currentUser;
    private Boolean rememberCheck;
    private Boolean autoConnect;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity_layout);
        activity = this;
        session = getSharedPreferences(CurrentUser.SESSION_FILENAME, MODE_PRIVATE);
        currentUser = CurrentUser.getInstance(getApplicationContext());
        toolbar = (android.widget.Toolbar) findViewById(R.id.login_bar);

        TextView title = (TextView) toolbar.findViewById(R.id.app_bar_title);
        title.setText(R.string.title_activity_login);
        toolbar.setTitle(null);
        toolbar.getMenu();
        setActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        email = (TextView) findViewById(R.id.emailText);
        password = (TextView) findViewById(R.id.passwordText);
        login = (Button) findViewById(R.id.loginBtn);
        signUp = (Button) findViewById(R.id.signupBtn);
        rememberMe = (CheckBox) findViewById(R.id.remember_check);
        progress = new ProgressDialog(this);
        progress.setIndeterminateDrawable(getDrawable(R.drawable.circle_progressbar_custom));
        progress.setMessage(getResources().getString(R.string.loading));
        rememberCheck =  session.getBoolean(CurrentUser.REMEMBER_KEY, false);
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked) {
                    rememberCheck = true;
                } else {
                    rememberCheck = false;
                    session.getBoolean(CurrentUser.LOGIN_SHOW_KEY, true);
                }
            }
        });
        autoConnect = session.getBoolean(CurrentUser.RECONNECT_KEY, false);
        if(rememberCheck) {
            rememberMe.setChecked(rememberCheck);
            email.setText(session.getString(CurrentUser.EMAIL_KEY, "").replaceAll("\"", ""));
            password.setText(CurrentUser.getSavedPassword());
            if(autoConnect) {
                progressBar.setVisibility(View.INVISIBLE);
                if (!session.getString(CurrentUser.EMAIL_KEY, "").replaceAll("\"", "").isEmpty() && !CurrentUser.getSavedPassword().isEmpty()) {
                    if (autoConnect) {
                        emailText = email.getText().toString();
                        passwordText = password.getText().toString();
                        login();
                    }
                } else {
                    Toast.makeText(activity, getString(R.string.error_get_user_info), Toast.LENGTH_SHORT).show();
                }
            }
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errors = false;
                emailText = email.getText().toString();

                if (!isValidEmail(emailText)) {
                    email.setError(getResources().getString(R.string.error_invalid_email));
                    errors = true;
                }

                passwordText = password.getText().toString();

                if (!isValidPassword(passwordText)) {
                    password.setError(getResources().getString(R.string.error_invalid_password));
                    errors = true;
                }
                if(rememberCheck && session.getBoolean(CurrentUser.LOGIN_SHOW_KEY, true)) {
                    openDialog();
                } else {
                    if (!errors) {
                        login();
                    }
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(activity, SignUpActivity.class);
                startActivityForResult(signUpIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                User user = data.getParcelableExtra("user");
                email.setText(user.getEmail());
                password.setText(user.getPassword());
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void login() {
        if (NetworkUtil.checkDeviceConnected(this)) {
            if (email.getText() != null && email.getText().length() > 0 && password.getText() != null && password.getText().length() > 0) {
                dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL, email.getText().toString(), password.getText().toString());
            }
            dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL);
            Call<JsonObject> call = dotService.login(emailText, passwordText);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject userJson = response.body().get("data").getAsJsonObject();
                        CurrentUser.init(userJson.get("id").getAsInt(), userJson.get("attributes").getAsJsonObject());
                        if (rememberMe.isChecked()) {
                            CurrentUser.saveRemember(true);
                            CurrentUser.savePassword(passwordText);
                            CurrentUser.saveCurrentUserSession();
                        } else {
                            editor = session.edit();
                            editor.putBoolean(CurrentUser.LOGIN_SHOW_KEY, true);
                            editor.putBoolean(CurrentUser.REMEMBER_KEY, false);
                            editor.apply();
                        }
                        Intent mainIntent = new Intent(activity, MainActivity.class);
                        if (CurrentUser.getIsApproved()) {
                            progressBar.setVisibility(View.GONE);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            showDialog();
                        }
                    } else {
                        if(response.code()==401){
                            Toast.makeText(activity, "Error : " + getResources().getString(R.string.error_connexion_authorized), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Error : " + getResources().getString(R.string.error_connexion), Toast.LENGTH_SHORT).show();
                        }
                     }
                }
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
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

    public void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.alert_text));
        alertDialogBuilder.setNegativeButton(getString(R.string.ok),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 6) {
            return true;
        }
        return false;
    }

    private void openDialog(){
        LayoutInflater inflater = LayoutInflater.from(activity);
        View subView = inflater.inflate(R.layout.login_choice, null);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        final CheckBox dontShow = (CheckBox) subView.findViewById(R.id.checkBoxDontshowAgain);
        builder.setTitle(getString(R.string.login_choice));
        builder.setView(subView);

        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor = session.edit();
                editor.putBoolean(CurrentUser.RECONNECT_KEY, true);
                editor.apply();
                if(dontShow.isChecked()) {
                    editor.putBoolean(CurrentUser.LOGIN_SHOW_KEY, false);
                    editor.apply();
                }
                if (!errors) {
                    login();
                }
            }
        });

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor = session.edit();
                editor.putBoolean(CurrentUser.RECONNECT_KEY, false);
                editor.apply();
                if (!errors) {
                    login();
                }
            }
        });
        builder.show();
    }

}
