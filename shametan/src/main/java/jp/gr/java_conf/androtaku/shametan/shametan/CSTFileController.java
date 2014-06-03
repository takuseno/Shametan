package jp.gr.java_conf.androtaku.shametan.shametan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by takuma on 2014/06/03.
 */
public class CSTFileController {

    File path;
    File[] files;
    int numFiles;
    boolean initialized = false;

    public CSTFileController(String path){
        this.path = new File(path);
        if(this.path.exists()){
           initialized = true;
        }
    }

    public void importCSTFile(){
        if(initialized) {
            try {
                FileInputStream fis = new FileInputStream(path.getPath());
                byte[] readBytes = new byte[fis.available()];
                fis.read(readBytes);
                String readString = new String(readBytes);
                analyzeSTFile(readString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void analyzeSTFile(String data){
        int index = data.indexOf(",");
        numFiles = Integer.valueOf(data.substring(0,index));
        String restString = data.substring(index + 1);

        files = new File[numFiles];
        for(int i = 0;i < numFiles;++i){
            String temp = restString;
            index = temp.indexOf(",");
            files[i] = new File(temp.substring(0,index));
            restString = temp.substring(index + 1);
        }
    }

    public File[] getFiles(){
        return files;
    }

    public void saveCSTFile(File file){
        if(initialized){
            importCSTFile();
            boolean saved = false;
            for(int i = 0;i < files.length;++i){
                if(file.getName().equals(files[i].getName())){
                    saved = true;
                    break;
                }
            }
            if(!saved) {
                path.delete();
                try {
                    FileOutputStream fos = new FileOutputStream(path);
                    String output = String.valueOf(files.length + 1) + ",";
                    for (int i = 0; i < files.length; ++i) {
                        output += String.valueOf(files[i].getPath()) + ",";
                    }
                    output += String.valueOf(file.getPath()) + ",";
                    fos.write(output.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            try{
                FileOutputStream fos = new FileOutputStream(path);
                String output = "1,";
                output += String.valueOf(file.getPath()) + ",";
                fos.write(output.getBytes());
                fos.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }

    }
}
