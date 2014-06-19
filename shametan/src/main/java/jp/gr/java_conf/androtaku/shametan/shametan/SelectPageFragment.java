package jp.gr.java_conf.androtaku.shametan.shametan;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by takuma on 2014/06/07.
 */
public class SelectPageFragment extends Fragment {

    TextView noPageText;
    GridView gridPageView;
    CSTFileController cstFileController;

    File[] pageFiles;
    PageGridAdapter pageAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.page_select_layout,container,false);
        init(rootView);

        NotebookActivity.menuType = NotebookActivity.MENU_SELECT_PAGE;
        setHasOptionsMenu(true);
        return rootView;
    }

    public void init(View v){
        cstFileController = new CSTFileController(getArguments().getString("cst_path"));
        cstFileController.importCSTFile();
        pageFiles = cstFileController.getPageFiles();
        gridPageView = (GridView)v.findViewById(R.id.pageList);
        gridPageView.setNumColumns(2);
        pageAdapter = new PageGridAdapter(getActivity(),R.layout.grid_items,pageFiles);
        gridPageView.setAdapter(pageAdapter);
        gridPageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = pageFiles[position].getPath().lastIndexOf(".");
                String dataPath = pageFiles[position].getPath().substring(0, index) + ".st";
                toNote(dataPath);
            }
        });
        gridPageView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(pageFiles[position]);
                return true;
            }
        });

        noPageText = (TextView)v.findViewById(R.id.no_pages_text);
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshPageAdapter();
    }

    @Override
    public void onDestroy(){
        NotebookActivity.menuType = NotebookActivity.MENU_SELECT_NOTE;
        super.onDestroy();
    }

    public void refreshPageAdapter(){
        cstFileController.importCSTFile();
        pageFiles = cstFileController.getPageFiles();
        pageAdapter.refreshData(pageFiles);
        pageAdapter.notifyDataSetChanged();
        if(pageFiles.length == 0){
            noPageText.setVisibility(View.VISIBLE);
            gridPageView.setVisibility(View.INVISIBLE);
        }
        else{
            noPageText.setVisibility(View.INVISIBLE);
            gridPageView.setVisibility(View.VISIBLE);
        }
    }

    public void showDeleteDialog(File file){
        final File deleteItem = file;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("削除")
                .setMessage("選択したものを削除しますか？")
                .setPositiveButton("削除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cstFileController.deleteItem(deleteItem);
                        refreshPageAdapter();
                    }
                })
                .setNegativeButton("キャンセル", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        Intent intent;

        switch(menuItem.getItemId()){
            case R.id.add_from_camera:
                intent = new Intent(getActivity().getApplicationContext(),GetImageFromCameraActivity.class);
                intent.putExtra("cst_file",getArguments().getString("cst_path"));
                getActivity().startActivity(intent);
                break;

            case R.id.add_from_gallery:
                intent = new Intent(getActivity().getApplicationContext(),GetImageFromGalleryActivity.class);
                intent.putExtra("cst_file",getArguments().getString("cst_path"));
                getActivity().startActivity(intent);
                break;

            default:
        }
        return true;

    }

    public void toNote(String filePath){
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("file_path",filePath);
        bundle.putString("cst_file",getArguments().getString("cst_path"));
        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack("pagelist");
        transaction.commit();
    }
}
