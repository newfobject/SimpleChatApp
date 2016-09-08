package com.newfobject.simplechatapp;


import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static String getDateFromMillis(long time) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat dateFormat;

        if (time < (currentTime - TimeUnit.DAYS.toMillis(100))) {
            dateFormat = new SimpleDateFormat("MMM/dd/yyyy");
        } else if (!DateUtils.isToday(time)) {
            dateFormat = new SimpleDateFormat("MMM dd, kk:mm");
        } else {
            dateFormat = new SimpleDateFormat("kk:mm");
        }
        return dateFormat.format(new Date(time));

    }
}
