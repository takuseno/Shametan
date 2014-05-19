package jp.gr.java_conf.androtaku.shametan.shametan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by takuma on 2014/05/19.
 */
public class PhotoAsync extends AsyncTask<String,Void,Bitmap> {

    ImageView imageView;
    GridAdapter adapter;
    String tag;

    public PhotoAsync(ImageView imageView,GridAdapter adapter){
        this.imageView = imageView;
        this.adapter = adapter;
        this.tag = imageView.getTag().toString();
    }

    @Override
    protected Bitmap doInBackground(String... params){
        Bitmap bitmap;
        bitmap = compressImage(params[0]);
//        imageView.setImageBitmap(bitmap);
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
