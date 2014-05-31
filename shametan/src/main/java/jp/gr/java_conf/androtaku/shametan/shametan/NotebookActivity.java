package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

/**
 * Created by takuma on 2014/05/30.
 */
public class NotebookActivity extends Activity{

    public static int menuType = 1;
    public static final int MENU_MAIN = 1;
    public static final int MENU_NOTE = 2;
    public static final int MENU_DRAWLINE = 3;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, TempSelectNoteFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        switch(menuType) {
            case MENU_MAIN:
                getMenuInflater().inflate(R.menu.main, menu);
                break;

            case MENU_NOTE:
                getMenuInflater().inflate(R.menu.viewnote_menu,menu);
                break;

            case MENU_DRAWLINE:
                getMenuInflater().inflate(R.menu.drawline_menu,menu);
                break;

            default:
        }
        return super.onCreateOptionsMenu(menu);
    }

}
