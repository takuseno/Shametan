package jp.gr.java_conf.androtaku.shametan.shametan;

import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by takuma on 2014/05/19.
 */
public class ArrangeImages {
    public File[] arrangeImages(File[] images){
        int length = images.length;
        File[] arranged = new File[length];
        ExifInterface ei;
        for(int i = 0;i < length;++i){
            long date = images[i].lastModified();
            int count = 0;
            for(int j = 0;j < length;++j){
                if(i != j){
                    long cpDate = images[j].lastModified();
                    if(date < cpDate){
                        ++count;
                    }
                }
            }
            arranged[count] = images[i];
        }
        return arranged;
    }

    public long toLong(String value){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
        Date date;
        try {
            date = dateFormat.parse(value);
            return date.getTime();
        }catch(ParseException e){
            return 0;
        }

    }
}
