package kapp.chat.pojo;

/**
 * Created by Arunraj on 11/27/2017.
 */

public class ChatMessage {

    public static final int RECEIVER = 2;
    public static final int SENDER = 1;
    public static final String TEXT = "TEXT";
    public static final String IMG = "IMG";

    public int TYPE, isRead;
    public String message, message_id, message_type;

    public ChatMessage(){

    }

    public ChatMessage(String message_id, int TYPE, String message, String message_type, int isRead) {
        this.message_id = message_id;
        this.TYPE = TYPE;
        this.message = message;
        this.message_type = message_type;
        this.isRead = isRead;
    }
}
