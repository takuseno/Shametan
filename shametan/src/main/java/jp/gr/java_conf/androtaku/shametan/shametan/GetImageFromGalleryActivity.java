package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.res.Configuration;
import android.graphics.Color;
import android.media.ExifInterface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import java.io.IOException;

/**
 * Created by takuma on 2014/05/24.
 */
public class GetImageFromGalleryActivity extends ActionBarActivity {

    public static int menuType = 1;
    public static final int MENU_MAIN = 1;
    public static final int MENU_DRAWLINE = 2;
    public static final int MENU_TRIM = 3;

    private static final int ORIEN_VERTICAL = 1;
    private static final int ORIEN_HORIZON = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar_drawline);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,0x1111);
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

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(data == null){
            finish();
        }
        if(requestCode == 0x1111 && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn,null,null,null);
            cursor.moveToFirst();

            int index = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(index);
            cursor.close();

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("cst_path",getIntent().getStringExtra("cst_path"));
            bundle.putString("image_path",imagePath);
            bundle.putString("from","gallery");
            try {
                ExifInterface exifInterface = new ExifInterface(imagePath);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_UNDEFINED:
                        bundle.putInt("orientation", ORIEN_HORIZON);
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                        bundle.putInt("orientation", ORIEN_HORIZON);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bundle.putInt("orientation", ORIEN_VERTICAL);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bundle.putInt("orientation", ORIEN_HORIZON);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bundle.putInt("orientation", ORIEN_VERTICAL);
                        break;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            TrimFragment fragment = new TrimFragment();
            fragment.setArguments(bundle);
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack("gallery_small");
            transaction.commit();
        }
    }
}
