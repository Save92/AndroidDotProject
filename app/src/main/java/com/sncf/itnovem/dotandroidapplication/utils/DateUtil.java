package com.sncf.itnovem.dotandroidapplication.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Save92 on 13/04/16.
 */
 public class DateUtil {
    public static SimpleDateFormat dateFormatLocal = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
    //public static SimpleDateFormat dateFormatJson = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.sssZ");

    public static Date stringToDate(String dateString)
    {

        Date date = null;
        try {
            date = dateFormatLocal.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

     public static String parseDate(String strDate) {
        DateFormat readFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.FRANCE);

        Date date = null;
        try {
            date = readFormat.parse(strDate);
        } catch ( ParseException e ) {
            e.printStackTrace();
        }

        String formattedDate = "";
        if( date != null ) {
            formattedDate = dateFormatLocal.format( date );
        }
        return formattedDate;
    }

     public static String parseJsonDate(String strDate) {
         SimpleDateFormat dateFormatLocal = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

         Log.v("DATEUTIL", "dateUtil");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
        Date d = null;
        try {
            d = sdf.parse(strDate);
        } catch ( ParseException e ) {
            e.printStackTrace();
        }
        String formattedTime = output.format(d);
        Log.v("DATEUTIL", "formattedTime" + formattedTime);
        return  formattedTime;
    }

    public static String parseForJsonDate(String strDate) {
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

        Log.v("DATEUTIL", "dateUtil");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.FRANCE);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
        Date d = null;
        try {
            d = sdf.parse(strDate);
        } catch ( ParseException e ) {
            e.printStackTrace();
        }
        String formattedTime = output.format(d);
        Log.v("DATEUTIL", "formattedTime" + formattedTime);
        return  formattedTime;
    }

    public static String getCurrentTimeStamp() {
        Date now = new Date();
        String strDate = dateFormatLocal.format(now);
        return strDate;
    }
}
