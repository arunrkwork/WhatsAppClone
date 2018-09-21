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
public class CallsFragment extends Fragment {

    public CallsFragment() {
        // Required empty public constructor
    }

    public static CallsFragment newInstance(){
        return new CallsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        L.i( "CallsFragment onCreateView: ");
        return inflater.inflate(R.layout.fragment_calls, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        L.i("CallsFragment onViewCreated: called...");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        L.i("CallsFragment onActivityCreated: called...");

    }

    @Override
    public void onPause() {
        super.onPause();
        L.i("CallsFragment onPause: called...");
    }

    @Override
    public void onResume() {
        super.onResume();
        L.i("CallsFragment onResume: called...");
    }
}
