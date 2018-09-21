package kapp.chat.db.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Arunraj on 11/23/2017.
 */

@IgnoreExtraProperties
public class UserGroupMap {

    public String user_group_map_id, user_id, group_id;
    public long user_type, created_date;
    public boolean is_active;


}
