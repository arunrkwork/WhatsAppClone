package kapp.chat.db.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Arunraj on 11/23/2017.
 */

@IgnoreExtraProperties
public class Status {

    public String status_id, creater_id, status_path;
    public long created_date;

}
