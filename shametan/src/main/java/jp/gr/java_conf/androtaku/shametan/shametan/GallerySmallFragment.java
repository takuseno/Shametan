package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.io.File;
import java.io.StringBufferInputStream;

/**
 * Created by takuma on 2014/05/18.
 */
public class GallerySmallFragment extends Fragment {
    GridView gridVIew;
    FileSearch fileSearch = new FileSearch();
    File[] imageFiles;

    public GallerySmallFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.gallery_samll_layout,container,false);
        init(rootView);

        return rootView;
    }

    public void init(View v){
        gridVIew = (GridView)v.findViewById(R.id.gridView);
        gridVIew.setNumColumns(3);
        fileSearch.searchFolder(new File(getArguments().getString("selected_directory_path")),".jpg",".JPG");
        ArrangeImages arrangeImages = new ArrangeImages();
        imageFiles = fileSearch.getFileList();
        GridAdapter adapter = new GridAdapter(getActivity(),R.layout.grid_items,imageFiles);
        gridVIew.setAdapter(adapter);
        gridVIew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toTrim(imageFiles[position].getPath());
            }
        });
    }

    public void toTrim(String imagePath){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("image_path",imagePath);
        bundle.putString("from","gallery");
        TrimFragment fragment = new TrimFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment,"trim_fragment");
        transaction.addToBackStack("gallery_small");
        transaction.commit();
    }
}
