package kapp.chat.db.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

import kapp.chat.db.KEY;

/**
 * Created by Arunraj on 11/28/2017.
 */

@IgnoreExtraProperties
public class ChatRoomMap {

    public String chat_room_map_id, chat_room_id, user_id, recipient_id;
    public int unread_message_count;
    public boolean is_typing;

    public ChatRoomMap() {
    }

    public ChatRoomMap(String chat_room_map_id, String chat_room_id, String user_id, String recipient_id) {
        this.chat_room_map_id = chat_room_map_id;
        this.chat_room_id = chat_room_id;
        this.user_id = user_id;
        this.recipient_id = recipient_id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(KEY.CHAT_ROOM_MAP_ID, chat_room_map_id);
        result.put(KEY.CHAT_ROOM_ID, chat_room_id);
        result.put(KEY.USER_ID, user_id);
        result.put(KEY.RECIPIENT_ID, recipient_id);
        result.put(KEY.IS_TYPING, false);
        return result;
    }

}
