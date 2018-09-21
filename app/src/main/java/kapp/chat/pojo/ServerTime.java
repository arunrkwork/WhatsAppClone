package kapp.chat.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by Arunraj on 11/22/2017.
 */

public class ServerTime {

    HashMap<String, Object> timestampCreated;
    Object object;

    public ServerTime(Object object) {
        this.object = object;
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", this.object);
        this.timestampCreated = timestampNow;
    }

    public ServerTime() {
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }

}
