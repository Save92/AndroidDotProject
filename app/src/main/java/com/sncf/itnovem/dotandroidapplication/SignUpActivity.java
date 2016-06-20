package com.sncf.itnovem.dotandroidapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.Models.User;
import com.sncf.itnovem.dotandroidapplication.services.API;
import com.sncf.itnovem.dotandroidapplication.services.APIError;
import com.sncf.itnovem.dotandroidapplication.services.DotService;
import com.sncf.itnovem.dotandroidapplication.services.ErrorUtils;
import com.sncf.itnovem.dotandroidapplication.services.ServiceGenerator;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;
import com.sncf.itnovem.dotandroidapplication.utils.NetworkUtil;
import com.sncf.itnovem.dotandroidapplication.utils.SecurityUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends Activity {

    static public String TAG = "LOGINSIGNUP";

    private Activity activity;
    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText password;
    private String passwordText;
    private EditText passwordConfirm;
    private Button signUp;
    private DotService dotService;
    private ProgressDialog progress;
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_layout);

        activity = this;
        progress = new ProgressDialog(this);
        progress.setIndeterminateDrawable(getDrawable(R.drawable.circle_progressbar_custom));
        progress.setMessage(getResources().getString(R.string.loading));
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        firstname = (EditText) findViewById(R.id.firtsnameText);
        lastname = (EditText) findViewById(R.id.lastnameText);
        email = (EditText) findViewById(R.id.emailText);
        password = (EditText) findViewById(R.id.passwordText);
        passwordConfirm = (EditText) findViewById(R.id.passwordConfirmText);

        signUp = (Button) findViewById(R.id.signupBtn);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFormValid()) {
                    signUp();
                }
            }
        });

    }

    private void signUp() {

        if (NetworkUtil.checkDeviceConnected(this)) {
            progress.show();
            User newUser = new User();
            newUser.setFirstname(firstname.getText().toString());
            newUser.setLastname(lastname.getText().toString());
            newUser.setEmail(email.getText().toString());
            newUser.setPassword(SecurityUtils.sha256(password.getText().toString()));
            passwordText = password.getText().toString();
            dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL,email.getText().toString(), SecurityUtils.sha256(password.getText().toString()));
            Call<JsonObject> call = dotService.createUser(newUser);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        progress.dismiss();
                            JsonObject myUser = response.body().get("data").getAsJsonObject();
                            Toast.makeText(activity, "User created", Toast.LENGTH_SHORT).show();
                        //TODO passer les infos au login
                            finish();
                    } else {
                        Toast.makeText(activity, "Error : " + getResources().getString(R.string.errorSignUp), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.v(TAG, t.toString());

                }
            });
        } else {
            toast.setText(getResources().getString(R.string.errorNetwork));
            toast.show();
        }

    }

    private Boolean isFormValid() {
        if(firstname.getText() == null || firstname.getText().length() == 0) {
            firstname.setError(getResources().getString(R.string.errorFirstnameRequired));
            return false;
        }
        if(lastname.getText() == null || lastname.getText().length() == 0) {
            lastname.setError(getResources().getString(R.string.errorLastnameRequired));
            return false;
        }
        if(!isValidEmail(email.getText().toString())) {
            email.setError(getResources().getString(R.string.errorInvalidEmail));
            return false;
        }
        if(!isValidPassword(password.getText().toString())) {
            password.setError(getResources().getString(R.string.errorInvalidPassword));
            return false;
        } else if(!isSamePassword(password.getText().toString(), passwordConfirm.getText().toString())) {
            password.setError(getResources().getString(R.string.errorInvalidConfirmPassword));
            passwordConfirm.setError(getResources().getString(R.string.errorInvalidConfirmPassword));
            return false;
        }

        return true;
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

    // validating password with retype password
    private boolean isSamePassword(String pass, String passConfirm) {
        if (pass != null && pass.equals(passConfirm)) {
            return true;
        }
        return false;
    }

}
