package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;

/**
 * Created by takuma on 2014/06/07.
 */
public class SelectPageFragment extends Fragment {

    TextView noPageText;
    GridView gridPageView;
    FloatingActionButton addFromCameraButton,addFromCollectionsButton;
    CSTFileController cstFileController;

    File[] pageFiles;
    PageGridAdapter pageAdapter;

    int selectedPosition;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.page_select_layout,container,false);
        init(rootView);
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.pages));
        actionBar.setDisplayHomeAsUpEnabled(true);
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
                selectedPosition = position;
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

        addFromCameraButton = (FloatingActionButton)v.findViewById(R.id.add_from_camera);
        addFromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity().getApplicationContext(),GetImageFromCameraActivity.class);
                intent.putExtra("cst_path",getArguments().getString("cst_path"));
                getActivity().startActivity(intent);
            }
        });

        addFromCollectionsButton = (FloatingActionButton)v.findViewById(R.id.add_from_collections);
        addFromCollectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity().getApplicationContext(),GetImageFromGalleryActivity.class);
                intent.putExtra("cst_path",getArguments().getString("cst_path"));
                getActivity().startActivity(intent);
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
                .setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.chosendelete))
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cstFileController.deleteItem(deleteItem);
                        refreshPageAdapter();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        Intent intent;

        switch(menuItem.getItemId()){
            case R.id.add_from_camera:
                intent = new Intent(getActivity().getApplicationContext(),GetImageFromCameraActivity.class);
                intent.putExtra("cst_path",getArguments().getString("cst_path"));
                getActivity().startActivity(intent);
                break;

            case R.id.add_from_gallery:
                intent = new Intent(getActivity().getApplicationContext(),GetImageFromGalleryActivity.class);
                intent.putExtra("cst_path",getArguments().getString("cst_path"));
                getActivity().startActivity(intent);
                break;

            case android.R.id.home:
                getFragmentManager().popBackStack();
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
        bundle.putString("cst_path",getArguments().getString("cst_path"));
        bundle.putInt("position",selectedPosition);
        NotePagerFragment fragment = new NotePagerFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack("pagelist");
        transaction.commit();
    }
}
