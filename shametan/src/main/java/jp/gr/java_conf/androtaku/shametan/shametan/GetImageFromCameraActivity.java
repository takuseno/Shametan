package jp.gr.java_conf.androtaku.shametan.shametan;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

/**
 * Created by takuma on 2014/05/24.
 */
public class GetImageFromCameraActivity extends ActionBarActivity {

    public static int menuType = 1;
    public static final int MENU_MAIN = 1;
    public static final int MENU_DRAWLINE = 2;
    public static final int MENU_TRIM = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar_drawline);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString("cst_path",getIntent().getStringExtra("cst_path"));
            CameraFragment fragment = new CameraFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
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

            case MENU_TRIM:
                getMenuInflater().inflate(R.menu.trim_menu,menu);
                break;

            default:
        }
        return super.onCreateOptionsMenu(menu);
    }
}
