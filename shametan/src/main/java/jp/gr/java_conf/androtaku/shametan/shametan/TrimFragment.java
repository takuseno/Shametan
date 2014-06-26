package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.pm.ActivityInfo;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by takuma on 2014/05/05.
 */
public class TrimFragment extends Fragment{
    ImageView imageView;
    TrimmingView trimmingView;
    FrameLayout frameLayout;
    String imagePath;

    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");

    private final static int ORIEN_VERTICAL = 1;
    private final static int ORIEN_HORIZON = 2;

    private float PIXEL_LIMIT;
    private float rsz_ratio;
    private Bitmap setBmp;

    private int dispWidth,dispHeight;
    private int imageWidth,imageHeight;


    public TrimFragment(){

    }

    public static TrimFragment newInstance(){
        TrimFragment fragment = new TrimFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.show();

        final View rootView = inflater.inflate(R.layout.trim_layout,container,false);
        init(rootView);

        rootView.post(new Runnable() {
            @Override
            public void run() {
                dispWidth = rootView.getWidth();
                dispHeight = rootView.getHeight();
                trimmingView.init(dispWidth,dispHeight);
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageWidth = imageView.getWidth();
                        imageHeight = imageView.getHeight();
                        trimmingView.putWidth(imageWidth);
                        trimmingView.putHeight(imageHeight);
                        Log.i("image width",String.valueOf(imageWidth));
                        Log.i("image height",String.valueOf(imageHeight));
                    }
                });
            }
        });
        if(getActivity().getClass() == GetImageFromCameraActivity.class) {
            GetImageFromCameraActivity.menuType = GetImageFromCameraActivity.MENU_TRIM;
        }
        else if(getActivity().getClass() == GetImageFromGalleryActivity.class) {
            GetImageFromGalleryActivity.menuType = GetImageFromGalleryActivity.MENU_TRIM;
        }
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void init(View v){
        //if(getArguments().getInt("orientation") == ORIEN_VERTICAL){
          //  getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //}
        //else if(getArguments().getInt("orientation") == ORIEN_HORIZON){
        //    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      //  }

        imageView = (ImageView)v.findViewById(R.id.trimImageView);
        imagePath = getArguments().getString("image_path");
        WindowManager wm = getActivity().getWindowManager();
        Display disp = wm.getDefaultDisplay();
        if(Build.VERSION.SDK_INT < 13){
            dispWidth = disp.getWidth();
            dispHeight = disp.getHeight();
        }
        else {
            Point point = new Point();
            disp.getSize(point);
            dispWidth = point.x;
            dispHeight = point.y;
        }
        PIXEL_LIMIT = dispWidth * dispHeight * 3;
        Bitmap srcBmp = compressImage(imagePath);
        if(getArguments().getInt("orientation") == ORIEN_VERTICAL){
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBmp = Bitmap.createBitmap(srcBmp,0,0,
                    srcBmp.getWidth(),srcBmp.getHeight(),matrix,true);
            imageView.setImageBitmap(rotatedBmp);
            setBmp = rotatedBmp;
        }
        else{
            imageView.setImageBitmap(srcBmp);
            setBmp = srcBmp;
        }

        frameLayout = (FrameLayout)v.findViewById(R.id.trim_framelayout);

        trimmingView = new TrimmingView(getActivity());
        frameLayout.addView(trimmingView);
    }

    public Bitmap compressImage(String imageName){
        Bitmap bmpSrc;
        bmpSrc = BitmapFactory.decodeFile(imageName);
        float srcWidth = bmpSrc.getWidth();
        float srcHeight = bmpSrc.getHeight();

        rsz_ratio = (float)Math.sqrt(PIXEL_LIMIT / (srcWidth * srcHeight));
        Log.i("ratio",String.valueOf(rsz_ratio));
        Matrix matrix = new Matrix();

        matrix.postScale(rsz_ratio,rsz_ratio);

        Bitmap bmpRsz = Bitmap.createBitmap(bmpSrc,0,0,bmpSrc.getWidth(),
                bmpSrc.getHeight(),matrix,true);
        return bmpRsz;
    }

    public void saveImage(File imagePath){
        float[] pointX = trimmingView.getPointsX();
        float[] pointY = trimmingView.getPointsY();

        float rszX1,rszX2,rszY1,rszY2;

       // if(getArguments().getInt("orientation") == ORIEN_VERTICAL){
        //    rszX1 = (pointY[0]) * ((float) setBmp.getWidth() / (float) imageWidth);
       //     rszX2 = (pointY[1]) * ((float) setBmp.getWidth() / (float) imageWidth);
       //     rszY1 = ((dispWidth - pointX[1])) * ((float) setBmp.getHeight() / (float) imageHeight);
       //     rszY2 = ((dispWidth - pointX[0])) * ((float) setBmp.getHeight() / (float) imageHeight);
        //}
       // else {
            rszX1 = (pointX[0]) * ((float) setBmp.getWidth() / (float) imageWidth);
            rszX2 = (pointX[1]) * ((float) setBmp.getWidth() / (float) imageWidth);
            rszY1 = (pointY[0]) * ((float) setBmp.getHeight() / (float) imageHeight);
            rszY2 = (pointY[1]) * ((float) setBmp.getHeight() / (float) imageHeight);
       // }

        Bitmap saveBmp = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            setBmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] bytes = baos.toByteArray();
            BitmapRegionDecoder regionDecoder = BitmapRegionDecoder.newInstance(bytes,0,bytes.length,false);
            Rect rect = new Rect((int)rszX1,(int)rszY1,(int)rszX2,(int)rszY2);
            Bitmap tempBmp = regionDecoder.decodeRegion(rect,null);
            float srcWidth = tempBmp.getWidth();
            float srcHeight = tempBmp.getHeight();

            if(srcWidth < srcHeight * (dispWidth / dispHeight)){
                rsz_ratio = dispHeight / srcHeight;
            }
            else{
                rsz_ratio = dispWidth / srcWidth;
            }
            Matrix matrix = new Matrix();
            matrix.postScale(rsz_ratio,rsz_ratio);
            saveBmp = Bitmap.createBitmap(tempBmp,0,0,tempBmp.getWidth(),
                    tempBmp.getHeight(),matrix,true);
            /*if(getArguments().getInt("orientation") == ORIEN_VERTICAL){
                tempBmp = saveBmp;
                matrix = new Matrix();
                matrix.postRotate(90);
                saveBmp = Bitmap.createBitmap(tempBmp,0,0,
                        tempBmp.getWidth(),tempBmp.getHeight(),matrix,true);
                tempBmp = saveBmp;
                srcWidth = tempBmp.getWidth();
                srcHeight = tempBmp.getHeight();

                if(srcWidth < srcHeight * (dispWidth / dispHeight)){
                    rsz_ratio = dispHeight / srcHeight;
                }
                else{
                    rsz_ratio = dispWidth / srcWidth;
                }
                matrix = new Matrix();
                matrix.postScale(rsz_ratio,rsz_ratio);
                saveBmp = Bitmap.createBitmap(tempBmp,0,0,tempBmp.getWidth(),
                        tempBmp.getHeight(),matrix,true);
            }*/
        }catch (IOException e){
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        saveBmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        try {
            fos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.trimming:
                final Calendar calendar = Calendar.getInstance();
                String imagePath = basePath.getPath() + "/" + String.valueOf(calendar.getTimeInMillis()) + ".jpg";
                saveImage(new File(imagePath));
                toDrawLine(imagePath);
                break;

            case R.id.ratate:
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap tempBmp = setBmp;
                Bitmap rotatedBmp = Bitmap.createBitmap(tempBmp,0,0,
                        tempBmp.getWidth(),tempBmp.getHeight(),matrix,true);
                imageView.setImageBitmap(rotatedBmp);
                setBmp = rotatedBmp;
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageWidth = imageView.getWidth();
                        imageHeight = imageView.getHeight();
                        trimmingView.putWidth(imageWidth);
                        trimmingView.putHeight(imageHeight);
                    }
                });
                break;

            default:
        }

        return super.onOptionsItemSelected(menuItem);
    }
    public void toDrawLine(String imagePath){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("trimed_image_path",imagePath);
        bundle.putString("cst_path",getArguments().getString("cst_path"));
        DrawLineFragment fragment = new DrawLineFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment,"drawline_fragment");
        transaction.addToBackStack("trim");
        transaction.commit();
    }
}
