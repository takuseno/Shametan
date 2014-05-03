package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.zip.Inflater;

/**
 * Created by takuma on 2014/05/03.
 */
public class CameraFragment extends Fragment {
    CameraView cameraView;
    LinearLayout cameraLayout;
    public CameraFragment(){

    }

    public static CameraFragment newInstance(){
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.camera_layout,container,false);
        init(rootView);
        return rootView;
    }

    public void init(View v){
        cameraView = new CameraView(getActivity());
        cameraLayout = (LinearLayout)v.findViewById(R.id.camera_view);
        cameraLayout.addView(cameraView);
    }
}
