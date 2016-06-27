package com.sncf.itnovem.dotandroidapplication.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Journaud Nicolas on 13/04/16.
 */
 public class DateUtil {
     public static String parseJsonDate(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
        Date d = null;
        try {
            d = sdf.parse(strDate);
        } catch ( ParseException e ) {
            e.printStackTrace();
        }
        String formattedTime = output.format(d);
        return  formattedTime;
    }

    public static String parseForJsonDate(String strDate) {
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.FRANCE);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
        Date d = null;
        try {
            d = sdf.parse(strDate);
        } catch ( ParseException e ) {
            e.printStackTrace();
        }
        String formattedTime = output.format(d);
        return  formattedTime;
    }
}
