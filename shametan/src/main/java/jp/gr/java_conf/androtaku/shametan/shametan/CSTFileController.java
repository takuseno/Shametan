package jp.gr.java_conf.androtaku.shametan.shametan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by takuma on 2014/06/03.
 */
public class CSTFileController {

    //declare cst file path
    private File path;
    //declare all files
    private File[] files;
    private File[] noteFiles;
    private File[] pageFiles;
    //declare the number of all files
    private int numFiles = 0;
    //declare the number of note files
    private int numNote = 0;
    //declare the number of page files
    private int numPage = 0;
    //declare String of read data
    private String readString;

    //constructor
    public CSTFileController(String path){
        //get cst file path
        this.path = new File(path);
    }

    public void importCSTFile(){
        try {
            FileInputStream fis = new FileInputStream(path.getPath());
            byte[] readBytes = new byte[fis.available()];
            fis.read(readBytes);
            //get read data
            readString = new String(readBytes);
            //analyze cst file
            analyzeCSTFile(readString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        header:the number of cst files
        content:files contained by cst file devided by ','

        ex.
           2,root/image01.jpg,root/image02.jpg
     */


    //function of analyzing cst file
    public void analyzeCSTFile(String data){
        int index = data.indexOf(",");
        //get the number of all files
        numFiles = Integer.valueOf(data.substring(0,index));
        String restString = data.substring(index + 1);

        numNote = 0;
        numPage = 0;

        files = new File[numFiles];
        File[] tempNoteFiles = new File[numFiles];
        File[] tempPageFiles = new File[numFiles];
        for(int i = 0;i < numFiles;++i){
            //declare String of the rest data
            String temp = restString;
            //get index of next ','
            index = temp.indexOf(",");
            //get file path
            files[i] = new File(temp.substring(0,index));
            //move to next section
            restString = temp.substring(index + 1);
            //checking whether cst file or not
            if(files[i].getName().contains(".cst")){
                tempNoteFiles[numNote] = files[i];
                ++numNote;
            }
            else{
                tempPageFiles[numPage] = files[i];
                ++numPage;
            }
        }

        noteFiles = new File[numNote];
        System.arraycopy(tempNoteFiles,0,noteFiles,0,numNote);
        pageFiles = new File[numPage];
        System.arraycopy(tempPageFiles,0,pageFiles,0,numPage);
    }

    //function of getting all files paths
    public File[] getFiles(){
        return files;
    }

    //function of getting note files paths
    public File[] getNoteFiles(){
        return noteFiles;
    }

    //function of getting page files paths
    public File[] getPageFiles(){
        return pageFiles;
    }

    //function of deleting file in cst file
    public void deleteItem(File file){
        //pattern of deleting cst file
        if(file.getName().contains(".cst")){
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] readBytes = new byte[fis.available()];
                fis.read(readBytes);
                String readDeleteString = new String(readBytes);
                int index = readDeleteString.indexOf(",");
                int numDelete = Integer.valueOf(readDeleteString.substring(0, index));
                String restString = readDeleteString.substring(index + 1);

                //deleting all files in deleted cst file
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

        //pattern of deleting image file
        else{
            int index = file.getPath().indexOf(".jpg");
            //deleting st file
            File deleteSTFile = new File(file.getPath().substring(0,index) + ".st");
            deleteSTFile.delete();
        }

        //revise loaded cst file
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

    //making cst file
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
}
