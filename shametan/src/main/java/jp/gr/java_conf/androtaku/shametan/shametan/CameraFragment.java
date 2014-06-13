package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.File;
import java.util.zip.Inflater;

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

    //constructor
    public CameraFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.camera_layout,container,false);
        //get cst file path
        cstPath = getArguments().getString("cst_file");
        //initialize views
        init(rootView);
        return rootView;
    }

    //function of initializing views
    public void init(View v){
        cameraView = new CameraView(getActivity(),getFragmentManager(),cstPath);
        cameraLayout = (LinearLayout)v.findViewById(R.id.camera_view);
        cameraLayout.addView(cameraView);

        shutter = (ImageButton)v.findViewById(R.id.shutter);
        shutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.myAutoFocus();
            }
        });
    }


}
