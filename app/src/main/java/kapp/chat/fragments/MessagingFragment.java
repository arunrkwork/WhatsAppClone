package kapp.chat.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import kapp.chat.R;
import kapp.chat.activities.ImageViewActivity;
import kapp.chat.adapter.MessagingAdapter;
import kapp.chat.db.KEY;
import kapp.chat.db.pojo.ChatRoom;
import kapp.chat.db.pojo.ChatRoomMap;
import kapp.chat.db.pojo.Message;
import kapp.chat.db.pojo.MessageRecipient;
import kapp.chat.pojo.ChatMessage;
import kapp.chat.utill.L;
import kapp.chat.utill.Prefs;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagingFragment extends Fragment implements View.OnClickListener, MessagingAdapter.OnMessageItemClick {

    private RecyclerView mRecyclerView;
    private MessagingAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<ChatMessage> list;
    private String contactNumber = "";
    private Prefs prefs;
    private String creater, chatRoomID = null;

    EmojiconEditText emojiconEditText;
    ImageView emojiButton, submitButton, imgAttach;
    EmojIconActions emojIcon;
    View rootView;
    private DatabaseReference mDatabase;
    private ValueEventListener mReadMessageValueEventListener, mChatRoomAvailableListener,
            mUpdateReadMessageStatusListener, mCreateChatRoomListener;
    private ChildEventListener mChildEventListener;
    private Query readMessageQuery;
    private boolean isChatRoomAvaialble = false;
    private static final String TAG = "Messaging data : ";
    private LinearLayout attachmentLayout;
    private boolean isHidden = true;
    OnMessageInteractListener mListener;
    private StorageReference mStorageRef;

    @Override
    public void setOnMessageItemClick(String message_id, String file_path) {
        startActivity(new Intent(getActivity(), ImageViewActivity.class).putExtra(KEY.USER_NAME, "").putExtra(KEY.FILE_PATH, file_path));
    }

    public interface OnMessageInteractListener {
        void setOnMessageInteractListener(String status);

        void setAttachFileListener();
    }

    public MessagingFragment() {
        // Required empty public constructor
    }

    public static MessagingFragment newInstance(String contactNumber) {
        MessagingFragment fragment = null;
        if (fragment == null) {
            fragment = new MessagingFragment();
            Bundle bundle = new Bundle();
            bundle.putString(KEY.MOBILE_NO, contactNumber);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contactNumber = getArguments().getString(KEY.MOBILE_NO);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMessageInteractListener) {
            mListener = (OnMessageInteractListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMessageInteractListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        L.i("MessagingFragment onCreateView: called...");
        return inflater.inflate(R.layout.fragment_messaging, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        L.i("MessagingFragment onViewCreated: called...");
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        rootView = view.findViewById(R.id.bottom);
        emojiButton = view.findViewById(R.id.emoji_btn);
        submitButton = view.findViewById(R.id.submit_btn);
        emojiconEditText = view.findViewById(R.id.emojicon_edit_text);
        imgAttach = view.findViewById(R.id.imgAttach);
        imgAttach.setOnClickListener(this);

      /*  attachmentLayout = (LinearLayout) view.findViewById(R.id.menu_attachments);

        ImageButton btnDocument = (ImageButton) view.findViewById(R.id.menu_attachment_document);
        ImageButton btnCamera = (ImageButton) view.findViewById(R.id.menu_attachment_camera);
        ImageButton btnGallery = (ImageButton) view.findViewById(R.id.menu_attachment_gallery);
        ImageButton btnAudio = (ImageButton) view.findViewById(R.id.menu_attachment_audio);
        ImageButton btnLocation = (ImageButton) view.findViewById(R.id.menu_attachment_location);
        ImageButton btnContact = (ImageButton) view.findViewById(R.id.menu_attachment_contact);

        btnDocument.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnAudio.setOnClickListener(this);
        btnLocation.setOnClickListener(this);
        btnContact.setOnClickListener(this);*/

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        L.i("MessagingFragment onActivityCreated: called...");
        prefs = new Prefs(getActivity());
        creater = prefs.getString(KEY.MOBILE_NO);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        list = new ArrayList<>();
        mAdapter = new MessagingAdapter(this, getActivity(), list);
        mRecyclerView.setAdapter(mAdapter);

        checkChatRoomAvailablity();


        emojIcon = new EmojIconActions(getActivity(), rootView, emojiconEditText, emojiButton, "#495C66", "#DCE1E2", "#E6EBEF");
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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = emojiconEditText.getText().toString();
                send(newText, "", KEY.TEXT);
            }
        });

        emojiconEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = emojiconEditText.getText().toString();
                if (text != null) {
                    if (text.length() != 0) {
                        updateUserAction(true);
                    } else updateUserAction(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void send(String newText, String fileType, String message_type) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (isChatRoomAvaialble) {
            if (TextUtils.isEmpty(newText)) L.t(getActivity(), "Please Type Your Message");
            else sendMessage(newText, fileType, message_type, chatRoomID);
        } else createChatRoom(newText, fileType, message_type);

    }

    private void updateUserAction(final boolean action) {
        // for update user is typing
        if (mDatabase != null) {
            Query query = mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postDataSnapshot : dataSnapshot.getChildren()) {
                        ChatRoomMap chatRoomMap = postDataSnapshot.getValue(ChatRoomMap.class);
                        if (chatRoomMap.user_id != null) {
                            if (chatRoomMap.user_id.equals(contactNumber) && chatRoomMap.recipient_id.equals(creater)) {
                                String key = postDataSnapshot.getKey();
                                mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP).child(key).child(KEY.IS_TYPING).setValue(action);
                                return;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void checkChatRoomAvailablity() {

        mChatRoomAvailableListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot listDataSnapshot : dataSnapshot.getChildren()) {
                    ChatRoomMap chatRoomMap = listDataSnapshot.getValue(ChatRoomMap.class);

                    if (chatRoomMap != null) {
                        if (chatRoomMap.user_id.equals(creater) && chatRoomMap.recipient_id.equals(contactNumber)) {
                            setChatRoomID(chatRoomMap.chat_room_id);
                            isChatRoomAvaialble = true;
                            readMessageList();
                            return;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if (mDatabase != null)
            mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP).addListenerForSingleValueEvent(mChatRoomAvailableListener);

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatRoomMap chatRoomMap = dataSnapshot.getValue(ChatRoomMap.class);

                if (chatRoomMap != null) {
                    if (chatRoomMap.user_id != null) {
                        if (chatRoomMap.user_id.equals(creater) && chatRoomMap.recipient_id.equals(contactNumber)) {
                            setChatRoomID(chatRoomMap.chat_room_id);
                            isChatRoomAvaialble = true;
                            System.out.println("list chat rooms : " + chatRoomMap.chat_room_id + " chat room id");
                            System.out.println("list chat rooms : " + chatRoomMap.user_id + " user id");
                            System.out.println("list chat rooms : " + chatRoomMap.recipient_id + " recipient id");
                            readMessageList();
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        if (mDatabase != null)
            mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP).addChildEventListener(mChildEventListener);

    }

    private void setChatRoomID(String chatRoomID) {
        this.chatRoomID = chatRoomID;

    }

    private String getChatRoomID() {
        return this.chatRoomID;
    }

    private void readMessageList() {
        mReadMessageValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot messageDataSnapshot : dataSnapshot.getChildren()) {
                    final Message message = messageDataSnapshot.getValue(Message.class);
                    updateMessageReadStatus(message.message_id);
                    list.add(new ChatMessage(message.message_id, message.creater_id.equals(creater) ? ChatMessage.SENDER : ChatMessage.RECEIVER, message.message_type.equals(KEY.IMG) ? message.file_path : message.message_body, message.message_type, 3));
                    L.i("list chat messages : " + message.message_body);
                }
                refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if (mDatabase != null)
            mDatabase.child(KEY.TABLE_MESSAGE).child(getChatRoomID()).addValueEventListener(mReadMessageValueEventListener);

    }

    private void refresh() {
        mRecyclerView.scrollToPosition(list.size() - 1);
        mAdapter.notifyDataSetChanged();
        updateUnreadMessageCount(false);
    }

    private void updateMessageReadStatus(final String message_id) {
        L.i("update message " + message_id);
        mUpdateReadMessageStatusListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    MessageRecipient messageRecipient = post.getValue(MessageRecipient.class);
                    if (messageRecipient.message_id.equals(message_id) && messageRecipient.recipient_id.equals(creater)) {
                        if (!messageRecipient.is_read) {
                            mDatabase.child(KEY.TABLE_MESSAGE_RECIPIENT)
                                    .child(getChatRoomID())
                                    .child(post.getKey())
                                    .child(KEY.IS_READ)
                                    .setValue(true);
                            mDatabase.child(KEY.TABLE_MESSAGE_RECIPIENT)
                                    .child(getChatRoomID())
                                    .child(post.getKey())
                                    .child(KEY.MESSAGE_READ_TIME)
                                    .setValue(ServerValue.TIMESTAMP);
                            L.i("Message key : refresh");
                            L.i("Message key : " + dataSnapshot.getKey() + " --- " + post.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if (mDatabase != null)
            mDatabase.child(KEY.TABLE_MESSAGE_RECIPIENT).child(getChatRoomID()).addListenerForSingleValueEvent(mUpdateReadMessageStatusListener);
    }

    private void createChatRoom(final String message, final String file_type, final String message_type) {

        final String chatRoomID = mDatabase.child(KEY.TABLE_CHAT_ROOM).push().getKey();
        String chatRoomMapID = mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP).push().getKey();
        String chatRoomMapReverseID = mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP).push().getKey();

        ChatRoom chatRoom = new ChatRoom(chatRoomID, creater);
        ChatRoomMap chatRoomMap = new ChatRoomMap(chatRoomMapID, chatRoomID, creater, contactNumber);
        ChatRoomMap chatRoomReverseMap = new ChatRoomMap(chatRoomMapID, chatRoomID, contactNumber, creater);

        Map<String, Object> chatRoomValues = chatRoom.toMap();
        Map<String, Object> chatRoomMapValues = chatRoomMap.toMap();
        Map<String, Object> chatRoomMapReverseValues = chatRoomReverseMap.toMap();

        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + KEY.TABLE_CHAT_ROOM + "/" + chatRoomID, chatRoomValues);
        childUpdates.put("/" + KEY.TABLE_CHAT_ROOM_MAP + "/" + chatRoomMapID, chatRoomMapValues);
        childUpdates.put("/" + KEY.TABLE_CHAT_ROOM_MAP + "/" + chatRoomMapReverseID, chatRoomMapReverseValues);

        mCreateChatRoomListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isChatRoomAvaialble = true;
                setChatRoomID(chatRoomID);
                mDatabase.updateChildren(childUpdates);
                sendMessage(message, file_type, message_type, chatRoomID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if (mDatabase != null)
            mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP).addListenerForSingleValueEvent(mCreateChatRoomListener);

    }

    private void sendMessage(String newText, String file_type, String message_type, String chatRoomID) {
        String messageID = mDatabase.child(KEY.TABLE_MESSAGE).push().getKey();
        String messageRecipientID = mDatabase.child(KEY.TABLE_MESSAGE_RECIPIENT).push().getKey();

        Message message = new Message(
                messageID,
                chatRoomID,
                creater,
                message_type,
                message_type.equals(KEY.IMG) ? "" : newText,
                file_type,
                message_type.equals(KEY.IMG) ? newText : "");
        MessageRecipient messageRecipient = new MessageRecipient(messageRecipientID, contactNumber, messageID);

        Map<String, Object> messageValues = message.toMap();
        Map<String, Object> messageRecipientValues = messageRecipient.toMap();

        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + KEY.TABLE_MESSAGE + "/" + chatRoomID + "/" + messageID, messageValues);
        childUpdates.put("/" + KEY.TABLE_MESSAGE_RECIPIENT + "/" + chatRoomID + "/" + messageRecipientID, messageRecipientValues);

        emojiconEditText.setText("");

        if (mDatabase != null) mDatabase.updateChildren(childUpdates);

        updateUserAction(false);
        updateUnreadMessageCount(true);

    }

    private void updateUnreadMessageCount(final boolean action) {
        // for update recipient unread message count
        if (mDatabase != null) {
            Query query = mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postDataSnapshot : dataSnapshot.getChildren()) {
                        ChatRoomMap chatRoomMap = postDataSnapshot.getValue(ChatRoomMap.class);
                        if (chatRoomMap.user_id != null) {
                            if (action) {
                                if (chatRoomMap.user_id.equals(contactNumber) && chatRoomMap.recipient_id.equals(creater)) {
                                    int unReadMessageCount = chatRoomMap.unread_message_count;
                                    String key = postDataSnapshot.getKey();
                                    mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP).child(key).child(KEY.UNREAD_MESSAGE_COUNT).setValue(unReadMessageCount + 1);
                                    return;
                                }
                            } else {
                                if (chatRoomMap.user_id.equals(creater) && chatRoomMap.recipient_id.equals(contactNumber)) {
                                    int unReadMessageCount = chatRoomMap.unread_message_count;
                                    String key = postDataSnapshot.getKey();
                                    if (unReadMessageCount != 0)
                                        mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP).child(key).child(KEY.UNREAD_MESSAGE_COUNT).setValue(0);
                                    return;
                                }
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        L.i("MessagingFragment onResume: called...");
    }

    @Override
    public void onStop() {
        super.onStop();
        L.i("MessagingFragment onStop: called...");
        if (mDatabase != null) removeListeners();
    }

    private void removeListeners() {
        if (mChatRoomAvailableListener != null)
            mDatabase.removeEventListener(mChatRoomAvailableListener);
        if (mChildEventListener != null)
            mDatabase.child(KEY.TABLE_CHAT_ROOM_MAP).removeEventListener(mChildEventListener);
        if (mReadMessageValueEventListener != null)
            mDatabase.removeEventListener(mReadMessageValueEventListener);
        if (mCreateChatRoomListener != null) mDatabase.removeEventListener(mCreateChatRoomListener);
        if (mUpdateReadMessageStatusListener != null)
            mDatabase.child(KEY.TABLE_MESSAGE_RECIPIENT).child(getChatRoomID()).removeEventListener(mUpdateReadMessageStatusListener);
        mDatabase = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
       /* if (view.getId() == R.id.imgAttach){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                showMenuBelowLollipop();
            else
                showMenu();
        } else hideMenu();*/
        if (view == imgAttach) {
            mListener.setAttachFileListener();
        }
    }

   /* void showMenuBelowLollipop() {
        int cx = (attachmentLayout.getLeft() + attachmentLayout.getRight());
        int cy = attachmentLayout.getTop();
        int radius = Math.max(attachmentLayout.getWidth(), attachmentLayout.getHeight());

        try {
            SupportAnimator animator = ViewAnimationUtils.createCircularReveal(attachmentLayout, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(300);

            if (isHidden) {
                //Log.e(getClass().getSimpleName(), "showMenuBelowLollipop");
                attachmentLayout.setVisibility(View.VISIBLE);
                animator.start();
                isHidden = false;
            } else {
                SupportAnimator animatorReverse = animator.reverse();
                animatorReverse.start();
                animatorReverse.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {
                    }

                    @Override
                    public void onAnimationEnd() {
                        //Log.e("MainActivity", "onAnimationEnd");
                        isHidden = true;
                        attachmentLayout.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel() {
                    }

                    @Override
                    public void onAnimationRepeat() {
                    }
                });
            }
        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), "try catch");
            isHidden = true;
            attachmentLayout.setVisibility(View.INVISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void showMenu() {
        int cx = (attachmentLayout.getLeft() + attachmentLayout.getRight());
        int cy = attachmentLayout.getTop();
        int radius = Math.max(attachmentLayout.getWidth(), attachmentLayout.getHeight());

        if (isHidden) {
            Animator anim = android.view.ViewAnimationUtils.createCircularReveal(attachmentLayout, cx, cy, 0, radius);
            attachmentLayout.setVisibility(View.VISIBLE);
            anim.start();
            isHidden = false;
        } else {
            Animator anim = android.view.ViewAnimationUtils.createCircularReveal(attachmentLayout, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    attachmentLayout.setVisibility(View.INVISIBLE);
                    isHidden = true;
                }
            });
            anim.start();
        }
    }

    private void hideMenu() {
        attachmentLayout.setVisibility(View.GONE);
        isHidden = true;
    }*/

    public void uploadFile(Context context, final Uri uri, final String fileName, final String fileType, String creater, String contactNumber) {

        prefs = new Prefs(context);
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(KEY.COMMON_STORAGE_PATH);
        StorageReference riversRef = mStorageRef.child(KEY.FILE_STORAGE + "/" + Calendar.getInstance().getTimeInMillis() + fileName);

        riversRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        send(downloadUrl.toString(), fileType, KEY.IMG);
                        L.t(getContext(), "file upload success...");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        L.t(getContext(), "Upload failed...");
                        L.i("ProfileActivity upload image failed : " + exception.getMessage());

                    }
                });
    }

}
