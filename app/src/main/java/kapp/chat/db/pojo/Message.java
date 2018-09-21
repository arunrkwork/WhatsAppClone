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
public class Message {

    public String message_id, chat_room_id, creater_id, message_type, message_body, file_type, file_path;
    public Object created_date;

    public Message() {
    }

    public Message(String message_id, String chat_room_id, String creater_id, String message_type, String message_body, String file_type, String file_path) {
        this.message_id = message_id;
        this.chat_room_id = chat_room_id;
        this.creater_id = creater_id;
        this.message_type = message_type;
        this.message_body = message_body;
        this.file_type = file_type;
        this.file_path = file_path;
        this.created_date = ServerValue.TIMESTAMP;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(KEY.MESSAGE_ID, message_id);
        result.put(KEY.CHAT_ROOM_ID, chat_room_id);
        result.put(KEY.CREATER_ID, creater_id);
        result.put(KEY.MESSAGE_TYPE, message_type);
        result.put(KEY.MESSAGE_BODY, message_body);
        result.put(KEY.FILE_TYPE, file_type);
        result.put(KEY.FILE_PATH, file_path);
        result.put(KEY.CREATED_DATE, created_date);
        return result;
    }
}
