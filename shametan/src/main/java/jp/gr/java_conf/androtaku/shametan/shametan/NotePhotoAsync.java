package jp.gr.java_conf.androtaku.shametan.shametan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by takuma on 2014/05/31.
 */
public class NotePhotoAsync extends AsyncTask<String,Void,Bitmap> {

    ImageView imageView;
    NoteGridAdapter adapter;
    String tag;
    int width;

    public NotePhotoAsync(ImageView imageView,NoteGridAdapter adapter,int width){
        this.imageView = imageView;
        this.adapter = adapter;
        this.tag = imageView.getTag().toString();
        this.width = width;
    }

    @Override
    protected Bitmap doInBackground(String... params){

        Bitmap bitmap = compressImage(params[0]);

        return bitmap;
    }

    @Override
    protected  void onPostExecute(Bitmap result){
        if(tag.equals(imageView.getTag())) {
            if (result != null) {
                imageView.setImageBitmap(result);
                Log.i("onpostexecute", "done");
            }
        }
    }

    public Bitmap compressImage(String imageName){
        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imageName, opt);

        int scaleW = opt.outWidth / (width/2);
        int scaleH = opt.outHeight / (width/2);

        opt.inSampleSize = Math.max(scaleW, scaleH);
        opt.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imageName, opt);

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        float scale = Math.min((float)(width/2)/w, (float)(width/2)/h);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        bmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);

        return bmp;
    }
}
