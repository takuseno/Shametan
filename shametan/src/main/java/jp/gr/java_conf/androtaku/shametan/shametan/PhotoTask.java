package jp.gr.java_conf.androtaku.shametan.shametan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by takuma on 2014/05/18.
 */
public class PhotoTask extends AsyncTask<String,Void,Bitmap> {
    private ImageView imageView;
    private GridAdapter adapter;
    private byte[] data;

    public PhotoTask(ImageView view,GridAdapter adapter){
        imageView = view;
        this.adapter = adapter;
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

    @Override
    protected Bitmap doInBackground(String... params){
        return compressImage(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap result){
        imageView.setImageBitmap(result);
        adapter.notifyDataSetChanged();
    }
}
