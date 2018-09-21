package kapp.chat.db.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Arunraj on 11/23/2017.
 */

@IgnoreExtraProperties
public class BlockedUser {

    public String blocked_user_id, creater_id, user_id;
    public long created_date;

}
