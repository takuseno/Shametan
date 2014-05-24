package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.io.File;
import java.io.StringBufferInputStream;

/**
 * Created by takuma on 2014/05/18.
 */
public class GallerySmallFragment extends Fragment {
    GridView gridView;
    FileSearch fileSearch = new FileSearch();
    File[] imageFiles;

    int firstVisibleId = 100;

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
        gridView = (GridView)v.findViewById(R.id.gridView);
        gridView.setNumColumns(3);
        fileSearch.searchFolder(new File(getArguments().getString("selected_directory_path")),".jpg",".JPG",".png",".PNG");
        //ArrangeImages arrangeImages = new ArrangeImages(fileSearch.getFileList());
        imageFiles = fileSearch.getFileList();
        final GridAdapter adapter = new GridAdapter(getActivity(),R.layout.grid_items,imageFiles);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toTrim(imageFiles[position].getPath());
            }
        });
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch(scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        adapter.scrolled = false;
                        for(int i = 0;i < adapter.diplayedId.length;++i){
                            if(!adapter.showed[adapter.diplayedId[i]]){
                                Log.i("getadapter",String.valueOf(adapter.diplayedId[i]));
                                View targetView = gridView.getChildAt(adapter.diplayedId[i]);
                                gridView.getAdapter().getView(adapter.diplayedId[i],targetView,gridView);
                            }
                        }
                        break;

                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        adapter.scrolled = true;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem != firstVisibleId) {
                    for (int i = 0; i < adapter.diplayedId.length; ++i) {
                        firstVisibleId = firstVisibleItem;
                        adapter.diplayedId[i] = firstVisibleItem + i;
                    }
                }
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
