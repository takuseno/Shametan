package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

/**
 * Created by takuma on 2014/05/24.
 */
public class GetImageFromCameraActivity extends Activity {

    public static int menuType = 1;
    public static final int MENU_MAIN = 1;
    public static final int MENU_DRAWLINE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, CameraFragment.newInstance())
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

            case MENU_DRAWLINE:
                getMenuInflater().inflate(R.menu.drawline_menu,menu);
                break;

            default:
        }
        return super.onCreateOptionsMenu(menu);
    }
}
