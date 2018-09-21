package kapp.chat.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import kapp.chat.R;
import kapp.chat.db.KEY;
import kapp.chat.db.pojo.User;
import kapp.chat.utill.L;
import kapp.chat.utill.Prefs;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private OnProfileInteractionListener mListener;
    public static ImageView imgEdit;
    TextView txtNumber, txtStatus;
    ImageView imgUserNameEdit, imgCamera, imgUserStatusEdit;
    EmojiconTextView txtUserName;
    Prefs prefs;
    String uname = "", status = "", mobile = "", userID = "", profile_image = "", bitmapUri = "";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]
    private ValueEventListener mValueEventListener, mUserUpdateListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtUserName = view.findViewById(R.id.txtUserName);
        txtStatus = view.findViewById(R.id.txtStatus);
        imgUserNameEdit = view.findViewById(R.id.imgUserNameEdit);
        imgUserStatusEdit = view.findViewById(R.id.imgUserStatusEdit);
        imgEdit = view.findViewById(R.id.imgEdit);
        imgCamera = view.findViewById(R.id.imgCamera);
        txtNumber = view.findViewById(R.id.txtNumber);

        imgUserNameEdit.setOnClickListener(this);
        imgUserStatusEdit.setOnClickListener(this);
        imgEdit.setOnClickListener(this);
        imgCamera.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prefs = new Prefs(getActivity());
        userID = prefs.getString(KEY.USER_ID);
        uname = prefs.getString(KEY.USER_NAME);
        status = prefs.getString(KEY.USER_STATUS);
        mobile = prefs.getString(KEY.MOBILE_NO);
        profile_image = prefs.getString(KEY.PROFILE_IMAGE_PATH);
        bitmapUri = prefs.getString(Prefs.PREF_KEY_IMAGE_URI);

        txtNumber.setText(mobile);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        txtUserName.setText(uname.equals(Prefs.PREF_KEY_DEFAULT) ? "Name" : uname);
        txtStatus.setText(status.equals(Prefs.PREF_KEY_DEFAULT) ? "Hey! there I m using KChat" : status);
        if (!profile_image.equals(Prefs.PREF_KEY_DEFAULT)) Glide.with(getActivity()).load(prefs.getString(KEY.PROFILE_IMAGE_PATH)).apply(RequestOptions.circleCropTransform()).into(ProfileFragment.imgEdit);
    }

    public void onButtonPressed(int type) {
        if (mListener != null) {
            mListener.onProfileInteraction(type);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProfileInteractionListener) {
            mListener = (OnProfileInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnProfileInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (view == imgEdit) chooser();
        else if (view == imgCamera) chooser();
        else if (view == imgUserNameEdit) editUserName();
        else if (view == imgUserStatusEdit) editUserStatus();
    }

    private void editUserStatus() {
        final Dialog dialog = new Dialog(getActivity() ,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.setContentView(R.layout.dialog_uname_edit);

        TextView txtCancel, txtOk;
        View root;
        EmojIconActions emojIcon;
        final EmojiconEditText edUname;
        ImageView emojiButton;

        root = dialog.findViewById(R.id.root);
        emojiButton =  dialog.findViewById(R.id.emoji_btn);
        edUname = dialog.findViewById(R.id.edUname);
        edUname.setText(txtStatus.getText().toString());
        edUname.setSelection(txtStatus.getText().toString().length());

        txtOk = dialog.findViewById(R.id.txtOk);
        txtCancel = dialog.findViewById(R.id.txtCancel);

        emojIcon=new EmojIconActions(dialog.getContext(),root,edUname,emojiButton,"#495C66","#DCE1E2","#E6EBEF");
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uname = edUname.getText().toString();
                if (uname != null) {
                    String name = uname +"";
                    prefs.putString(KEY.USER_STATUS, name);
                    updateUserNameStatusToUsersTable(KEY.USER_STATUS, name);
                    txtStatus.setText(name);
                }
                dialog.dismiss();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void chooser() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_camera_gallery_chooser);
        ImageView imgPickCamera, imgPickGallery;
        imgPickCamera = dialog.findViewById(R.id.imgPickCamera);
        imgPickGallery = dialog.findViewById(R.id.imgPickGallery);

        imgPickCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(1);
                dialog.dismiss();
            }
        });

        imgPickGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(2);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void editUserName() {
        final Dialog dialog = new Dialog(getActivity() ,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.setContentView(R.layout.dialog_uname_edit);

        TextView txtCancel, txtOk;
        View root;
        EmojIconActions emojIcon;
        final EmojiconEditText edUname;
        ImageView emojiButton;

        root = dialog.findViewById(R.id.root);
        emojiButton =  dialog.findViewById(R.id.emoji_btn);
        edUname = dialog.findViewById(R.id.edUname);
        edUname.setText(txtUserName.getText().toString());
        edUname.setSelection(txtUserName.getText().toString().length());

        txtOk = dialog.findViewById(R.id.txtOk);
        txtCancel = dialog.findViewById(R.id.txtCancel);

        emojIcon=new EmojIconActions(dialog.getContext(),root,edUname,emojiButton,"#495C66","#DCE1E2","#E6EBEF");
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uname = edUname.getText().toString();
                if (uname != null) {
                    String name = uname +"";
                    prefs.putString(KEY.USER_NAME, name);
                    updateUserNameStatusToUsersTable(KEY.USER_NAME, name);
                    txtUserName.setText(name);
                }
                dialog.dismiss();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateUserNameStatusToUsersTable(final String key, final String value) {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user value
                User user = dataSnapshot.getValue(User.class);

                // [START_EXCLUDE]
                if (user == null) {
                    // User is null, error out
                    Toast.makeText(getActivity(),
                            "Error: could not fetch user.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Write user name
                    mDatabase.child(KEY.TABLE_USER).child(mobile).child(key).setValue(value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                L.w("getUser:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child(KEY.TABLE_USER).child(mobile).addListenerForSingleValueEvent(mValueEventListener);
     }

    @Override
    public void onStop() {
        super.onStop();
        removeListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       removeListeners();
    }

    private void removeListeners() {
        if (mDatabase != null) {
            if (mValueEventListener != null) mDatabase.removeEventListener(mValueEventListener);
            if (mUserUpdateListener != null) mDatabase.removeEventListener(mUserUpdateListener);
        }
    }

    public interface OnProfileInteractionListener {
        void onProfileInteraction(int type);
    }
}
