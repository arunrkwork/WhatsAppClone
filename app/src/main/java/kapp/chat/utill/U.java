package kapp.chat.utill;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Arunraj on 11/7/2017.
 */

public class U {
    public static final String TAG = "Whatsapp";

    public static String convertTimeStampToDateTime(String timestamp) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String day, month, year, hour, min;

        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        year = String.valueOf(calendar.get(Calendar.YEAR));
        hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        min = String.valueOf(calendar.get(Calendar.MINUTE));

        String dateTime = s2d(day) + "-" + s2d(month) + "-" + s2d(year) + " " + s2d(hour) + ":" + s2d(min);

        return dateTime;
    }

    public static String convertTimeStampToTime(String timestamp) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String day, month, year, hour, min;

        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        year = String.valueOf(calendar.get(Calendar.YEAR));
        hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        min = String.valueOf(calendar.get(Calendar.MINUTE));

        String dateTime = s2d(hour) + ":" + s2d(min);

        return dateTime;
    }

    public static String s2d(String s) {
        return (s != null) ? ((s.length() == 1) ? ("0" + s) : s) : "00";
    }


    public static long daysBetween(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
    }

}
