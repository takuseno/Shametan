package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    Context cotext;

    int dispWidth;

    boolean scrolled = false;

    int[] diplayedId = new int[15];
    boolean[] notShowed = new boolean[15];
    int displayedCount = 0;

    public GridAdapter(Context context,int layoutId,File[] imgList){
        super();
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.imgList = imgList;
        this.cotext = context;

        for(int i = 0;i < 15;++i){
            diplayedId[i] = 0;
            notShowed[i] = false;
        }

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        dispWidth = size.x;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        String mFilePath = imgList[position].getPath();
        if(displayedCount == 15){
            int[] temp = diplayedId;
            boolean[] tempflag = notShowed;
            for(int i = 0;i < 14;++i){
                diplayedId[i] = temp[i + 1];
                notShowed[i] = tempflag[i + 1];
            }
            displayedCount = 14;
        }
        diplayedId[displayedCount] = position;
        notShowed[displayedCount] = false;
        Log.i("id",String.valueOf(position));

        GridViewHolder holder;
        if(convertView == null) {
            holder = new GridViewHolder();
            convertView = inflater.inflate(layoutId, parent, false);
            ViewGroup.LayoutParams params = convertView.getLayoutParams();
            params.width = dispWidth/3;
            params.height = dispWidth/3;
            convertView.setLayoutParams(params);

            holder.imageView = (ImageView) convertView.findViewById(R.id.gridImageVIew);
            convertView.setTag(holder);
        }

        else{
            holder = (GridViewHolder)convertView.getTag();
        }
        holder.imageView.setTag(mFilePath);
        holder.imageView.setImageResource(R.drawable.dummy);
        if(!scrolled) {
            PhotoAsync task = new PhotoAsync(holder.imageView, this, cotext);
            task.execute(mFilePath);
            notShowed[displayedCount] = true;
        }

        ++displayedCount;
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
