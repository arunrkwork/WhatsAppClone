package kapp.chat.activities;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import kapp.chat.R;
import kapp.chat.db.KEY;
import kapp.chat.utill.FileUtills;
import kapp.chat.utill.L;

public class ImageViewActivity extends AppCompatActivity implements View.OnClickListener {

    private String FILE_PATH, contactName;
    private ImageView imgAttach;
    private Toolbar toolbar;
    private ImageView imgDownload;
    private TextView txtTitle;

    private FirebaseStorage storage;
    private StorageReference httpsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        if (getIntent() != null) {
            contactName = getIntent().getStringExtra(KEY.USER_NAME);
            FILE_PATH = getIntent().getStringExtra(KEY.FILE_PATH);
        }

        storage = FirebaseStorage.getInstance();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtTitle = findViewById(R.id.txtTitle);
        imgAttach = findViewById(R.id.imgAttach);
        imgDownload = findViewById(R.id.imgDownload);
        imgDownload.setOnClickListener(this);
        Glide.with(this).load(FILE_PATH).into(imgAttach);
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
    public void onClick(View view) {
        if (view == imgDownload) {
            downloadFile();
        }
    }

    private void downloadFile() {

        if (FileUtills.isExternalStorageWritable()) {
            File localFile = null;

            FileUtills.getAlbumStorageDir(FileUtills.MEDIA_IMAGE_FOLDER);
            localFile = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + FileUtills.MEDIA_ROOT + "/" + FileUtills.MEDIA_IMAGE_FOLDER + "/" + Calendar.getInstance().getTimeInMillis() + ".png");
            L.t(this, "Downloading...");
            if (localFile != null) {
                httpsReference = storage.getReferenceFromUrl(FILE_PATH);

                httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        L.t(ImageViewActivity.this, "Download Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        L.t(ImageViewActivity.this, "error");
                        L.e("file download error " + exception.getMessage());
                    }
                });
            } else L.t(this, "Folder can't create...");
        } else L.t(this, "You can't write file...");

    }
}
