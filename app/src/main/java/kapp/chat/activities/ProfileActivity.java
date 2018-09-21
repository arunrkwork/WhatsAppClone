package kapp.chat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;

import kapp.chat.R;
import kapp.chat.db.KEY;
import kapp.chat.utill.L;
import kapp.chat.crop.CropImage;
import kapp.chat.fragments.ProfileFragment;
import kapp.chat.utill.Prefs;

public class ProfileActivity extends BaseActivity implements ProfileFragment.OnProfileInteractionListener {

    private static final int REQUEST_IMAGE_GALLERY = 102;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    Toolbar toolbar;
    Uri mImageCaptureUri;
    Prefs prefs;
    String mobile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = new Prefs(this);
        mobile = prefs.getString(KEY.MOBILE_NO);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.profile));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, ProfileFragment.newInstance(), "").commit();
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
    public void onProfileInteraction(int type) {
        if (type == 1) cameraIntent();
        else if (type == 2) galleryIntent();

    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY);
    }

    private void cameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                    "tmp_contact_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Pick image from camera
            if (mImageCaptureUri != null) {
                CropImage.activity(mImageCaptureUri).start(this);
            } else
                L.i("ProfileActivity onActivityResult : request image capture null pointer exception");
        } else if (requestCode == REQUEST_IMAGE_GALLERY) {
            //Pick image from gallery
            if (data != null) {
                if (data.getData() != null) {
                    mImageCaptureUri = data.getData();
                    CropImage.activity(mImageCaptureUri).start(this);
                } else
                    L.i("ProfileActivity onActivityResult : request image gallery null pointer exception");
            }

        }  else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    L.i("ProfileActivity image result uri : " + resultUri);


                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                        if (bitmap != null) {
                            prefs.putString(Prefs.PREF_KEY_IMAGE_URI, resultUri.toString());
                            Glide.with(this).load(bitmap).apply(RequestOptions.circleCropTransform()).into(ProfileFragment.imgEdit);
                        } else
                            L.i("ProfileActivity onActivityResult: place profile image - bitmap null...");
                        uploadProfileImage(this, resultUri, "before-scaled");
                    } catch (IOException e) {
                        L.i("ProfileActivity onActivityResult : " + e.getMessage());
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    //Exception error = result.getError();
                }
            }
        }
    }

}
