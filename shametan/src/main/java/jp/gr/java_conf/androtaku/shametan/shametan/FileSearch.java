package jp.gr.java_conf.androtaku.shametan.shametan;

import java.io.File;

/**
 * Created by takuma on 2014/05/17.
 */
public class FileSearch {

    File[] searchList;
    File[] fileList;

    int folderNumber;
    int fileNumber;

    public void searchFolder(File path,String extension,String orExtension){

        searchList = new File[1000];
        fileList = new File[5000];
        folderNumber = 0;
        fileNumber = 0;

        File[] content = path.listFiles();
        int length = content.length;

        for(int i = 0;i < length;++i){
            if(content[i].isDirectory()){
                searchList[folderNumber] = content[i];
                ++folderNumber;
            }
            else{
                if(recogFile(content[i],extension)){
                    fileList[fileNumber] = content[i];
                    ++fileNumber;
                }
                else if(recogFile(content[i],orExtension)){
                    fileList[fileNumber] = content[i];
                    ++fileNumber;
                }
            }
        }

        for(int i = 0;i < folderNumber;++i){
            File folder = searchList[i];
            File[] list = folder.listFiles();

            for(int j = 0;j < list.length;++j){
                if(list[j].isDirectory()){
                    searchList[folderNumber] = list[j];
                    ++folderNumber;
                }
                else{
                    if(recogFile(list[j],extension)){
                        fileList[fileNumber] = list[j];
                        ++fileNumber;
                    }
                    else if(recogFile(list[j],orExtension)){
                        fileList[fileNumber] = list[j];
                        ++fileNumber;
                    }
                }
            }

        }

    }

    public File[] existFolderCheck(File path,String extension,String orExtension){

        searchList = new File[1000];
        File[] tempList = new File[100];
        folderNumber = 0;
        int tempCount = 0;

        File[] content = path.listFiles();
        int length = content.length;

        for(int i = 0;i < length;++i){
            if(content[i].isDirectory()){
                searchList[folderNumber] = content[i];
                ++folderNumber;
            }
        }

        for(int i = 0;i < folderNumber;++i){
            File folder = searchList[i];
            File[] list = folder.listFiles();

            for(int j = 0;j < list.length;++j){
                if(list[j].isDirectory()){
                    if(!list[j].getName().startsWith("thumbnail") && !list[j].getName().startsWith(".")) {
                        searchList[folderNumber] = list[j];
                        ++folderNumber;
                    }
                }

                else if(list[j].getName().equals(".nomedia")){
                    break;
                }

                else{
                    if(recogFile(list[j],extension)){
                        tempList[tempCount] = folder;
                        tempCount++;
                        break;
                    }
                    else if(recogFile(list[j],orExtension)){
                        tempList[tempCount] = folder;
                        tempCount++;
                        break;
                    }
                }
            }
        }

        File[] existFolder = new File[tempCount];
        for(int i = 0;i < tempCount;++i){
            existFolder[i] = tempList[i];
        }

        return existFolder;
    }

    public File[] getFileList(){
        File[] list = new File[fileNumber];
        for(int i = 0;i < fileNumber;++i){
            list[i] = fileList[i];
        }

        if(fileNumber == 0){
            return null;
        }

        return list;
    }

    public File[] getFolderList(){
        File[] list = new File[folderNumber];
        for(int i = 0;i < folderNumber;++i){
            list[i] = searchList[i];
        }
        return list;
    }

    public boolean recogFile(File path,String extension){
        if(path.getName().endsWith(extension)){
            return true;
        }
        else{
            return false;
        }
    }
}
