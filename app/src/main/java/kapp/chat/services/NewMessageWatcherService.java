package kapp.chat.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kapp.chat.R;
import kapp.chat.activities.MainActivity;
import kapp.chat.activities.MessagingActivity;
import kapp.chat.db.KEY;
import kapp.chat.db.pojo.ChatRoomMap;
import kapp.chat.db.pojo.User;
import kapp.chat.utill.L;
import kapp.chat.utill.Prefs;

/**
 * Created by Arunraj on 12/9/2017.
 */

public class NewMessageWatcherService extends Service {

    private DatabaseReference mDatabase;
    private ChildEventListener mChildEventListener;
    private Prefs prefs;
    private String creater = "";
    private int id = 1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.t(this, "New Message started ....");
        prefs = new Prefs(this);
        creater = prefs.getString(KEY.MOBILE_NO);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatRoomMap chatRoomMap = dataSnapshot.getValue(ChatRoomMap.class);
                if (chatRoomMap.user_id.equals(creater)) {
                    if (chatRoomMap.unread_message_count > 0) {
                        L.i("New Message : " + chatRoomMap.chat_room_id + " --- " + chatRoomMap.unread_message_count);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ChatRoomMap chatRoomMap = dataSnapshot.getValue(ChatRoomMap.class);
                if (chatRoomMap.user_id.equals(creater)) {
                    if (!chatRoomMap.is_typing) {
                        if (chatRoomMap.unread_message_count > 0) {
                            L.i("New Message changed : " + chatRoomMap.chat_room_id + " --- " + chatRoomMap.unread_message_count);
                            getUserName(chatRoomMap);
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP).addChildEventListener(mChildEventListener);

        return START_STICKY;
    }

    private void getUserName(final ChatRoomMap chatRoomMap) {
        mDatabase.child(KEY.TABLE_USER).child(chatRoomMap.recipient_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    generateNotification(chatRoomMap, user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void generateNotification(ChatRoomMap chatRoomMap, User user) {
        // The id of the channel.
        String CHANNEL_ID = chatRoomMap.chat_room_id;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(user.user_name.toUpperCase())
                        .setContentText("sent new message " + chatRoomMap.unread_message_count);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        resultIntent.putExtra(KEY.USER_NAME, user.user_name);
        resultIntent.putExtra(KEY.MOBILE_NO, user.mobile_no);
        resultIntent.putExtra(KEY.PROFILE_IMAGE_PATH, user.profile_image_path);
        resultIntent.putExtra(KEY.LAST_SEEN, String.valueOf(user.last_seen));
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().

        mNotificationManager.notify(id, mBuilder.build());
    }
}
