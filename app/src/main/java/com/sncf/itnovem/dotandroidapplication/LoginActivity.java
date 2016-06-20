package com.sncf.itnovem.dotandroidapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * Created by Save92 on 25/01/16.
 */
public class LoginActivity extends Activity {

    public static final String TAG = "LOGINLoginActivity";

    private android.widget.Toolbar toolbar;
    private TextView email;
    private String emailText;
    private TextView password;
    private String passwordText;
    private Button login;
    private Button signUp;
    private CheckBox rememberMe;
    private boolean errors;
    private Toast toast;
    private ProgressDialog progress;
    private DotService dotService;
    private Activity activity;
    private SharedPreferences session;
    private SharedPreferences.Editor editor;
    private AlertDialog alert;
    private CurrentUser currentUser;

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


        email = (TextView) findViewById(R.id.emailText);
        password = (TextView) findViewById(R.id.passwordText);
        login = (Button) findViewById(R.id.loginBtn);
        signUp = (Button) findViewById(R.id.signupBtn);
        rememberMe = (CheckBox) findViewById(R.id.remember_check);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        progress = new ProgressDialog(this);
        progress.setIndeterminateDrawable(getDrawable(R.drawable.circle_progressbar_custom));
        progress.setMessage(getResources().getString(R.string.loading));
        Boolean rememberCheck =  session.getBoolean(CurrentUser.REMEMBER_KEY, false);
        Log.v(TAG, String.valueOf(rememberCheck));
        if(rememberCheck) {
            rememberMe.setChecked(rememberCheck);
            email.setText(session.getString(CurrentUser.EMAIL_KEY, "").replaceAll("\"", ""));
            password.setText(CurrentUser.getSavedPassword());
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errors = false;
                emailText = email.getText().toString();

                if (!isValidEmail(emailText)) {
                    email.setError(getResources().getString(R.string.errorInvalidEmail));
                    errors = true;
                }

                passwordText = password.getText().toString();

                if (!isValidPassword(passwordText)) {
                    password.setError(getResources().getString(R.string.errorInvalidPassword));
                    errors = true;
                }

                if (!errors) {
                    login();
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(activity, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        if(CurrentUser.getInstance(activity) != null &&  CurrentUser.isLogin()) {
            Log.v(TAG, "isLogin");
            //TODO Appel API pour vérifier si le token est valide
            // redirect
        } else if (CurrentUser.awake()) {

            Log.v(TAG, "awake");
            //TODO Appel API pour vérifier si le token est valide
            // redirect
        } else {
            Log.v(TAG, "login");
            if(rememberMe.isChecked()) {
                CurrentUser.saveCurrentUserSession();
            }
        }

    }

    private void login() {
//        CurrentUser.setAdmin(false);
//        Intent mainIntent = new Intent(activity, MainActivity.class);
//        startActivity(mainIntent);

        if (NetworkUtil.checkDeviceConnected(this)) {
           // progress.show();
            Log.v(TAG, String.valueOf(rememberMe.isChecked()));
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
                        Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show();
                        if (rememberMe.isChecked()) {
                            Log.v(TAG, "RememberMe");
                            CurrentUser.saveRemember(true);
                            CurrentUser.savePassword(passwordText);
                            CurrentUser.saveCurrentUserSession();
                        }
                        Intent mainIntent = new Intent(activity, MainActivity.class);
                        if (CurrentUser.getIsApproved()) {
                            startActivity(mainIntent);
                            finish();
                        } else {
                            showDialog();
                        }
                    } else {
                        Toast.makeText(activity, "Error : " + getResources().getString(R.string.errorConnexion), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.v("Login", call.toString());

                }
            });
        }
    }


//            dotService.basicLogin(new Callback<User>() {
//                @Override
//                public void success(User user, Response response) {
//                    //Log.v(TAG, "Sucess");
//                    progress.dismiss();
//
//                    //Try to get response body
//                    BufferedReader reader = null;
//                    StringBuilder sb = new StringBuilder();
//                    try {
//
//                        reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
//
//                        String line;
//
//                        try {
//                            while ((line = reader.readLine()) != null) {
//                                sb.append(line);
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }


                   // String result = sb.toString();
                   // Log.v(TAG, "User" + user.getLastname());


