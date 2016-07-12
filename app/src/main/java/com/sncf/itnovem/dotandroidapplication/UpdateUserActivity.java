package com.sncf.itnovem.dotandroidapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.services.API;
import com.sncf.itnovem.dotandroidapplication.utils.NetworkUtil;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sncf.itnovem.dotandroidapplication.Models.User;
import com.sncf.itnovem.dotandroidapplication.services.DotService;
import com.sncf.itnovem.dotandroidapplication.services.ServiceGenerator;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;
import com.sncf.itnovem.dotandroidapplication.utils.Images;
import com.sncf.itnovem.dotandroidapplication.utils.RoundedAvatarDrawable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Journaud Nicolas on 20/04/16.
 */
public class UpdateUserActivity extends Activity {
    private int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "UPDATE_USER";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Activity activity;
    private ImageView avatar;
    private String avatarPath;
    private CurrentUser currentUser;
    private EditText firstname;
    private EditText lastname;
    private EditText email;

    private SharedPreferences session;
    private SharedPreferences.Editor editor;

    private EditText oldPsw;
    private EditText newPsw;
    private EditText newPswConfirm;
    private Button saveBtn;
    private DotService dotService;
    boolean saved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        activity = this;
        currentUser = CurrentUser.getInstance(getApplicationContext());
        session = getSharedPreferences(CurrentUser.SESSION_FILENAME, MODE_PRIVATE);
        initUser();
        saveBtn = (Button) findViewById(R.id.saveBtn);
        oldPsw = (EditText) findViewById(R.id.oldPswText);
        newPsw = (EditText) findViewById(R.id.newPasswordText);
        newPswConfirm = (EditText) findViewById(R.id.newPasswordConfirmText);

