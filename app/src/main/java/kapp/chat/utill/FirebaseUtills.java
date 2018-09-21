package kapp.chat.utill;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arunraj on 11/17/2017.
 */

public class FirebaseUtills {


    public static long getServerTimeStamp(){
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", ServerValue.TIMESTAMP);
        L.i(" Server Time : " + map.get("timestamp").toString());
        return 0;
    }

    public static long getTimeStamp(Object timestamp){
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", timestamp);
        return (long) map.get("timestamp");
    }

    public static Calendar getLastSeenTime(Object timestamp){
        long time = getTimeStamp(timestamp);
        Calendar calendar = longToCalendar(time);
        return calendar;
    }

    public static Calendar longToCalendar(long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar;
    }

    public static Calendar getServerTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getServerTimeStamp());
        return calendar;
    }

}
