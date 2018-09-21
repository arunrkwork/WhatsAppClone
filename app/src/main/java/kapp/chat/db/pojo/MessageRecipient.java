package kapp.chat.db.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

import kapp.chat.db.KEY;

/**
 * Created by Arunraj on 11/23/2017.
 */
 
@IgnoreExtraProperties
public class MessageRecipient {

    public String message_recipient_id, recipient_id, recipient_group_id, message_id;
    public boolean is_read;
    public Object message_read_time;

    public MessageRecipient() {
    }

    public MessageRecipient(String message_recipient_id, String recipient_id, String message_id) {
        this.message_recipient_id = message_recipient_id;
        this.recipient_id = recipient_id;
        this.message_id = message_id;
        this.is_read = false;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(KEY.MESSAGE_RECIPIENT_ID, message_recipient_id);
        result.put(KEY.RECIPIENT_ID, recipient_id);
        result.put(KEY.MESSAGE_ID, message_id);
        result.put(KEY.IS_READ, is_read);
        return result;
    }
}
