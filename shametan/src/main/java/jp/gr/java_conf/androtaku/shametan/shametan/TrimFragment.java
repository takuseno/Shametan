package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    ImageView imageView;
    LinearLayout linearLayout;
    String imagePath;

    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");
    private final static int ORIEN_VERTICAL = 1;
    private final static int ORIEN_HORIZON = 2;

    private int dispWidth,dispHeight;

    public TrimFragment(){

    }

    public static TrimFragment newInstance(){
        TrimFragment fragment = new TrimFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.show();

        WindowManager wm =
                (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        dispWidth = size.x;
        dispHeight = size.y;

        View rootView = inflater.inflate(R.layout.trim_layout,container,false);
        init(rootView);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void init(View v){
        imageView = (ImageView)v.findViewById(R.id.trimImageView);
        imagePath = getArguments().getString("image_path");
        if("camera".equals(getArguments().getString("from"))){
            //imageView.fromFragment = imageView.FROM_CAMERA;

        }
        else if("gallery".equals(getArguments().getString("from"))){
            //imageView.fromFragment = imageView.FROM_GALLERY;
        }
        imageView.setImageBitmap(compressImage(imagePath));
        if(getArguments().getInt("orientation") == ORIEN_HORIZON){
            //imageView.setRotation(90);
        }

        trimButton = (Button)v.findViewById(R.id.trimButton);
        trimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                String imagePath = basePath.getPath() + "/" + String.valueOf(calendar.getTimeInMillis()) + ".jpg";
                saveImage(new File(imagePath));
                toDrawLine(imagePath);
            }
        });

        linearLayout = (LinearLayout)v.findViewById(R.id.screenshot_layout);
    }

    public Bitmap compressImage(String imageName){
        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imageName, opt);

        int scaleW = opt.outWidth / dispWidth;
        int scaleH = opt.outHeight / dispHeight;

        opt.inSampleSize = Math.max(scaleW, scaleH);
        opt.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imageName, opt);

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        float scale = Math.min((float)dispWidth/w, (float)dispHeight/h);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        bmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);

        return bmp;
    }

    public void saveImage(File imagePath){
        linearLayout.setDrawingCacheEnabled(true);
        Bitmap saveBitmap = Bitmap.createBitmap(linearLayout.getDrawingCache());
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
        WindowManager windowManager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int orentation = 1;
        switch(rotation){
            case Surface.ROTATION_0:
                orentation = ORIEN_VERTICAL;
                break;
            case Surface.ROTATION_90:
                orentation = ORIEN_HORIZON;
                break;
            case Surface.ROTATION_180:
                orentation = ORIEN_VERTICAL;
                break;
            case Surface.ROTATION_270:
                orentation = ORIEN_HORIZON;
                break;
        }

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("trimed_image_path",imagePath);
        bundle.putString("cst_file",getArguments().getString("cst_file"));
        bundle.putInt("orientation",orentation);
        DrawLineFragment fragment = new DrawLineFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment,"drawline_fragment");
        transaction.addToBackStack("trim");
        transaction.commit();
    }
}
