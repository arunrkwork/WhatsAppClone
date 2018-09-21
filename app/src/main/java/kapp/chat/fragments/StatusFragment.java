package kapp.chat.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kapp.chat.R;
import kapp.chat.utill.L;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    private static final String TAG = "Whatsapp";

    public StatusFragment() {
        // Required empty public constructor
    }

    public static StatusFragment newInstance(){
        return new StatusFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        L.i( "StatusFragment onCreateView: called...");
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        L.i( "StatusFragment onViewCreated: called...");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        L.i( "StatusFragment onActivityCreated: called...");
    }

    @Override
    public void onPause() {
        super.onPause();
        L.i( "StatusFragment onPause: called...");
    }

    @Override
    public void onResume() {
        super.onResume();
        L.i( "StatusFragment onResume: called...");
    }

}
