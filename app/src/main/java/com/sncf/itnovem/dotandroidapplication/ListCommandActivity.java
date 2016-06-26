package com.sncf.itnovem.dotandroidapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.sncf.itnovem.dotandroidapplication.Adapters.RecyclerAdapter;
import com.sncf.itnovem.dotandroidapplication.Adapters.SimpleDividerItemDecoration;
import com.sncf.itnovem.dotandroidapplication.Models.Commande;
import com.sncf.itnovem.dotandroidapplication.Models.VoiceCommand;
import com.sncf.itnovem.dotandroidapplication.services.API;
import com.sncf.itnovem.dotandroidapplication.services.APIError;
import com.sncf.itnovem.dotandroidapplication.services.DotService;
import com.sncf.itnovem.dotandroidapplication.services.ErrorUtils;
import com.sncf.itnovem.dotandroidapplication.services.ServiceGenerator;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;
import com.sncf.itnovem.dotandroidapplication.utils.NetworkUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** implementation de l interface de l adapter... */
public class ListCommandActivity extends FragmentActivity implements ListCommandRecyclerAdapter.CallbackAdapter {


    static public String TAG = "LOGINLIST_COMMANDES";
    private Toolbar toolbarTop;
    private Toolbar bottomtoolbar;
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
        initToolbars();

        mRecyclerView = (RecyclerView) findViewById(R.id.commandesRecycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));



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
        getCommandList();

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
                        for (int i = 0; i < myList.size() - 1; i++) {
                            JsonObject myCommandJson = myList.get(i).getAsJsonObject();

                            Log.v(TAG, "myCommandJson " + myCommandJson.toString());

                            VoiceCommand thisCommand = VoiceCommand.init(myCommandJson);
                            commandeList.add(thisCommand);
                        }
                        adapter = new ListCommandRecyclerAdapter(activity, commandeList);
                        mRecyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        //Log.v(TAG, response.body().getFirstname());
                    } else {
                        Toast.makeText(activity, "Error : " + getResources().getString(R.string.errorGetSettings), Toast.LENGTH_SHORT).show();

                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.v(TAG, t.toString());

                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            try {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("Info");

                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage(getResources().getString(R.string.errorNetwork));
                final android.support.v7.app.AlertDialog alertDialog = builder.create();
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                builder.show();
            }
            catch(Exception e)
            {
                Log.d(TAG, "Show Dialog: "+e.getMessage());
            }
        }

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        call.cancel();
    }

    public void initToolbars() {
        toolbarTop = (Toolbar) findViewById(R.id.app_bar);
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
        if(CurrentUser.getAdmin()) {
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

        setActionBar(toolbarTop);
        toolbarTop.setLogo(R.drawable.ic_home_black_24dp);
        View logoView = getToolbarLogoIcon(toolbarTop);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logo clicked
                Intent homeIntent = new Intent(activity, MainActivity.class);
                startActivity(homeIntent);
                activity.finish();
            }
        });

        Toolbar toolbarBottom = (Toolbar) findViewById(R.id.bottomToolbar);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        initToolbars();
    }

    public static View getToolbarLogoIcon(Toolbar toolbar){
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews,contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if(potentialViews.size() > 0){
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
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
                    /** Il faudra remplacer les deux drawables suivants pour y mettre tes propres animators */
                    .setCustomAnimations(R.anim.slide_in_right, 0, 0,R.anim.slide_out_right)
                    .replace(R.id.commande_container_frame, frag, "detailFrag")
                    .addToBackStack("detailFrag")
                    .commit();
        } else {
            CommandListDetailFragment frag = CommandListDetailFragment.newInstance(c);
            getSupportFragmentManager().beginTransaction()
                    /** Il faudra remplacer les deux drawables suivants pour y mettre tes propres animators */
                    .setCustomAnimations(R.anim.slide_in_right, 0, 0,R.anim.slide_out_right)
                    .replace(R.id.commande_container_frame, frag, "detailFrag")
                    .addToBackStack("detailFrag")
                    .commit();
        }



    }
}
