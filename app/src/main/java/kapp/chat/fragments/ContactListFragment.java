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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kapp.chat.R;
import kapp.chat.adapter.ContactListAdapter;
import kapp.chat.db.KEY;
import kapp.chat.db.pojo.User;
import kapp.chat.utill.L;
import kapp.chat.utill.Prefs;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactListFragment extends Fragment implements ContactListAdapter.OnInteractContactListAdapter {

    private DatabaseReference mDatabase;
    private ValueEventListener mValueEventListener;
    private RecyclerView mRecyclerView;
    private ContactListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<User> list;
    Prefs prefs;
    OnInteractContactListFragment mListener;

    public interface OnInteractContactListFragment {
        public void setOnInteractContactListFragment(String name, String contact, String img, String lastSeen);
    }

    public static ContactListFragment newInstance(){
        return new ContactListFragment();
    }

    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInteractContactListFragment) {
            mListener = (OnInteractContactListFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInteractContactListFragment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prefs = new Prefs(getActivity());
        list = new ArrayList<>();
        mAdapter = new ContactListAdapter(getActivity(), this, list);
        mRecyclerView.setAdapter(mAdapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot listSnapshot: dataSnapshot.getChildren()) {
                    User user = listSnapshot.getValue(User.class);
                    if (user != null) {
                        if (!user.user_id.equals(prefs.getString(KEY.USER_ID))) list.add(user);
                    }
                    mAdapter.notifyDataSetChanged();
                    L.i("list values : " + user.mobile_no + " -- " + user.profile_image_path);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child(KEY.TABLE_USER).addValueEventListener(mValueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDatabase != null) {
            if (mValueEventListener != null) mDatabase.removeEventListener(mValueEventListener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setOnInteractContactListAdapter(String contactName, String contactNumber, String contactImage, String contactLastSeen) {
        if (mListener != null) mListener.setOnInteractContactListFragment(contactName, contactNumber, contactImage, contactLastSeen);
    }

}
