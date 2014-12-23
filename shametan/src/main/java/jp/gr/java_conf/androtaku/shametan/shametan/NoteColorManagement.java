package jp.gr.java_conf.androtaku.shametan.shametan;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by takuma on 2014/06/27.
 */
public class NoteColorManagement {
    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");

    private static final int BLUE = 1;
    private static final int GREEN = 2;
    private static final int PINK = 3;
    private static final int YELLOW = 4;
    private static final int VIOLET = 5;
    private static final int ORANGE = 6;

    public NoteColorManagement(){

    }

    public void makeNCFile(String fileName,int colorId){
        File nsFile = new File(basePath + "/" + fileName + ".ns");
        try{
            FileOutputStream fos = new FileOutputStream(nsFile);
            String output = String.valueOf(colorId);
            fos.write(output.getBytes());
            fos.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public int getColor(String fileName){
        File nsFile = new File(basePath + "/" + fileName);
        if(nsFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(nsFile);
                byte[] readBytes = new byte[fis.available()];
                fis.read(readBytes);
                String readString = new String(readBytes);
                int colorId = Integer.valueOf(readString);
                return colorId;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }
        else{
            return 0;
        }
    }
}
