package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;

/**
 * Created by takuma on 2014/05/30.
 */
public class TempSelectNoteFragment extends Fragment {
    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");
    ListView listView;

    public static TempSelectNoteFragment newInstance(){
        TempSelectNoteFragment fragment = new TempSelectNoteFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.tempnoteselect,container,false);
        init(rootView);
        return rootView;
    }

    public void init(View v){
        listView = (ListView)v.findViewById(R.id.noteList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1);
        FileSearch fileSearch = new FileSearch();
        fileSearch.searchFolder(basePath,".txt","aaa","aaa","aaa");
        final File[] list = fileSearch.getFileList();
        for(int i = 0;i < list.length;++i){
            adapter.add(list[i].getName());
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toNote(list[position].getPath());
            }
        });
        listView.setAdapter(adapter);
    }

    public void toNote(String filePath){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("file_path",filePath);
        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment,"note_fragment");
        transaction.addToBackStack("notelist");
        transaction.commit();
    }
}
