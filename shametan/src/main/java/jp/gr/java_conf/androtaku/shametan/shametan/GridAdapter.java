package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by takuma on 2014/05/18.
 */
public class GridAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int layoutId;
    private File[] imgList;

    public GridAdapter(Context context,int layoutId,File[] imgList){
        super();
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.imgList = imgList;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        String mFilePath = imgList[position].getPath();
        convertView = inflater.inflate(layoutId, parent, false);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.gridImageVIew);
        imageView.setImageBitmap(compressImage(mFilePath));
        return convertView;
    }

    public Bitmap compressImage(String imageName){
        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imageName, opt);

        int scaleW = opt.outWidth / 320;
        int scaleH = opt.outHeight / 240;

        opt.inSampleSize = Math.max(scaleW, scaleH);
        opt.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imageName, opt);

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        float scale = Math.min((float)320/w, (float)240/h);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        bmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);

        return bmp;
    }

    @Override
    public int getCount(){
        return imgList.length;
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public long getItemId(int position){
        return 0;
    }
}
