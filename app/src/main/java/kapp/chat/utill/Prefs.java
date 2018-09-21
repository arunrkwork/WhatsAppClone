package kapp.chat.utill;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Arunraj on 11/6/2017.
 */

public class Prefs {


    public static String PREF_KEY_IMAGE_URI = "pref_key_image_uri";
    private SharedPreferences perf;
    SharedPreferences.Editor editor;
    Context context;

    public static final String PREF_NAME = "pref";
    public static final String PREF_KEY_DEFAULT = "N/A";
    public static final String PREF_KEY_OTP_VERIFIED = "pref_key_otp_verified";

    public Prefs(Context context) {
        this.context = context;
        this.perf = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = perf.edit();
    }

    public String getString(String key){
       return perf.getString(key, PREF_KEY_DEFAULT);
    }

    public void putString(String key, String value){
        editor.putString(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        return perf.getInt(key, 0);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

}
