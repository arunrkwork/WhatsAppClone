package kapp.chat.fragments;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import kapp.chat.R;
import kapp.chat.utill.L;
import kapp.chat.views.CameraPreview;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "Whatsapp";
    private CameraPreview mPreview;
    private RelativeLayout mLayout;
    FloatingActionButton fabCamera;

    public CameraFragment() {
        // Required empty public constructor
    }

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        L.i( "CameraFragment onCreateView: called...");
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        L.i( "CameraFragment onViewCreated: called...");
        mLayout = view.findViewById(R.id.camera_preview);
        fabCamera = view.findViewById(R.id.fabCamera);
        fabCamera.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        L.i( "CameraFragment onActivityCreated: called...");
    }

    @Override
    public void onResume() {
        super.onResume();
        L.i( "CameraFragment onResume: called...");
      //  mPreview = new CameraPreview(getActivity(), 0, CameraPreview.LayoutMode.NoBlank);
     //   RelativeLayout.LayoutParams previewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
     //   mLayout.addView(mPreview, 0, previewLayoutParams);
    }


    @Override
    public void onPause() {
        super.onPause();
        L.i( "CameraFragment onPause: called...");
     //   mPreview.stop();
     //   mLayout.removeView(mPreview);
      //  mPreview = null;
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == fabCamera) takePhoto();
    }

    private void takePhoto() {
    }
}
