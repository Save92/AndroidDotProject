package com.sncf.itnovem.dotandroidapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sncf.itnovem.dotandroidapplication.Adapters.ListCommandRecyclerAdapter;
import com.sncf.itnovem.dotandroidapplication.Adapters.SimpleDividerItemDecoration;
import com.sncf.itnovem.dotandroidapplication.Models.VoiceCommand;
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
public class ListCommandActivity extends FragmentActivity implements ListCommandRecyclerAdapter.CallbackAdapter {


    static public String TAG = "LIST_COMMANDES";
    private Toolbar toolbarTop;
    private Activity activity;
    private ImageButton telecommandeBtn;
    private ImageButton listBtn;
    private ImageButton notificationsBtn;

    private RecyclerView mRecyclerView;
    private ListCommandRecyclerAdapter adapter;
    private List<VoiceCommand> commandeList;
    private ProgressBar progressBar;
    private Call<JsonObject> call;

    private DotService dotService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_command);
        activity = this;
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) findViewById(R.id.commandesRecycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        getCommandList();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void getCommandList() {
        if (NetworkUtil.checkDeviceConnected(this)) {
            commandeList = new ArrayList<>();
            // Appel list notifications
            dotService = ServiceGenerator.createService(DotService.class, API.RESTAPIURL);
            call = dotService.getListCommandes(CurrentUser.getToken(), CurrentUser.getEmail());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonArray myList = response.body().get("data").getAsJsonArray();
                        for (int i = 0; i < myList.size(); i++) {
                            JsonObject myCommandJson = myList.get(i).getAsJsonObject();
                            VoiceCommand thisCommand = VoiceCommand.init(myCommandJson);
                            commandeList.add(thisCommand);
                        }
                        adapter = new ListCommandRecyclerAdapter(activity, commandeList);
                        mRecyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(activity, "Error : " + getResources().getString(R.string.error_get_settings), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            try {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.info));

                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage(getResources().getString(R.string.error_network));
                final android.support.v7.app.AlertDialog alertDialog = builder.create();
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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
    public void onBackPressed() {
        Fragment selectedFragment = getSupportFragmentManager().findFragmentByTag("detailFrag");

        if(selectedFragment != null && selectedFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_out_right, R.anim.slide_out_right, R.anim.slide_out_right, R.anim.slide_out_right)
                    .remove(selectedFragment).commit();
            initToolbars();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        call.cancel();
    }

    public void initToolbars() {

        telecommandeBtn = (ImageButton) findViewById(R.id.telecommandeBtn);
        listBtn = (ImageButton) findViewById(R.id.listBtn);
        notificationsBtn = (ImageButton) findViewById(R.id.notificationsBtn);

        telecommandeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent telecomandeIntent = new Intent(activity, CommandActivity.class);
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

        toolbarTop = (Toolbar) findViewById(R.id.app_bar);
        if (CurrentUser.getAvatarPath() != null) {
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
        if (CurrentUser.getAdmin()) {
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
        title.setText(R.string.title_activity_list_command);
        title.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        toolbarTop.setTitle(null);
        toolbarTop.setNavigationIcon(null);

        setActionBar(toolbarTop);
        toolbarTop.setLogo(R.drawable.ic_home_white_36dp);

        View logoView = getToolbarLogoIcon(toolbarTop);
        logoView.setPadding(0, 0, 15, 0);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logo clicked
//                Intent homeIntent = new Intent(activity, MainActivity.class);
//                startActivity(homeIntent);
                activity.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initToolbars();
    }

    public static View getToolbarLogoIcon(Toolbar toolbar){
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        toolbar.findViewsWithText(potentialViews,contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        View logoIcon = null;
        if(potentialViews.size() > 0){
            logoIcon = potentialViews.get(0);
        }
        if(hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }

    /** methode de callback appelee quand le clic est declenche dans l adapter */
    @Override
    public void showDetail(VoiceCommand c) {
        final Fragment fragTest = getSupportFragmentManager().findFragmentByTag("detailFrag");
        if(fragTest == null) {
            CommandListDetailFragment frag = CommandListDetailFragment.newInstance(c);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, 0, 0,R.anim.slide_out_right)
                    .replace(R.id.commande_container_frame, frag, "detailFrag")
                    .addToBackStack("detailFrag")
                    .commit();
        } else {
            CommandListDetailFragment frag = CommandListDetailFragment.newInstance(c);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, 0, 0,R.anim.slide_out_right)
                    .replace(R.id.commande_container_frame, frag, "detailFrag")
                    .addToBackStack("detailFrag")
                    .commit();
        }
    }
}
