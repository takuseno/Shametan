package jp.gr.java_conf.androtaku.shametan.shametan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by takuma on 2014/06/03.
 */
public class CSTFileController {

    File path;
    File[] files;
    File[] noteFiles;
    File[] pageFiles;
    int numFiles;
    int numNote = 0;
    int numPage = 0;
    boolean initialized = false;

    String readString;

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
                readString = new String(readBytes);
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

        numNote = 0;
        numPage = 0;

        files = new File[numFiles];
        for(int i = 0;i < numFiles;++i){
            String temp = restString;
            index = temp.indexOf(",");
            files[i] = new File(temp.substring(0,index));
            restString = temp.substring(index + 1);
            if(files[i].getName().contains(".cst")){
                ++numNote;
            }
            else{
                ++numPage;
            }
        }
    }

    public File[] getFiles(){
        return files;
    }

    public File[] getNoteFiles(){
        int counter = 0;
        noteFiles = new File[numNote];
        for(int i = 0;i < numFiles;++i){
            if(files[i].getName().contains(".cst")){
                noteFiles[counter] = files[i];
                ++counter;
            }
        }

        return noteFiles;
    }

    public File[] getPageFiles(){
        int counter = 0;
        pageFiles = new File[numPage];
        for(int i = 0;i < numFiles;++i){
            if(!files[i].getName().contains(".cst")){
                pageFiles[counter] = files[i];
                ++counter;
            }
        }

        return pageFiles;
    }

    public void deleteItem(File file){
        if(file.getName().contains(".cst")){
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] readBytes = new byte[fis.available()];
                fis.read(readBytes);
                String readDeleteString = new String(readBytes);
                int index = readDeleteString.indexOf(",");
                int numDelete = Integer.valueOf(readDeleteString.substring(0, index));
                String restString = readDeleteString.substring(index + 1);

                for (int i = 0; i < numDelete; ++i) {
                    String temp = restString;
                    index = temp.indexOf(",");
                    File deleteFile = new File(temp.substring(0, index));
                    int tempIndex = deleteFile.getPath().indexOf(".jpg");
                    File deleteSTFile = new File(deleteFile.getPath().substring(0,tempIndex) + ".st");
                    deleteFile.delete();
                    deleteSTFile.delete();
                    restString = temp.substring(index + 1);
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }

        else{
            int index = file.getPath().indexOf(".jpg");
            File deleteSTFile = new File(file.getPath().substring(0,index) + ".st");
            deleteSTFile.delete();
        }

        int index = readString.indexOf(file.getPath());
        String foreString = readString.substring(readString.indexOf(",") + 1, index);
        String backString = readString.substring(index + file.getPath().length() + 1);
        try {
            FileOutputStream fos = new FileOutputStream(path);
            String output = String.valueOf(files.length - 1) + "," + foreString + backString;
            fos.write(output.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        file.delete();
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

    public void makeCST(File file){
        try{
            FileOutputStream fos = new FileOutputStream(file);
            String output = "0,";
            fos.write(output.getBytes());
            fos.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean isCSTFile(File path) {
        return path.getName().contains(".cst");
    }

    public boolean idCSTExists(){
        return path.exists();
    }
}
