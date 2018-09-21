package kapp.chat.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import kapp.chat.R;
import kapp.chat.adapter.ViewPagerAdapter;
import kapp.chat.db.KEY;
import kapp.chat.fragments.ChatRoomFragment;
import kapp.chat.fragments.StatusFragment;
import kapp.chat.services.NewMessageWatcherService;
import kapp.chat.services.OnClearFromRecentService;
import kapp.chat.utill.Prefs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener, ChatRoomFragment.OnInteractChatRoomListFragment {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DatabaseReference mDatabase;
    private Prefs prefs;
    String userID = "", mobile = "";
    FloatingActionButton fabClick;
    String contactName = "", contactNumber = "", contactImage = "", contactLastSeen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: called");
        setContentView(R.layout.activity_main);
        prefs = new Prefs(this);
        userID = prefs.getString(KEY.USER_ID);
        mobile = prefs.getString(KEY.MOBILE_NO);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Log.i(TAG, "onCreate: "+ prefs.getString(KEY.USER_NAME) + " --- " + prefs.getString(KEY.PROFILE_IMAGE_PATH));
        //Firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fabClick = findViewById(R.id.fabClick);
        fabClick.setOnClickListener(this);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
        setupTabIcons();

        //start service for clear from recent service to update last seen and online user_status
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

        //start service for listern new messages and generate notifications
        startService(new Intent(getBaseContext(), NewMessageWatcherService.class));

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            contactName = getIntent().getStringExtra(KEY.USER_NAME);
            contactNumber = getIntent().getStringExtra(KEY.MOBILE_NO);
            contactImage = getIntent().getStringExtra(KEY.PROFILE_IMAGE_PATH);
            contactLastSeen = getIntent().getStringExtra(KEY.LAST_SEEN);
            startActivity(new Intent(MainActivity.this, MessagingActivity.class).putExtra(KEY.USER_NAME, contactName).putExtra(KEY.MOBILE_NO, contactNumber).putExtra(KEY.PROFILE_IMAGE_PATH, contactImage).putExtra(KEY.LAST_SEEN, contactLastSeen));
        }

    }

    private void setupTabIcons() {

        //  ImageView tabOne = (ImageView) LayoutInflater.from(this).inflate(R.layout.image_view, null);
        //  tabLayout.getTabAt(0).setCustomView(tabOne);

        for (int index = 0; index < 2; index++) {
            TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            if (index == 0) {
                textView.setTextAppearance(this, R.style.TextAppearance_Tabs_Selected);
                textView.setText("Chats");
            } else if (index == 1) textView.setText("Status");
            //else if (index == 3) textView.setText("Calls");
            tabLayout.getTabAt(index).setCustomView(textView);
        }
        viewPager.setCurrentItem(0);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //adapter.addFragment(CameraFragment.newInstance(), "");
        adapter.addFragment(ChatRoomFragment.newInstance(), "Chats");
        adapter.addFragment(StatusFragment.newInstance(), "Status");
        //   adapter.addFragment(CallsFragment.newInstance(), "Call");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        changeTabStyle(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        if (view instanceof TextView) {
            ((TextView) view).setTextAppearance(getApplicationContext(), R.style.TextAppearance_Tabs);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        changeTabStyle(tab);
    }

    private void changeTabStyle(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        if (view instanceof TextView) {
            ((TextView) view).setTextAppearance(getApplicationContext(), R.style.TextAppearance_Tabs_Selected);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: called");
        //update user is offline and last seen to the firebase database users table
        mDatabase.child(KEY.TABLE_USER).child(mobile).child(KEY.IS_ONLINE).setValue(false);
        mDatabase.child(KEY.TABLE_USER).child(mobile).child(KEY.LAST_SEEN).setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabClick:
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
        }
    }

    @Override
    public void setOnInteractChatRoomListFragment(String contactName, String contactNumber, String contactImage, String contactLastSeen) {
        startActivity(new Intent(MainActivity.this, MessagingActivity.class).putExtra(KEY.USER_NAME, contactName).putExtra(KEY.MOBILE_NO, contactNumber).putExtra(KEY.PROFILE_IMAGE_PATH, contactImage).putExtra(KEY.LAST_SEEN, contactLastSeen));
    }
}