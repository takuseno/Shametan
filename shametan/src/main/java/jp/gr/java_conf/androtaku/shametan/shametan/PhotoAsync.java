package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by takuma on 2014/05/19.
 */
public class PhotoAsync extends AsyncTask<String,Void,Bitmap> {

    ImageView imageView;
    GridAdapter adapter;
    Context context;
    String tag;

    public PhotoAsync(ImageView imageView,GridAdapter adapter,Context context){
        this.imageView = imageView;
        this.adapter = adapter;
        this.tag = imageView.getTag().toString();
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... params){
        /*ContentResolver cr = context.getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = cr.query(uri,null,null,null,null);
        long id;
        Bitmap bitmap = null;
        cursor.moveToFirst();
        for(int i = 0;i < cursor.getCount();++i) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            if(title.equals(params[0])) {
                id = cursor.getLong((cursor.getColumnIndexOrThrow("_id")));
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr,id, MediaStore.Images.Thumbnails.MICRO_KIND,null);
                break;
            }
        }*/

        Bitmap bitmap = compressImage(params[0]);

        Log.i("AsyncTask","doinbackground");
        return bitmap;
    }

    @Override
    protected  void onPostExecute(Bitmap result){
        if(tag.equals(imageView.getTag())) {
            if (result != null) {
                Log.i("AsyncTask", "onPostExecute");
                imageView.setImageBitmap(result);
                //imageView.setImageResource(R.drawable.ic_launcher);
            }
        }
       // adapter.notifyDataSetChanged();
    }

    public Bitmap compressImage(String imageName){
        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imageName, opt);

        int scaleW = opt.outWidth / 320;
        int scaleH = opt.outHeight / 240;

        opt.inSampleSize = Math.max(scaleW, scaleH);
        opt.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imageName, opt);

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        float scale = Math.min((float)320/w, (float)240/h);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        bmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);

        return bmp;
    }
}
