package jp.gr.java_conf.androtaku.shametan.shametan;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by takuma on 2014/05/16.
 */
public class GalleryBigFragment extends Fragment{

    ListView listView;

    FileSearch fileSearch = new FileSearch();

    File[] externalImageDirectories;
    File[] internalImageDirectories;
    File[] imageDirectories;

    public static GalleryBigFragment newInstance(){
        GalleryBigFragment fragment = new GalleryBigFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.gallery_big_layout,container,false);
        init(rootView);
        return rootView;
    }

    public void init(View v){
        listView = (ListView)v.findViewById(R.id.bigGallery);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toGridList(imageDirectories[position].getPath());
            }
        });
        setAdapter();
    }

    public void setAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1);
        imageDirectories = getImageDirectories();
        for(int i = 0;i < imageDirectories.length;++i){
            adapter.add(imageDirectories[i].getName());
        }
        listView.setAdapter(adapter);
    }

    public File[] getImageDirectories(){
        File[] directories;

        int numExDirectories = 0;
        if(getMount_sd()!=null) {
            File externalSD = new File(getMount_sd());
            if(isMounted(externalSD.getPath())) {
                externalImageDirectories = fileSearch.existFolderCheck(externalSD, ".jpg", ".JPG");
                numExDirectories = externalImageDirectories.length;
            }
        }

        File internalSD = Environment.getExternalStorageDirectory();
        int numInDirectories;
        internalImageDirectories = fileSearch.existFolderCheck(internalSD, ".jpg",".JPG");
        numInDirectories = internalImageDirectories.length;

        int numDirectories = numExDirectories + numInDirectories;
        directories = new File[numDirectories];
        for(int i = 0;i < numExDirectories;++i){
          directories[i] = externalImageDirectories[i];
        }
        for(int i = 0;i < numInDirectories;++i){
            directories[i + numExDirectories] = internalImageDirectories[i];
        }

        return directories;
    }

    // SDカードのマウント先をゲットするメソッド
    private String getMount_sd() {
        List<String> mountList = new ArrayList<String>();
        String mount_sdcard = null;

        Scanner scanner = null;
        try {
            // システム設定ファイルにアクセス
            File vold_fstab = new File("/system/etc/vold.fstab");
            if(!vold_fstab.exists()){
                return null;
            }
            scanner = new Scanner(new FileInputStream(vold_fstab));
            // 一行ずつ読み込む
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // dev_mountまたはfuse_mountで始まる行の
                if (line.startsWith("dev_mount") || line.startsWith("fuse_mount")) {
                    // 半角スペースではなくタブで区切られている機種もあるらしいので修正して
                    // 半角スペース区切り３つめ（path）を取得
                    String path = line.replaceAll("\t", " ").split(" ")[2];
                    // 取得したpathを重複しないようにリストに登録
                    if (!mountList.contains(path)){
                        mountList.add(path);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        // Environment.isExternalStorageRemovable()はGINGERBREAD以降しか使えない
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            // getExternalStorageDirectory()が罠であれば、そのpathをリストから除外
            if (!Environment.isExternalStorageRemovable()) {   // 注1
                mountList.remove(Environment.getExternalStorageDirectory().getPath());
            }
        }

        // マウントされていないpathは除外
        for (int i = 0; i < mountList.size(); i++) {
            if (!isMounted(mountList.get(i))){
                mountList.remove(i--);
            }
        }

        // 除外されずに残ったものがSDカードのマウント先
        if(mountList.size() > 0){
            mount_sdcard = mountList.get(0);
        }

        // マウント先をreturn（全て除外された場合はnullをreturn）
        return mount_sdcard;
    }

    // 引数に渡したpathがマウントされているかどうかチェックするメソッド
    public boolean isMounted(String path) {
        boolean isMounted = false;

        Scanner scanner = null;
        try {
            // マウントポイントを取得する
            File mounts = new File("/proc/mounts");   // 注2
            scanner = new Scanner(new FileInputStream(mounts));
            // マウントポイントに該当するパスがあるかチェックする
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().contains(path)) {
                    // 該当するパスがあればマウントされているってこと
                    isMounted = true;
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        // マウント状態をreturn
        return isMounted;
    }

    public void toGridList(String path){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("selected_directory_path",path);
        GallerySmallFragment fragment = new GallerySmallFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment,"gallery_fragment");
        transaction.addToBackStack("gallerybig");
        transaction.commit();
    }
}
