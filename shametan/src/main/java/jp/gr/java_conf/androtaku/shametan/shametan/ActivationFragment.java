package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by takuma on 2014/07/01.
 */
public class ActivationFragment extends Fragment {
    Button button;
    TextView textView;
    ImageView deviceImageView;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private int startOrientation;
    private static final int ORIEN_VERTICAL = 1;
    private static final int ORIEN_HORIZON = 2;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        prefs = getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE);
        editor = prefs.edit();

        final View rootView = inflater.inflate(R.layout.activation_layout,container,false);
        init(rootView);

        rootView.post(new Runnable() {
            @Override
            public void run() {
                if(startOrientation == ORIEN_VERTICAL){
                    editor.putInt("vDispWidth",rootView.getWidth());
                    editor.putInt("vDispHeight",rootView.getHeight());
                    Log.i("vdispwidth",""+rootView.getWidth());
                    Log.i("vDispHeight",""+rootView.getHeight());
                    editor.putBoolean("vDispObtained",true);
                    editor.commit();
                }
                else{
                    editor.putInt("lDispWidth",rootView.getWidth());
                    editor.putInt("lDispHeight",rootView.getHeight());
                    Log.i("ldispwidth",""+rootView.getWidth());
                    Log.i("lDispHeight",""+rootView.getHeight());
                    editor.putBoolean("lDispObtained",true);
                    editor.commit();
                }
                if(prefs.getBoolean("vDispObtained",false) && prefs.getBoolean("lDispObtained",false)){
                    button.setClickable(true);
                    button.setVisibility(View.VISIBLE);
                    textView.setText(getString(R.string.activated_text));
                    if(startOrientation == ORIEN_VERTICAL){
                        deviceImageView.setImageResource(R.drawable.rotate_vertical_checked);
                    }
                    else {
                        deviceImageView.setImageResource(R.drawable.rotate_horizontal_checked);
                    }
                }
            }
        });
        return rootView;
    }

    public void init(View v){
        button = (Button)v.findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("isActivated",true);
                editor.commit();
                toNote();
            }
        });
        button.setClickable(false);
        button.setVisibility(View.INVISIBLE);
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

        deviceImageView = (ImageView)v.findViewById(R.id.device_imageView);
        if(startOrientation == ORIEN_VERTICAL){
            deviceImageView.setImageResource(R.drawable.rotate_vertical);
        }
        else{
            deviceImageView.setImageResource(R.drawable.rotate_horizontal);
        }
    }

    public void toNote(){
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("cst_file", "/root.cst");
        SelectNoteFragment fragment = new SelectNoteFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack("activtion");
        transaction.commit();
    }
}
