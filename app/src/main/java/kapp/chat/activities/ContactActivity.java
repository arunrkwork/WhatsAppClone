package kapp.chat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import kapp.chat.R;
import kapp.chat.db.KEY;
import kapp.chat.fragments.ContactListFragment;

public class ContactActivity extends AppCompatActivity implements ContactListFragment.OnInteractContactListFragment{

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.contacts));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().add(R.id.container, ContactListFragment.newInstance(), "").commit();

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
    public void setOnInteractContactListFragment(String contactName, String contactNumber, String contactImage, String contactLastSeen) {
        startActivity(new Intent(ContactActivity.this, MessagingActivity.class).putExtra(KEY.USER_NAME, contactName).putExtra(KEY.MOBILE_NO, contactNumber).putExtra(KEY.PROFILE_IMAGE_PATH, contactImage).putExtra(KEY.LAST_SEEN, contactLastSeen));
        finish();
    }
}
