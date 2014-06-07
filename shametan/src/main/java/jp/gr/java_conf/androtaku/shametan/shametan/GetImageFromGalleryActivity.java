package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by takuma on 2014/05/24.
 */
public class GetImageFromGalleryActivity extends Activity {

    public static int menuType = 1;
    public static final int MENU_MAIN = 1;
    public static final int MENU_DRAWLINE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
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

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(data == null){
            finish();
        }

        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn,null,null,null);
            cursor.moveToFirst();

            int index = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(index);
            cursor.close();

            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("cst_file",getIntent().getStringExtra("cst_file"));
            bundle.putString("image_path",path);
            bundle.putString("from","gallery");
            TrimFragment fragment = new TrimFragment();
            fragment.setArguments(bundle);
            transaction.replace(R.id.container,fragment,"trim_fragment");
            transaction.addToBackStack("gallery_small");
            transaction.commit();
        }
    }
}
