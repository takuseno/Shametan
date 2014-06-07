package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
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
 * Created by takuma on 2014/05/18.
 */
public class GridAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int layoutId;
    private File[] imgList;
    Context cotext;

    int dispWidth;

    boolean scrolled = false;

    int[] diplayedId;
    boolean[] showed;

    public GridAdapter(Context context,int layoutId,File[] imgList){
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

        diplayedId = new int[size.y/(dispWidth/3)*3 + 3];
        for(int i = 0;i < diplayedId.length;++i){
            diplayedId[i] = 0;
        }
        Log.i("idlength",String.valueOf(diplayedId.length));

        showed = new boolean[imgList.length];
        for(int i = 0;i < imgList.length;++i) {
            showed[i] = false;
        }
        Log.i("imglistlength",String.valueOf(showed.length));


    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        String mFilePath = imgList[position].getPath();
        showed[position] = false;

        Animation anim = AnimationUtils.loadAnimation(cotext,R.anim.note_item_anim);

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
            PhotoAsync task = new PhotoAsync(holder.imageView, this);
            task.execute(mFilePath);
            showed[position] = true;
            Log.i("gotadapter",String.valueOf(position));
        }

        convertView.startAnimation(anim);

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
