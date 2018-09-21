package kapp.chat.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import kapp.chat.db.KEY;
import kapp.chat.db.pojo.User;
import kapp.chat.utill.L;
import kapp.chat.utill.Prefs;

/**
 * Created by Arunraj on 11/24/2017.
 */

public class BaseActivity extends AppCompatActivity {

    Prefs prefs;
    private static final int INSERT = 1;
    private static final int UPDATE = 2;
    DatabaseReference mDatabase;
    ValueEventListener mValueEventListener;
    private StorageReference mStorageRef;

    public void writeNewUser(Context context) {
        prefs = new Prefs(context);
        final String mobile = prefs.getString(KEY.MOBILE_NO);
        //Save user mobile number into the firebase database table user
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference(); 

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.mobile_no != null && user.mobile_no.equals(mobile)) {
                        // update exist user with updated time
                        L.i("update exist user with updated time");
                        process(mDatabase, user, mobile, UPDATE);
                    } else {
                        // for new user
                        L.i("for new user");
                        process(mDatabase, user, mobile, INSERT);
                    }
                } else {
                    // for new user
                    L.i("// for new user null");
                    process(mDatabase, user, mobile, INSERT);
                }

                prefs.putInt(Prefs.PREF_KEY_OTP_VERIFIED, 1);
                startActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                L.i("writeNewUser : error : " + databaseError.getMessage());
            }
        };
        mDatabase.child(KEY.TABLE_USER).child(mobile).addListenerForSingleValueEvent(mValueEventListener);

    }

    private void process(DatabaseReference mDatabase, User user, String mobile, int action) {

        if (action == UPDATE) {
            prefs.putString(KEY.PROFILE_IMAGE_PATH, user.profile_image_path != null ? (user.profile_image_path.length() != 0 ? user.profile_image_path : Prefs.PREF_KEY_DEFAULT) : Prefs.PREF_KEY_DEFAULT);
            prefs.putString(KEY.USER_NAME, user.user_name!=null ? (user.user_name.length() != 0 ? user.user_name : Prefs.PREF_KEY_DEFAULT) : Prefs.PREF_KEY_DEFAULT);
            mDatabase.child(KEY.TABLE_USER).child(mobile).child(KEY.RECREATED_DATE).setValue(ServerValue.TIMESTAMP);


        }
        else if (action == INSERT) mDatabase.child(KEY.TABLE_USER).child(mobile).setValue(getUser(mobile));
    }

    private Object getUser(String mobile) {
        return new User(getUid(), mobile);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void setMainView(String userID) {
        writeNewUser(this);
        prefs.putString(KEY.USER_ID, userID);
    }

    public void startActivity() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void uploadProfileImage(Context context, final Uri uri, String fname) {

        prefs = new Prefs(context);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //   Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        StorageReference riversRef = mStorageRef.child("images/" + fname + Calendar.getInstance().getTimeInMillis() + ".jpg");

        riversRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        // deleteFile(uri);
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        prefs.putString(KEY.PROFILE_IMAGE_PATH, downloadUrl.toString());
                        saveImageUrlToUsersDatabase(downloadUrl.toString());
                        //Glide.with(getApplicationContext()).load(downloadUrl).apply(RequestOptions.circleCropTransform()).into(ProfileFragment.imgEdit);
                        L.t(getApplicationContext(), "upload success...");
                        L.i("ProfileActivity image upload success uri is : " + downloadUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        L.t(getApplicationContext(), "Upload failed...");
                        L.i("ProfileActivity upload image failed : " + exception.getMessage());
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }


    private void saveImageUrlToUsersDatabase(final String url) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user value
                User user = dataSnapshot.getValue(User.class);

                // [START_EXCLUDE]
                if (user == null) {
                    Toast.makeText(BaseActivity.this,
                            "Error: could not fetch user.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Write url
                    mDatabase.child(KEY.TABLE_USER).child(prefs.getString(KEY.MOBILE_NO)).child(KEY.PROFILE_IMAGE_PATH).setValue(url);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                L.w("getUser:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child(KEY.TABLE_USER).child(prefs.getString(KEY.MOBILE_NO)).addListenerForSingleValueEvent(mValueEventListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabase != null) {
            if (mValueEventListener != null) mDatabase.removeEventListener(mValueEventListener);
        }
    }


}
