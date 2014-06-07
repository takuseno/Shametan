package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by takuma on 2014/05/30.
 */
public class NotebookActivity extends Activity{

    private static final File basePath = new File(Environment.getExternalStorageDirectory().getPath() + "/Shametan/");

    public static int menuType = 1;
    public static final int MENU_SELECT_NOTE = 1;
    public static final int MENU_NOTE = 2;
    public static final int MENU_DRAWLINE = 3;
    public static final int MENU_SELECT_PAGE = 4;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!basePath.exists()){
            if(!basePath.mkdir()){
                Toast.makeText(this, "failed to make a root folder", Toast.LENGTH_SHORT).show();
            }
            File noMedia = new File(basePath + "/.nomedia");
            try {
                if(!noMedia.createNewFile()){
                    Toast.makeText(this,"failed to make .nomedia",Toast.LENGTH_SHORT).show();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        File rootCST = new File(basePath + "/root.cst");
        if(!rootCST.exists()){
            try{
                FileOutputStream fos = new FileOutputStream(rootCST);
                String output = "0,";
                fos.write(output.getBytes());
                fos.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString("cst_file","/root.cst");
            SelectNoteFragment fragment = new SelectNoteFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        switch(menuType) {
            case MENU_SELECT_NOTE:
                getMenuInflater().inflate(R.menu.select_note_menu, menu);
                break;

            case MENU_NOTE:
                getMenuInflater().inflate(R.menu.viewnote_menu,menu);
                break;

            case MENU_DRAWLINE:
                getMenuInflater().inflate(R.menu.drawline_menu,menu);
                break;

            case  MENU_SELECT_PAGE:
                getMenuInflater().inflate(R.menu.select_page_menu,menu);
                break;

            default:
        }
        return super.onCreateOptionsMenu(menu);
    }

}
