package com.sncf.itnovem.dotandroidapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.Models.User;
import com.sncf.itnovem.dotandroidapplication.services.API;
import com.sncf.itnovem.dotandroidapplication.services.DotService;
import com.sncf.itnovem.dotandroidapplication.services.ServiceGenerator;
import com.sncf.itnovem.dotandroidapplication.utils.NetworkUtil;
import com.sncf.itnovem.dotandroidapplication.utils.SecurityUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Journaud Nicolas on 20/04/16.
 */
public class SignUpActivity extends Activity {

    static public String TAG = "SIGNUP";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_layout);

        activity = this;
        progress = new ProgressDialog(this);
        progress.setIndeterminateDrawable(getDrawable(R.drawable.circle_progressbar_custom));
        progress.setMessage(getResources().getString(R.string.loading));
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
                        User user = User.init(myUser.get("id").getAsInt(),myUser.get("attributes").getAsJsonObject(), password.getText().toString());
                        Toast.makeText(activity, getString(R.string.user_created), Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("user",user);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    } else {
                        Toast.makeText(activity, "Error : " + getResources().getString(R.string.error_sign_up), Toast.LENGTH_SHORT).show();
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

    private Boolean isFormValid() {
        if(firstname.getText() == null || firstname.getText().length() == 0) {
            firstname.setError(getResources().getString(R.string.error_firstname_required));
            return false;
        }
        if(lastname.getText() == null || lastname.getText().length() == 0) {
            lastname.setError(getResources().getString(R.string.error_lastname_required));
            return false;
        }
        if(!isValidEmail(email.getText().toString())) {
            email.setError(getResources().getString(R.string.error_invalid_email));
            return false;
        }
        if(!isValidPassword(password.getText().toString())) {
            password.setError(getResources().getString(R.string.error_invalid_password));
            return false;
        } else if(!isSamePassword(password.getText().toString(), passwordConfirm.getText().toString())) {
            password.setError(getResources().getString(R.string.error_invalid_confirm_password));
            passwordConfirm.setError(getResources().getString(R.string.error_invalid_confirm_password));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
