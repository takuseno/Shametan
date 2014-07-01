package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by takuma on 2014/07/01.
 */
public class ActivationFragment extends Fragment {
    Button button;
    TextView textView;

    private int startOrientation;
    private static final int ORIEN_VERTICAL = 1;
    private static final int ORIEN_HORIZON = 2;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.activation_layout,container,false);
        rootView.post(new Runnable() {
            @Override
            public void run() {

            }
        });
        return rootView;
    }

    public void init(View v){
        button = (Button)v.findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        button.setClickable(false);
        textView = (TextView)v.findViewById(R.id.activation_text);
        WindowManager windowManager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                startOrientation = ORIEN_VERTICAL;
                break;
            case Surface.ROTATION_90:
                startOrientation = ORIEN_HORIZON;
                break;
            case Surface.ROTATION_180:
                startOrientation = ORIEN_VERTICAL;
                break;
            case Surface.ROTATION_270:
                startOrientation = ORIEN_HORIZON;
                break;
        }
    }
}
