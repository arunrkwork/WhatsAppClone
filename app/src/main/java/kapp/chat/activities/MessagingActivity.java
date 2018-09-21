package kapp.chat.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.Calendar;

import kapp.chat.R;
import kapp.chat.db.KEY;
import kapp.chat.db.pojo.ChatRoomMap;
import kapp.chat.db.pojo.User;
import kapp.chat.fragments.MessagingFragment;
import kapp.chat.utill.L;
import kapp.chat.utill.Prefs;
import kapp.chat.utill.U;

public class MessagingActivity extends BaseActivity implements View.OnClickListener, MessagingFragment.OnMessageInteractListener {

    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "MessagingActivity";
    Toolbar toolbar;
    String status = "", creater = "";
    Calendar currentCalendar, lastSeenCalendar;
    boolean isOnline = false;
    DatabaseReference mDatabase;
    ValueEventListener mValueEventListener;
    String contactName = "", contactNumber = "", contactImage = "", contactLastSeen = "";

    // Tool bar Controls
    LinearLayout lContactItem;
    TextView txtUserName, txtStatus;
    ImageView imgProfile, imgBack;
    Prefs prefs;
    private MessagingFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        prefs = new Prefs(this);
        creater = prefs.getString(KEY.MOBILE_NO);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (getIntent() != null) {
            contactName = getIntent().getStringExtra(KEY.USER_NAME);
            contactNumber = getIntent().getStringExtra(KEY.MOBILE_NO);
            contactImage = getIntent().getStringExtra(KEY.PROFILE_IMAGE_PATH);
            contactLastSeen = getIntent().getStringExtra(KEY.LAST_SEEN);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lContactItem = findViewById(R.id.lContactItem);
        txtUserName = findViewById(R.id.txtUserName);
        txtStatus = findViewById(R.id.txtStatus);
        imgProfile = findViewById(R.id.imgProfile);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        setToolBarValue();

        currentCalendar = Calendar.getInstance();
        lastSeenCalendar = Calendar.getInstance();

        mFragment = MessagingFragment.newInstance(contactNumber);
        getSupportFragmentManager().beginTransaction().add(R.id.container, mFragment, "").commit();

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessagingActivity.this, UserDetailActivity.class).putExtra(KEY.USER_NAME, contactName).putExtra(KEY.MOBILE_NO, contactNumber).putExtra(KEY.PROFILE_IMAGE_PATH, contactImage).putExtra(KEY.LAST_SEEN, contactLastSeen));
            }
        });

    }

    private void setToolBarValue() {
        txtUserName.setText(contactName != null ? contactName : contactNumber);
        txtStatus.setText(contactLastSeen);
        Glide.with(this).load(contactImage == null ? R.drawable.ic_user_24 : contactImage).apply(RequestOptions.circleCropTransform()).into(imgProfile);
    }

    private void SET_ONLINE_STATUS_LISTENER() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.is_online) status = "Online";
                    else status = U.convertTimeStampToDateTime(user.last_seen.toString());

                    contactName = user.user_name;
                    contactLastSeen = status;

                    txtUserName.setText(contactName != null ? contactName : contactNumber);
                    txtStatus.setText(contactLastSeen);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child(KEY.TABLE_USER).child(contactNumber).addValueEventListener(mValueEventListener);

        // for update user is typing
        Query query = mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postDataSnapshot : dataSnapshot.getChildren()) {
                    ChatRoomMap chatRoomMap = postDataSnapshot.getValue(ChatRoomMap.class);
                    if (chatRoomMap.user_id != null) {
                        if (chatRoomMap.user_id.equals(prefs.getString(KEY.MOBILE_NO)) && chatRoomMap.recipient_id.equals(contactNumber)) {
                            txtStatus.setText(chatRoomMap.is_typing ? "typing..." : contactLastSeen);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SET_ONLINE_STATUS_LISTENER();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDatabase != null)
            if (mValueEventListener != null) mDatabase.removeEventListener(mValueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabase != null)
            if (mValueEventListener != null) mDatabase.removeEventListener(mValueEventListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                super.onBackPressed();
                break;
        }
    }

    @Override
    public void setOnMessageInteractListener(String status) {
        if (status.equals("OK")) txtStatus.setText(contactLastSeen);
        else txtStatus.setText(status);
    }

    @Override
    public void setAttachFileListener() {
        showFileChooser();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String path = getFilePath(uri);
                    String fileName = "", fileType = "";
                    fileName = new File(path).getName();
                    fileType = getFileExtension(path);
                    mFragment.uploadFile(this, uri, fileName, fileType, creater, contactNumber);
                    Log.d(TAG, "File Uri: " + uri.toString());
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getFilePath(Uri uri){
        String filePath = "";
        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            filePath = uri.getLastPathSegment();
        }
        else if (scheme.equals("content")) {
            //String[] proj = { MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DATA };
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                //int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }

            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return filePath;
    }

    public String getFileExtension(String fullName) {
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}