//                    CurrentUser currentUser = new CurrentUser(user);
//                    Intent mainIntent = new Intent(activity, MainActivity.class);
//                    mainIntent.putExtra("user", currentUser);
//                    if(user.getApproved()) {
//                        startActivity(mainIntent);
//                        finish();
//                    } else {
//                        showDialog();
//                    }
//
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    progress.dismiss();
//                    toast.setText(getResources().getString(R.string.errorConnexion));
//                    toast.show();
//                    //Log.v(TAG, "Erreur login");
//                }
//            });
//        } else {
//            toast.setText(getResources().getString(R.string.errorNetwork));
//            toast.show();
//        }
//    }


    public void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.alert_text));
        alertDialogBuilder.setNegativeButton("Ok",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public boolean verifyCheck() {
        return session.getBoolean(CurrentUser.REMEMBER_KEY, false);
    }

//    public boolean checkIfUserIsLoggued() {
//        Log.v(TAG, "checkIfUserIsLoggued Enter");
//        if (session.contains(CurrentUser.USERID_KEY) && session.contains(CurrentUser.TOKEN_KEY) && session.contains(CurrentUser.USERFIRSTNAME_KEY)
//                && session.contains(CurrentUser.EMAIL_KEY) && session.contains(CurrentUser.USERLASTNAME_KEY)) {
//
//            Log.v(TAG, "checkIfUserIsLoggued : true");
//            return true;
//        }
//
//        Log.v(TAG, "checkIfUserIsLoggued : false");
//        return false;
//    }


//    public void saveUserSession(User user, String response) {
//        Log.e(TAG, "SaveUserSession");
//        // ID du user récupéré de l'api n'est pas bon
//        if (user.getUserId() == null || user.getUserId() == 0)
//            return;
//
//        // Mise à jour des Préférences si différent de l'existant
//        if (session.getInt(CurrentUser.USERID_KEY, 0) != user.getUserId()) {
//            Log.v(TAG, "error email");
//            if (user.getEmail() != null && !user.getEmail().isEmpty())
//                editor.putString(CurrentUser.EMAIL_KEY, user.getEmail());
//            else
//                Log.e(TAG, "Error email");
//            if (user.getUserId() != null && !user.getUserId().toString().isEmpty())
//                editor.putInt(CurrentUser.USERID_KEY, user.getUserId());
//            else
//                Log.e(TAG, "Error userId");
//            if (user.getLastname() != null && !user.getLastname().isEmpty())
//                editor.putString(CurrentUser.USERLASTNAME_KEY, user.getLastname());
//            else
//                Log.e(TAG, "Error firstname");
//            if (user.getFirstname() != null && !user.getFirstname().isEmpty())
//                editor.putString(CurrentUser.USERFIRSTNAME_KEY, user.getFirstname());
//            else
//                Log.e(TAG, "Error token");
//            if (user.getToken() != null && !user.getToken().isEmpty())
//                editor.putString(CurrentUser.TOKEN_KEY, user.getToken());
//            else
//                Log.e(TAG, "Error admin");
//
//            editor.putBoolean(CurrentUser.REMEMBER_KEY, rememberMe.isChecked());
//
//            editor.apply();
//
//            // Mise à jour de la classe Constants d'après la dernière valeur de l'api
//
//            Log.v(TAG, "userId :" + user.getUserId().toString());
//            Log.v(TAG, "name : " + user.getLastname());
//            //Log.v(TAG, "isSuperAdmin : " + user.getGod_mode().toString());
//            if (user.getUserId() != null)
//                CurrentUser.setCurrentUser(user, true);
//            else
//                Log.v(TAG, "userId : " + user.getUserId());
//            if (user.getToken() != null) {
//                Log.v(TAG, "Save token :" + user.getToken());
//                CurrentUser.setToken(user.getToken());
//
//                Log.v(TAG, "Current token :" + CurrentUser.getToken());
//            } else {
//
//                Log.v(TAG, "getAuthentication_token : " + user.getToken());
//                toast.setText("Erreur lors de la connexion : token Invalid");
//                toast.show();
//            }
//
//        } else { // Mise à jour de la classe Constants d'après les Préférences
//            getUserByPreferences();
//
//        }
//    }

    public void reconnexion() {
        toast.setText("Vous devez vous reconnecter");
        toast.show();
        Bundle bundle = new Bundle();
        onCreate(bundle);
    }

//    public void getUserByPreferences() {
//        Log.v(TAG, "getUserByPreferences");
//        CurrentUser.setCurrentUserId(session.getInt(CurrentUser.USERID_KEY, 0));
//        CurrentUser.setEmail(session.getString(CurrentUser.EMAIL_KEY, ""));
//        CurrentUser.setToken(session.getString(CurrentUser.TOKEN_KEY, ""));
//        CurrentUser.setLastName(session.getString(CurrentUser.USERLASTNAME_KEY, ""));
//        CurrentUser.setFirstName(session.getString(CurrentUser.USERFIRSTNAME_KEY, ""));
//        CurrentUser.setAvatarPath(session.getString(CurrentUser.USERAVATAR_KEY, ""));
//        if (CurrentUser.getEmail().isEmpty() || CurrentUser.getToken().isEmpty()) {
//            editor.putInt(CurrentUser.USERID_KEY, 0);
//            editor.apply();
//            reconnexion();
//        }
//    }

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



}
