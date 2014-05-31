package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by takuma on 2014/05/05.
 */
public class TrimFragment extends Fragment {
    Button trimButton;
    TrimImageView imageView;
    String imagePath;

    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");

    public TrimFragment(){

    }

    public static TrimFragment newInstance(){
        TrimFragment fragment = new TrimFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.trim_layout,container,false);
        init(rootView);
        return rootView;
    }

    public void init(View v){
        imageView = (TrimImageView)v.findViewById(R.id.trimImageView);
        imagePath = getArguments().getString("image_path");
        if("camera".equals(getArguments().getString("from"))){
            imageView.fromFragment = imageView.FROM_CAMERA;
        }
        else if("gallery".equals(getArguments().getString("from"))){
            imageView.fromFragment = imageView.FROM_GALLERY;
        }
        imageView.setImage(imagePath);
        final File sourceImage = new File(imagePath);

        trimButton = (Button)v.findViewById(R.id.trimButton);
        trimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                String imagePath = basePath.getPath() + "/" + String.valueOf(calendar.getTimeInMillis()) + ".jpg";
                saveImage(new File(imagePath));
                sourceImage.delete();
                toDrawLine(imagePath);
            }
        });
    }

    public void saveImage(File imagePath){
        imageView.setDrawingCacheEnabled(true);
        Bitmap saveBitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        saveBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
        try {
            fos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        imageView.setDrawingCacheEnabled(false);
    }

    public void toDrawLine(String imagePath){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("trimed_image_path",imagePath);
        DrawLineFragment fragment = new DrawLineFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment,"drawline_fragment");
        transaction.addToBackStack("trim");
        transaction.commit();
    }
}
