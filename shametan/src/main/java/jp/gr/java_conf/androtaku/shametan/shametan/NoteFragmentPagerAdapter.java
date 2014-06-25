package jp.gr.java_conf.androtaku.shametan.shametan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.io.File;

/**
 * Created by takuma on 2014/06/24.
 */
public class NoteFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private File[] files;
    private String cstPath;

    public NoteFragmentPagerAdapter(FragmentManager fragmentManager,String cstPath){
        super(fragmentManager);
        this.cstPath = cstPath;
        CSTFileController cstFileController = new CSTFileController(cstPath);
        cstFileController.importCSTFile();
        files = cstFileController.getPageFiles();
    }

    @Override
    public Fragment getItem(int i){
        NoteFragment fragment = new NoteFragment();
        Bundle bundle = new Bundle();
        bundle.putString("cst_path",cstPath);
        bundle.putString("file_path",files[i].getPath());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount(){
        return files.length;
    }
}
