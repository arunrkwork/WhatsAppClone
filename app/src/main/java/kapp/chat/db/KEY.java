package kapp.chat.db;

/**
 * Created by Arunraj on 11/23/2017.
 */

public class KEY {

    public static final String COMMON_STORAGE_PATH = "gs://fir-project-e0ec2.appspot.com/";
    public static final String FILE_STORAGE = "files";
    public static final String TEXT = "TEXT";
    public static final String FILE = "FILE";
    public static final String IMG = "IMG";

    /*------------------ start available tables ------------------*/

    public static final String TABLE_USER = "user";
    public static final String TABLE_USER_GROUP_MAP = "user_group_map";
    public static final String TABLE_GROUP = "group";
    public static final String TABLE_MESSAGE = "message";
    public static final String TABLE_MESSAGE_RECIPIENT = "message_recipient";
    public static final String TABLE_CHAT_ROOM = "chat_room";
    public static final String TABLE_CHAT_ROOM_MAP = "chat_room_map";
    public static final String TABLE_BLOCKED_USER = "blocked_user";
    public static final String TABLE_STATUS = "user_status";

    /*------------------ end available tables ------------------*/

    /*------------------ start user table ------------------*/
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String MOBILE_NO = "mobile_no";
    public static final String IS_ONLINE = "is_online";
    public static final String PROFILE_IMAGE_PATH = "profile_image_path";
    public static final String USER_TYPE = "user_type";
    public static final String CREATED_DATE = "created_date";
    public static final String LAST_SEEN = "last_seen";
    public static final String UPDATED_DATE = "updated_date";
    public static final String RECREATED_DATE = "recreated_date";
    public static final String IS_ACTIVE = "is_active";
    public static final String USER_STATUS = "user_status";
    /*------------------ end user table ------------------*/

    /*------------------ start user_group table ------------------*/

    public static final String GROUP_ID = "group_id";
    public static final String GROUP_NAME = "group_name";
    //public static final String CREATED_DATE = "created_date";
    //public static final String IS_ACTIVE = "is_active";

    /*------------------ end user_group table ------------------*/

    /*------------------ start user_group_map table ------------------*/

    public static final String USER_GROUP_MAP_ID = "";
    //public static final String USER_ID = "user_id";
    //public static final String GROUP_ID = "group_id";
    //public static final String USER_TYPE= "user_type";
    //public static final String CREATED_DATE = "created_date";
    //public static final String IS_ACTIVE = "is_active";
    public static final String UNREAD_MESSAGE_COUNT = "unread_message_count";

    /*------------------ end user_group_map table ------------------*/

    /*-------------------- start message table ----------------------*/

    public static final String MESSAGE_ID = "message_id";
    //public static final String CHAT_ROOM_ID = "chat_room_id";
    //public static final String CREATER_ID = "creater_id";
    public static final String MESSAGE_TYPE = "message_type";
    public static final String MESSAGE_BODY = "message_body";
    public static final String FILE_TYPE = "file_type";
    public static final String FILE_PATH = "file_path";
    //public static final String CREATED_DATE = "created_date";

    /*-------------------- end message table ----------------------*/

    /*--------------------- start message recipient -----------------*/
    public static final String MESSAGE_RECIPIENT_ID = "message_recipient_id";
    public static final String RECIPIENT_ID = "recipient_id";
    public static final String RECIPIENT_GROUP_ID = "recipient_group_id";
    //public static final String MESSAGE_ID = "message_id";
    public static final String IS_READ = "is_read";
    public static final String MESSAGE_READ_TIME = "message_read_time";
    /*--------------------- end message recipient -----------------*/

    /*-------------- start chat room ------------- */
    public static final String CHAT_ROOM_ID = "chat_room_id";
    public static final String CREATER_ID = "creater_id";
    //public static final String CREATED_DATE = "created_date";
    /*-------------- end chat room ------------- */

    /*---------------- start blocked user --------------*/
    public static final String BLOCKED_USER_ID = "blocked_user_id";
    //public static final String CREATER_ID = "creater_id";
    //public static final String USER_ID = "user_id";
    //public static final String CREATED_DATE = "created_date";
    /*---------------- end blocked user --------------*/

    /*------------------ start user_status ------------------*/
    public static final String STATUS_ID = "status_id";
    //public static final String CREATER_ID = "creater_id";
    public static final String STATUS_PATH = "status_path";

    //public static final String CREATED_DATE = "created_date";
    //public static final String USER_ID = "user_id";
    /*------------------ end user_status ------------------*/

    /*-------------------- start chat room map ---------------------*/
    public static final String CHAT_ROOM_MAP_ID = "chat_room_map_id";
    public static final String IS_TYPING = "is_typing";
    //public static final String CHAT_ROOM_ID = "chat_room_id";
    //public static final String USER_ID = "user_id";
    //public static final String RECIPIENT_ID = "recipient_id";

    /*-------------------- end chat room map ---------------------*/

}
