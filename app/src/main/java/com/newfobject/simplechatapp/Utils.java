package com.newfobject.simplechatapp;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static String getDateFromMillis(long time) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat dateFormat;

        if (time < (currentTime - TimeUnit.DAYS.toMillis(360))) {
            dateFormat = new SimpleDateFormat("MMM/dd/yyyy");
        } else if (time < (currentTime - TimeUnit.DAYS.toMillis(1))) {
            dateFormat = new SimpleDateFormat("MMM dd, kk:mm");
        } else {
            dateFormat = new SimpleDateFormat("kk:mm");

        }
        return dateFormat.format(new Date(time));

    }
}
