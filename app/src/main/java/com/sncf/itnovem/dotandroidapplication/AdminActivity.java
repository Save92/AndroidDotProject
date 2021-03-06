package com.sncf.itnovem.dotandroidapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.Adapters.SimpleDividerItemDecoration;
import com.sncf.itnovem.dotandroidapplication.Adapters.UserRecyclerAdapter;
import com.sncf.itnovem.dotandroidapplication.Models.User;
import com.sncf.itnovem.dotandroidapplication.services.API;
import com.sncf.itnovem.dotandroidapplication.services.DotService;
import com.sncf.itnovem.dotandroidapplication.services.ServiceGenerator;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;
import com.sncf.itnovem.dotandroidapplication.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Journaud Nicolas on 20/04/16.
 */
public class AdminActivity extends AppCompatActivity implements UserRecyclerAdapter.CallbackAdapter{
    static public String TAG = "ADMIN";

    private Toolbar toolbar;
    private Activity activity;
    private ProgressBar progressBar;
    private List<User> userList;
    private RecyclerView userRecyclerView;
    private UserRecyclerAdapter adapter;
    private Call<JsonObject> call;

    private DotService dotService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!CurrentUser.getAdmin()) {
            finish();
        }
        setContentView(R.layout.activity_admin);
        activity = this;
        userRecyclerView = (RecyclerView) findViewById(R.id.usersRecycleView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void getUserList() {
        if (NetworkUtil.checkDeviceConnected(this)) {
            userList = new ArrayList<>();
            adapter = new UserRecyclerAdapter(activity, userList);
            userRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL);
            call = dotService.getUsers(CurrentUser.getToken(), CurrentUser.getEmail());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonArray myList = response.body().get("data").getAsJsonArray();
                        for (int i = 0; i < myList.size(); i++) {
                            JsonObject myUserJson = myList.get(i).getAsJsonObject();
                            User myUser = User.init(myUserJson.get("id").getAsInt(), myUserJson.get("attributes").getAsJsonObject());
                            userList.add(myUser);
                        }
                        adapter = new UserRecyclerAdapter(activity, userList);
                        userRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(activity, "Error :" + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
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

    @Override
    public void showDetail(User u) {
        Intent myIntent = new Intent(this, UserDetailActivity.class);
        myIntent.putExtra("user", u);
        startActivity(myIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        initToolbar();
        getUserList();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        call.cancel();
    }
    private void initToolbar() {
        android.widget.Toolbar toolbarTop = (android.widget.Toolbar) findViewById(R.id.app_bar_profil);
        toolbarTop.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);
        TextView title = (TextView) toolbarTop.findViewById(R.id.app_bar_title);
        title.setText(R.string.admin_title);
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
}
