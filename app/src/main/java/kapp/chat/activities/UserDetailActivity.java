package kapp.chat.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kapp.chat.R;
import kapp.chat.db.KEY;
import kapp.chat.db.pojo.ChatRoomMap;
import kapp.chat.db.pojo.User;
import kapp.chat.utill.Prefs;
import kapp.chat.utill.U;

public class UserDetailActivity extends AppCompatActivity {

    private ImageView imgUserImage;
    private CollapsingToolbarLayout profileactivity_collapsing_toolbar;
    private Toolbar toolbar;
    private TextView txtStatus, txtNumber, txtUserName, txtLastSeen;
    private DatabaseReference mDatabase;
    private ValueEventListener mValueEventListener, mIsTypingListener;
    private Prefs prefs;
    private String contactName = "", contactNumber = "", contactImage = "", contactLastSeen = "", status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new Prefs(this);

        if (getIntent() != null) {
            contactName = getIntent().getStringExtra(KEY.USER_NAME);
            contactNumber = getIntent().getStringExtra(KEY.MOBILE_NO);
            contactImage = getIntent().getStringExtra(KEY.PROFILE_IMAGE_PATH);
            contactLastSeen = getIntent().getStringExtra(KEY.LAST_SEEN);
        }

        profileactivity_collapsing_toolbar = findViewById(R.id.profileactivity_collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtStatus = findViewById(R.id.txtStatus);
        txtUserName = findViewById(R.id.txtUserName);
        txtLastSeen = findViewById(R.id.txtLastSeen);
        txtNumber = findViewById(R.id.txtNumber);
        imgUserImage = findViewById(R.id.imgUserImage);

        updateValue();
        Glide.with(this).load(contactImage == null ? R.drawable.ic_user_24 : contactImage).into(imgUserImage);


        Glide.with(this)
                .load(contactImage)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        Bitmap anImage = ((BitmapDrawable) resource).getBitmap();
                        setToolbarColor(anImage);
                    }
                });
    }

    private Palette.Swatch checkVibrantSwatch(Palette p) {
        Palette.Swatch vibrant = p.getVibrantSwatch();
        if (vibrant != null) {
            return vibrant;
        }
        return null;
    }

    public Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }


    public void setToolbarColor(Bitmap bitmap) {
        // Generate the palette and get the vibrant swatch
        // See the createPaletteSync() and checkVibrantSwatch() methods
        // from the code snippets above
        Palette p = createPaletteSync(bitmap);
        Palette.Swatch vibrantSwatch = checkVibrantSwatch(p);

        // Set the toolbar background and text colors
        toolbar.setBackgroundColor(vibrantSwatch.getRgb());
        toolbar.setTitleTextColor(vibrantSwatch.getTitleTextColor());
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUserDetails();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDatabase.removeEventListener(mValueEventListener);
        mDatabase.removeEventListener(mIsTypingListener);
    }

    private void updateValue() {
        txtNumber.setText(contactNumber);
        txtStatus.setText(status);
        txtUserName.setText(contactName);
        txtLastSeen.setText(contactLastSeen);
    }

    private void updateUserDetails() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    contactName = user.user_name;
                    contactNumber = user.mobile_no;
                    if (user.is_online) {
                        contactLastSeen = "Online";
                    } else {
                        contactLastSeen = U.convertTimeStampToDateTime(user.last_seen.toString());
                    }
                    status = user.user_status;
                    updateValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child(KEY.TABLE_USER).child(contactNumber).addValueEventListener(mValueEventListener);

        // for update user is typing
        mIsTypingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postDataSnapshot : dataSnapshot.getChildren()) {
                    ChatRoomMap chatRoomMap = postDataSnapshot.getValue(ChatRoomMap.class);
                    if (chatRoomMap.user_id != null) {
                        if (chatRoomMap.user_id.equals(prefs.getString(KEY.MOBILE_NO)) && chatRoomMap.recipient_id.equals(contactNumber)) {
                            txtLastSeen.setText(chatRoomMap.is_typing ? "typing..." : contactLastSeen);
                            return;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP).addValueEventListener(mIsTypingListener);
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

}
