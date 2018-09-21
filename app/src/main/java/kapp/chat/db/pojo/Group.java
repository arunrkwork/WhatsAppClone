package kapp.chat.db.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Arunraj on 11/23/2017.
 */

@IgnoreExtraProperties
public class Group {

    public String group_id, group_name;
    public long created_date;
    public boolean is_active;

}
