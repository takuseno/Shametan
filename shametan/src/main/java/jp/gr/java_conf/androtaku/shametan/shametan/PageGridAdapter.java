package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by takuma on 2014/06/04.
 */
public class PageGridAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int layoutId;
    private File[] pageList;
    Context cotext;

    int dispWidth;

    public PageGridAdapter(Context context,int layoutId,File[] pageList){
        super();
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.pageList = pageList;
        this.cotext = context;

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        dispWidth = size.x;

    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        File mFilePath = new File(pageList[position].getPath());

        Animation anim = AnimationUtils.loadAnimation(cotext,R.anim.note_item_anim);

        GridViewHolder holder;
        if(convertView == null) {
            holder = new GridViewHolder();
            convertView = inflater.inflate(layoutId, parent, false);
            ViewGroup.LayoutParams params = convertView.getLayoutParams();
            params.width = dispWidth/2;
            params.height = dispWidth/2;
            convertView.setLayoutParams(params);
            holder.imageView = (ImageView) convertView.findViewById(R.id.gridImageView);
            convertView.setTag(holder);
        }

        else{
            holder = (GridViewHolder)convertView.getTag();
        }

        holder.imageView.setTag(mFilePath);
        holder.imageView.setImageResource(R.drawable.dummy);
        PagePhotoAsync task = new PagePhotoAsync(holder.imageView, this, dispWidth);
        task.execute(mFilePath.getPath());

        convertView.startAnimation(anim);

        return convertView;
    }

    public void refreshData(File[] pageList){
        this.pageList = pageList;
    }

    @Override
    public int getCount(){
        return pageList.length;
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
