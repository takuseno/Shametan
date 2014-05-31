package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by takuma on 2014/05/31.
 */
public class NoteGridAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private int layoutId;
    private File[] imgList;
    Context cotext;

    int dispWidth;

    public NoteGridAdapter(Context context,int layoutId,File[] imgList){
        super();
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.imgList = imgList;
        this.cotext = context;

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        dispWidth = size.x;

    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        String mFilePath = imgList[position].getPath();

        GridViewHolder holder;
        if(convertView == null) {
            holder = new GridViewHolder();
            convertView = inflater.inflate(layoutId, parent, false);
            ViewGroup.LayoutParams params = convertView.getLayoutParams();
            params.width = dispWidth/2;
            params.height = dispWidth/2;
            convertView.setLayoutParams(params);
            holder.imageView = (ImageView) convertView.findViewById(R.id.gridImageVIew);
            convertView.setTag(holder);
        }

        else{
            holder = (GridViewHolder)convertView.getTag();
        }
        holder.imageView.setTag(mFilePath);
        holder.imageView.setImageResource(R.drawable.dummy);
        NotePhotoAsync task = new NotePhotoAsync(holder.imageView, this,dispWidth);
        task.execute(mFilePath);

        return convertView;
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