        verifyStoragePermissions(activity);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfValid()) {
                    saveCurrentUser();
                    finish();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initToolbar();
    }


    private void saveCurrentUser() {
        CurrentUser.setEmail(email.getText().toString());
        CurrentUser.setLastName(lastname.getText().toString());
        CurrentUser.setFirstName(firstname.getText().toString());
        if(avatarPath != null && avatarPath.length() > 0) {
            CurrentUser.setAvatarPath(avatarPath);
        }
        if(newPsw.getText() != null && newPsw.getText().toString().length() > 0) {
            CurrentUser.setPassword(newPsw.getText().toString());
        }
        if (NetworkUtil.checkDeviceConnected(this)) {
            User myUser = new User(CurrentUser.getInstance(activity));

            dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL);
            Call<JsonObject> call = dotService.updateUser(myUser.getUserId(), myUser, CurrentUser.getToken(), CurrentUser.getEmail());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject myUserJson = response.body().get("data").getAsJsonObject();
                        User user = User.init(myUserJson.get("id").getAsInt(), myUserJson.get("attributes").getAsJsonObject());
                        CurrentUser.setCurrentUser(user);
                        CurrentUser.saveCurrentUserSession();
                    } else {
                        Toast.makeText(activity, "Error : " + getResources().getString(R.string.error_update_user), Toast.LENGTH_SHORT).show();
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
        if(oldPsw.getText() == null || oldPsw.getText().length() == 0) {
            if( (newPsw.getText() != null && newPsw.getText().length() > 0)|| (newPswConfirm.getText() != null && newPswConfirm.getText().length() > 0)) {
                error = 1;
                oldPsw.setError(getResources().getString(R.string.error_missing_password));
            } else if ((newPsw.getText() != null && newPsw.getText().length() > 0)|| (newPswConfirm.getText() != null && newPswConfirm.getText().length() > 0) && !isValidPassword(oldPsw.getText().toString())){
                error = 1;
                oldPsw.setError(getResources().getString(R.string.error_invalid_password));
            }
        }
        if((newPsw.getText() != null && newPsw.getText().length() > 0)|| (newPswConfirm.getText() != null && newPswConfirm.getText().length() > 0)) {

            error = 1;
            if(oldPsw.getText() == null || oldPsw.getText().length() == 0) {
                error = 1;
                oldPsw.setError(getResources().getString(R.string.error_missing_password));
            } else if (!CurrentUser.getPassword().equals(oldPsw.getText().toString())) {
                error = 1;
                oldPsw.setError(getResources().getString(R.string.error_missing_password));
            }
            if(!isValidPassword(newPsw.getText().toString())) {
                error = 1;
                newPsw.setError(getResources().getString(R.string.error_invalid_old_password));
            }
            if(!isValidPassword(newPswConfirm.getText().toString())) {
                error = 1;
                newPswConfirm.setError(getResources().getString(R.string.error_invalid_password));
            }
            if(!isSamePassword(newPsw.getText().toString(), newPswConfirm.getText().toString())) {
                error = 1;
                newPsw.setError(getResources().getString(R.string.error_invalid_confirm_password));
                newPswConfirm.setError(getResources().getString(R.string.error_invalid_confirm_password));
            }
        } else if( (newPsw.getText() != null && newPswConfirm.getText() == null)) {
            error = 1;
            newPswConfirm.setError(getResources().getString(R.string.error_missing_password));
        } else if( (newPsw.getText() == null && newPswConfirm.getText() != null)) {
            error = 1;
            newPsw.setError(getResources().getString(R.string.error_missing_password));
        }
        if(error == 0) {
            return true;
        }
        return false;
    }

    private boolean isSamePassword(String pass, String passConfirm) {
        if (pass != null && pass.equals(passConfirm)) {
            return true;
        }
        return false;
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

    private void initUser() {
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastnameText);
        email = (EditText) findViewById(R.id.emailValue);
        initGetAvatar();


        if(CurrentUser.getEmail() != null && CurrentUser.getEmail().length() > 0) {
            email.setText(CurrentUser.getEmail());
        }
        if(CurrentUser.getFirstName() != null && CurrentUser.getFirstName().length() > 0) {
            firstname.setText(CurrentUser.getFirstName());
        }
        if(CurrentUser.getLastName() != null &&  CurrentUser.getLastName().length() > 0) {
            lastname.setText(CurrentUser.getLastName());
        }
    }

    private void initToolbar() {
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.app_bar_profil);

        toolbarTop.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);
        TextView title = (TextView) toolbarTop.findViewById(R.id.app_bar_title);
        title.setText(R.string.profil_title);
        title.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        toolbarTop.setTitle(null);
        setActionBar(toolbarTop);
        toolbarTop.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initGetAvatar() {
        avatar = (ImageView) findViewById(R.id.avatar);
        android.view.ViewGroup.LayoutParams layoutParams = avatar.getLayoutParams();
        layoutParams.width = 200;
        layoutParams.height = 200;
        avatar.setLayoutParams(layoutParams);

        if(CurrentUser.getAvatarPath() != null && CurrentUser.getAvatarPath().length() != 0) {
            Uri uriLoad = Uri.parse("file://" + CurrentUser.getAvatarPath());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriLoad);
                int maxWidth = bitmap.getWidth();
                int maxHeight = bitmap.getHeight();
                if(maxHeight > 250) {
                    maxHeight = 250;
                }
                if (maxWidth > 250) {
                    maxWidth = 250;
                }
                RoundedAvatarDrawable img = new RoundedAvatarDrawable(Images.scaleCenterCrop(bitmap, maxHeight, maxWidth));
                avatar.setBackground(null);
                avatar.setBackground(img);
                avatar.setImageBitmap(null);
                avatar.setMaxHeight(90);
            } catch (IOException e) {}
        } else {
            avatar.setImageResource(R.drawable.account_circle_fushia);
        }
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Sélectionner une image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            avatarPath = getRealPathFromURI(activity, uri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                int maxWidth = bitmap.getWidth();
                int maxHeight = bitmap.getHeight();
                if(maxHeight > 250) {
                    maxHeight = 250;
                }
                if (maxWidth > 250) {
                    maxWidth = 250;
                }
                RoundedAvatarDrawable img = new RoundedAvatarDrawable(Images.scaleCenterCrop(bitmap, maxHeight, maxWidth));
                avatar.setBackground(img);
                avatar.setImageBitmap(null);
                avatar.setMaxHeight(90);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
