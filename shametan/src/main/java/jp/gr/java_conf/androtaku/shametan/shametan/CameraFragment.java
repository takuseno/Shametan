package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by takuma on 2014/05/03.
 */
public class CameraFragment extends Fragment {
    //declare views
    CameraView cameraView;
    ImageButton shutter;
    LinearLayout cameraLayout;

    //declare String of cst file path
    String cstPath;

    private boolean isTaking = false;

    //constructor
    public CameraFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.hide();

        View rootView = inflater.inflate(R.layout.camera_layout,container,false);
        //get cst file path
        cstPath = getArguments().getString("cst_path");
        //initialize views
        init(rootView);
        return rootView;
    }

    //function of initializing views
    public void init(View v){
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        cameraView = new CameraView(getActivity(),getActivity().getSupportFragmentManager(),cstPath);
        cameraLayout = (LinearLayout)v.findViewById(R.id.camera_view);
        cameraLayout.addView(cameraView);

        shutter = (ImageButton)v.findViewById(R.id.shutter);
        shutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isTaking) {
                    isTaking = true;
                    cameraView.myAutoFocus(true);
                }
            }
        });
        isTaking = false;
    }
}
