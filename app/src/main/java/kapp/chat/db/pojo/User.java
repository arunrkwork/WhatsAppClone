package kapp.chat.db.pojo;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import kapp.chat.pojo.ServerTime;

/**
 * Created by Arunraj on 11/23/2017.
 */

@IgnoreExtraProperties
public class User {

    public String user_id, user_name, mobile_no, profile_image_path, user_status;
    public int unread_message_count;
    public boolean is_online, is_active;
    public Object user_type, last_seen, created_date, updated_date, recreated_date;

    public User() {
    }

    public User(String user_id, String mobile_no) {
        this.user_id = user_id;
        this.mobile_no = mobile_no;
        this.is_online = true;
        this.is_active = true;
        this.user_type = 1;
        this.created_date = ServerValue.TIMESTAMP;
    }
}
