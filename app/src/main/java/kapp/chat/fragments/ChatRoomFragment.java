package kapp.chat.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kapp.chat.R;
import kapp.chat.adapter.ChatRoomAdapter;
import kapp.chat.db.KEY;
import kapp.chat.db.pojo.ChatRoomMap;
import kapp.chat.db.pojo.User;
import kapp.chat.utill.L;
import kapp.chat.utill.Prefs;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomFragment extends Fragment implements ChatRoomAdapter.OnInteractChatRoomListAdapter {

    private static final String TAG = "Whatsapp";
    private DatabaseReference mDatabase;
    private ChildEventListener mChildEventListener;
    private ValueEventListener mValueEventListener;
    private RecyclerView mRecyclerView;
    private ChatRoomAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<User> list;
    Prefs prefs;
    private String creater;
    private OnInteractChatRoomListFragment mListener;


    public interface OnInteractChatRoomListFragment {
        void setOnInteractChatRoomListFragment(String contactName, String contactNumber, String contactImage, String contactLastSeen);
    }

    public ChatRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInteractChatRoomListFragment) {
            mListener = (OnInteractChatRoomListFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInteractContactListFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static ChatRoomFragment newInstance() {
        return new ChatRoomFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        L.i("ChatRoomFragment onCreateView: called...");
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        L.i("ChatRoomFragment onViewCreated: called...");

        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        L.i("ChatRoomFragment onActivityCreated: called...");
        prefs = new Prefs(getActivity());
        creater = prefs.getString(KEY.MOBILE_NO);
        list = new ArrayList<>();
        mAdapter = new ChatRoomAdapter(getActivity(), this, list);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updateChatRoom(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateChatRoom(dataSnapshot);
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

    private void updateChatRoom(DataSnapshot dataSnapshot) {
        ChatRoomMap chatRoomMap = dataSnapshot.getValue(ChatRoomMap.class);

        if (chatRoomMap.user_id.equals(creater)) {
            if (chatRoomMap.is_typing) {
                if (list.size() == 0) loadAll(chatRoomMap);
                else {
                    for (User user : list) {
                        if (user.mobile_no.equals(chatRoomMap.recipient_id)) {
                            int position = list.indexOf(user);
                            user.unread_message_count = chatRoomMap.unread_message_count;
                            user.user_status = "is typing...";
                            list.remove(position);
                            list.add(user);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } else {
                loadAll(chatRoomMap);
            }
        }
    }

    private void loadAll(final ChatRoomMap chatRoomMap) {
        list.clear();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabase.child(KEY.TABLE_USER).child(chatRoomMap.recipient_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    user.unread_message_count = chatRoomMap.unread_message_count;
                    list.add(user);
                    mAdapter.notifyDataSetChanged();
                } else L.i("Chat Room : list null");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        L.i("ChatRoomFragment onPause: called...");
    }

    @Override
    public void onResume() {
        super.onResume();
        L.i("ChatRoomFragment onResume: called...");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDatabase != null) mDatabase.removeEventListener(mChildEventListener);
        mDatabase = null;
    }

    @Override
    public void setOnInteractChatRoomListAdapter(String contactName, String contactNumber, String contactImage, String contactLastSeen) {
        if (mListener != null)
            mListener.setOnInteractChatRoomListFragment(contactName, contactNumber, contactImage, contactLastSeen);
    }
}
