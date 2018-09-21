package kapp.chat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import kapp.chat.R;
import kapp.chat.fragments.ProfileFragment;
import kapp.chat.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.OnSettingsInteractionListener{

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().add(R.id.container, SettingsFragment.newInstance(), "").commit();
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
    public void onSettingsInteraction(int item) {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}
