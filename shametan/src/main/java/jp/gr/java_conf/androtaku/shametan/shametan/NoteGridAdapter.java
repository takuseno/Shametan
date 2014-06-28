package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by takuma on 2014/05/31.
 */
public class NoteGridAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private int layoutId;
    private File[] noteList;
    Context cotext;

    int dispWidth;

    int[] colorIds = {
            R.drawable.note_title_blue,
            R.drawable.note_title_blue,R.drawable.note_title_green,
            R.drawable.note_title_pink,R.drawable.note_title_yellow,
            R.drawable.note_title_violet,R.drawable.note_title_orange
    };

    public NoteGridAdapter(Context context,int layoutId,File[] noteList){
        super();
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.noteList = noteList;
        this.cotext = context;

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        if(Build.VERSION.SDK_INT < 13){
            dispWidth = disp.getWidth();
        }else {
            Point size = new Point();
            disp.getSize(size);
            dispWidth = size.x;
        }

    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        File mFilePath = new File(noteList[position].getPath());

        Animation anim = AnimationUtils.loadAnimation(cotext,R.anim.note_item_anim);

        SelectNoteHolder holder;
        if(convertView == null) {
            holder = new SelectNoteHolder();
            convertView = inflater.inflate(layoutId, parent, false);
            ViewGroup.LayoutParams params = convertView.getLayoutParams();
            params.width = dispWidth/2;
            params.height = dispWidth/2;
            convertView.setLayoutParams(params);

            holder.imageView = (ImageView) convertView.findViewById(R.id.select_note_image);
            holder.textView = (TextView) convertView.findViewById(R.id.select_note_text);
            convertView.setTag(holder);
        }
        else{
            holder = (SelectNoteHolder)convertView.getTag();
        }

        holder.imageView.setTag(mFilePath);
        int index = mFilePath.getName().indexOf(".");
        holder.textView.setText(mFilePath.getName().substring(0,index));
        NoteColorManagement noteColorManagement = new NoteColorManagement();
        holder.imageView.setImageResource(colorIds[noteColorManagement.getColor(mFilePath.getName().substring(0,index) + ".ns")]);
        convertView.startAnimation(anim);

        return convertView;
    }

    public void refreshData(File[] noteList){
        this.noteList = noteList;
    }

    @Override
    public int getCount(){
        return noteList.length;
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
