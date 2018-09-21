package kapp.chat.db.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import kapp.chat.db.KEY;

/**
 * Created by Arunraj on 11/23/2017.
 */

@IgnoreExtraProperties
public class ChatRoom {

    public String chat_room_id, creater_id;
    public Object created_date;

    public ChatRoom() {
    }

    public ChatRoom(String chat_room_id, String creater_id) {
        this.chat_room_id = chat_room_id;
        this.creater_id = creater_id;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put(KEY.CHAT_ROOM_ID, chat_room_id);
        result.put(KEY.CREATER_ID, creater_id);
        result.put(KEY.CREATED_DATE, ServerValue.TIMESTAMP);
        return result;
    }

}
