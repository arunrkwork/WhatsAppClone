package kapp.chat.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kapp.chat.R;
import kapp.chat.db.KEY;
import kapp.chat.db.pojo.User;
import kapp.chat.utill.L;
import kapp.chat.utill.Prefs;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SettingsFragment";
    ImageView imgProfile;
    TextView txtUserName, txtStatus;
    LinearLayout profile;
    Prefs prefs;
    DatabaseReference mDatabase;
    ValueEventListener mUserUpdateListener;
    String uname = "", status = "", mobile = "", userID = "", profile_image = "", bitmapUri = "";

    private OnSettingsInteractionListener mListener;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public SettingsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated: ");
        profile = view.findViewById(R.id.profile);
        profile.setOnClickListener(this);
        imgProfile = view.findViewById(R.id.imgProfile);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtStatus = view.findViewById(R.id.txtStatus);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: ");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new Prefs(getActivity());
        readValues();
        txtUserName.setText(uname.equals(Prefs.PREF_KEY_DEFAULT) ? "Name" : uname);
        txtStatus.setText(status.equals(Prefs.PREF_KEY_DEFAULT) ? "Hey! there I am using KChat" : status);
        if (!profile_image.equals(Prefs.PREF_KEY_DEFAULT))
            Glide.with(getActivity()).load(profile_image).apply(RequestOptions.circleCropTransform()).into(imgProfile);
        else
            Glide.with(getActivity()).load(R.drawable.ic_user_24).apply(RequestOptions.circleCropTransform()).into(imgProfile);
    }

    private void refreshUI() {
        readValues();
        txtUserName.setText(uname.equals(Prefs.PREF_KEY_DEFAULT) ? "Name" : uname);
        if (!bitmapUri.equals(Prefs.PREF_KEY_DEFAULT)) {
            Uri uri = null;
            Bitmap bitmap = null;
            try {
                uri = Uri.parse(bitmapUri);
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            } catch (Exception e) {
                bitmap = null;
                L.d("SettingsFragment error bitmap null " + e.getMessage());
            }

            if (bitmap != null) {
                Bitmap b = bitmap;
                Glide.with(getActivity()).load(b).apply(RequestOptions.circleCropTransform()).into(imgProfile);
            }
        } else if (!profile_image.equals(Prefs.PREF_KEY_DEFAULT)) {
            Glide.with(getActivity()).load(profile_image).apply(RequestOptions.circleCropTransform()).into(imgProfile);
        } else
            Glide.with(getActivity()).load(R.drawable.ic_user_24).apply(RequestOptions.circleCropTransform()).into(imgProfile);
    }

    private void readValues() {
        userID = prefs.getString(KEY.USER_ID);
        uname = prefs.getString(KEY.USER_NAME);
        status = prefs.getString(KEY.USER_STATUS);
        mobile = prefs.getString(KEY.MOBILE_NO);
        profile_image = prefs.getString(KEY.PROFILE_IMAGE_PATH);
        bitmapUri = prefs.getString(Prefs.PREF_KEY_IMAGE_URI);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        mUserUpdateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    uname = user.user_name;
                    status = user.user_status;

                    prefs.putString(KEY.USER_NAME, uname);
                    prefs.putString(KEY.USER_STATUS, status);

                    txtStatus.setText(status);
                    txtUserName.setText(uname);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child(KEY.TABLE_USER).child(mobile).addValueEventListener(mUserUpdateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
        if (mDatabase != null) {
            if (mUserUpdateListener != null) mDatabase.removeEventListener(mUserUpdateListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");

    }

    @Override
    public void onClick(View view) {
        if (view == profile) onButtonPressed(1);
    }


    public void onButtonPressed(int item) {
        if (mListener != null) {
            mListener.onSettingsInteraction(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
        if (context instanceof OnSettingsInteractionListener)
            mListener = (OnSettingsInteractionListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnSettingsInteractionListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
        mListener = null;
    }

    public interface OnSettingsInteractionListener {
        void onSettingsInteraction(int item);
    }

}
