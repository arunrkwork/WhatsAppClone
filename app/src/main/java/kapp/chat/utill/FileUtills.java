package kapp.chat.utill;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Arunraj on 12/9/2017.
 */

public class FileUtills {

    public static final String MEDIA_ROOT = "KChat";
    public static final String MEDIA_IMAGE_FOLDER = "images";

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "/" + MEDIA_ROOT + "/" + albumName + "/");
        if (!file.exists()) {
            file.mkdir();
            if (!file.mkdirs()) {
                Log.e("Folder Creation", "Directory not created");
            }
        }
        return file;
    }

}
