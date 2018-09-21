package kapp.chat.utill;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Arunraj on 11/7/2017.
 */

public class L {

    public static void e(String message){
        Log.e(U.TAG, message != null ? message : "");
    }
    public static void i(String message){
        Log.i(U.TAG, message != null ? message : "");
    }

    public static void d(String message){
        Log.d(U.TAG, message != null ? message : "");
    }

    public static void w(String message, Throwable e){
        Log.w(U.TAG, message != null ? message : "", e);
    }

    public static void t(Context context, String message){
        if (context != null) Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
    }

}
