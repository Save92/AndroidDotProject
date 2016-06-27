package com.sncf.itnovem.dotandroidapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Journaud Nicolas on 28/07/15.
 */
public class NetworkUtil {
    /**
     * Constructeur privé de la classe.
     * Empèche son instanciation (=> classe statique).
     */
    private NetworkUtil() {

    }

    public static boolean checkDeviceConnected(Activity activity) {
        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

}
