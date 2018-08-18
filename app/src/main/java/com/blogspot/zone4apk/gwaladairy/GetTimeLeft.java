package com.blogspot.zone4apk.gwaladairy;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.ServerValue;

/**
 * Created by AMIT on 6/3/2018.
 */

public class GetTimeLeft extends Application {


        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeLeft(long time, long now) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
            return 30-(diff / DAY_MILLIS)+"" ;

    }


}
