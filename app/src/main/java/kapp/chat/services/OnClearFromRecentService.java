package kapp.chat.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import kapp.chat.db.KEY;
import kapp.chat.db.pojo.User;
import kapp.chat.utill.Prefs;


/**
 * Created by Arunraj on 11/20/2017.
 */

public class OnClearFromRecentService extends Service {

    DatabaseReference mDatabase;
    Prefs prefs;
    String creater = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new Prefs(this);
        creater = prefs.getString(KEY.MOBILE_NO);
        mDatabase.child(KEY.TABLE_USER)
                .child(prefs.getString(KEY.MOBILE_NO))
                .child(KEY.LAST_SEEN).setValue(ServerValue.TIMESTAMP);
        mDatabase.child(KEY.TABLE_USER)
                .child(prefs.getString(KEY.MOBILE_NO))
                .child(KEY.IS_ONLINE).setValue(true);
        return START_NOT_STICKY;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("ClearFromRecentService", "Service onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("ClearFromRecentService", "Service onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        //Code here
        //stopSelf();
        lastSeen(false);
        //restartService();
    }

    private void restartService() {
        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), NewMessageWatcherService.class),
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }

    private void lastSeen(final boolean status) {

        mDatabase.child(KEY.TABLE_USER).child(creater).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.is_online) {
                    mDatabase.child(KEY.TABLE_USER)
                            .child(prefs.getString(KEY.MOBILE_NO))
                            .child(KEY.LAST_SEEN).setValue(ServerValue.TIMESTAMP);
                    mDatabase.child(KEY.TABLE_USER)
                            .child(prefs.getString(KEY.MOBILE_NO))
                            .child(KEY.IS_ONLINE).setValue(status);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
