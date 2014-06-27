package jp.gr.java_conf.androtaku.shametan.shametan;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by takuma on 2014/06/24.
 */
public class NotePagerFragment extends Fragment {
    private ViewPager viewPager;

    public NotePagerFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.viewpager_layout,container,false);
        init(rootView);

        NotebookActivity.menuType = NotebookActivity.MENU_NOTE;
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        viewPager.getAdapter().notifyDataSetChanged();
    }

    public void init(View v){
        viewPager = (ViewPager)v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new NoteFragmentPagerAdapter(getChildFragmentManager(),
                getArguments().getString("cst_path")));
        if(Build.VERSION.SDK_INT > 12) {
            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        }
        viewPager.setCurrentItem(getArguments().getInt("position"));
    }

}
