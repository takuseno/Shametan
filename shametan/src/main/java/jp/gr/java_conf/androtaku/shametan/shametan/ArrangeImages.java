package jp.gr.java_conf.androtaku.shametan.shametan;

import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by takuma on 2014/05/19.
 */
public class ArrangeImages {
    Comparator comparator=new Comparator(){
        public int compare(Object o1,Object o2){
            File f1=(File)o1;
            File f2=(File)o2;

            return (int)(f1.lastModified()-f2.lastModified());
        }
    };

    private File[] dir;
    public ArrangeImages(File[] dir){
        this.dir=dir;
    }
    public File[] sort(){

        ArrayList list=new ArrayList();
        for(int i=0; i<dir.length; i++){
            list.add(dir[i]);
        }


        Collections.sort(list, this.comparator);

        File[] output = new File[list.size()];

        for(int i=0; i<list.size(); i++){
            File f=(File)list.get(i);
        }

        return output;
    }

}
