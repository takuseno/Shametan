package jp.gr.java_conf.androtaku.shametan.shametan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * Created by takuma on 2014/05/05.
 */
public class TrimImageView extends ImageView implements OnTouchListener {

    //===============================================================
    // 定数定義
    //===============================================================

    /** 動作モード：未設定 */
    private static final int MODE_NONE = 0;

    /** 動作モード：移動 */
    private static final int MODE_DRAG = 1;

    /** 動作モード：ズーム */
    private static final int MODE_ZOOM = 2;

    /** Matrixオブジェクトが保有しているデータの数 */
    private static final int MATRIX_VALUES_NUM = 9;

    /** 5倍超えるとそろそろ見ずらくなるので、デフォルト値は5とする */
    private static final float DEFAULT_MAX_SCALE = 5.0f;

    /** 等倍スケール */
    private static final float DEFAULT_SCALE = 1.0f;

    //===============================================================
    // メンバー変数
    //===============================================================
    // 表示画像のファイルパス
    private String mFilePath = null;

    // 表示画像のビットマップオブジェクト
    private Bitmap mBitmap = null;

    // ドラッグ操作時の基準座標
    private PointF mMovePoint = new PointF();

    // ピンチ操作時のMatrixオブジェクト
    private Matrix mImageMatrix = new Matrix();

    // ピンチ操作時のMatrixオブジェクト（一時保存用）
    private Matrix mSavedImageMatrix = new Matrix();

    // ピンチ操作時 マルチタッチ位置の距離
    private float mSpan = 0.0f;

    // 画像の最小拡大率
    private float mInitialScale = 1.0f;

    // 画像の最大拡大率
    private float mMaxScale = DEFAULT_MAX_SCALE;

    // ピンチ操作時の基点座標
    private PointF mMidPoint = new PointF();

    // 動作モード
    private int mMode = MODE_NONE;

    Bitmap saveBitmap;

    Canvas canvas;
    Matrix saveMatrix;

    int fromFragment = 2;
    static final int FROM_CAMERA = 1;
    static final int FROM_GALLERY = 2;

    //===============================================================
    // Constructor
    //===============================================================
    public TrimImageView(Context context) {
        super(context);
        init(context);
    }
    public TrimImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public TrimImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /** 初期化メソッド */
    private void init(Context context) {
        // Touchリスナー登録
        super.setOnTouchListener(this);
    }

    //===============================================================


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean ret = true;

        int actionCode = event.getAction() & MotionEvent.ACTION_MASK;

        // ドラッグイベントチェック
        ret = onTouchDragEvent(event, actionCode);
        if(!ret) {
            // マルチタッチイベントチェック
            ret = onTouchPointerEvent(event, actionCode);
        }
        return ret;
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(super.getWidth() == 0) {
            // Viewの幅が0の場合（まだViewが形成されていない）
            return;
        }

        if(mBitmap != null) {
            // ビットマップを作成しなおす。
            // 現在のビットマップが使用しているメモリを解放
            mBitmap.recycle();
        }
        // View生成されたので、初期描画
        drawInitial();
    }



    @Override
    protected void onDraw(Canvas canvas) {

        Matrix matrix = super.getImageMatrix();
        float[] values = new float[MATRIX_VALUES_NUM];

        // ズームの場合は、まず画像をズームさせる
        if(mMode == MODE_ZOOM) {

            // 描画（拡縮画像）
            super.onDraw(canvas);
            // 描画後のImageMatrixを取得（表示位置補正に使用する為）
            matrix = super.getImageMatrix();

            // キャンバスをクリア(背景色で上書き)
            // Zoomの場合は、Zoom画像と位置補正の2回描画するので、
            // 1回目の描画を背景色で塗りつぶす
            // (そうしないと画面に残りっぱなしになる)。
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_OVER);
        }
        matrix.getValues(values);

        // 画像表示位置を補正する
        setCenteringY(mBitmap, values[Matrix.MSCALE_X], matrix);    // 縦方向センタリング
        chkXPosition(mBitmap, values[Matrix.MSCALE_X], matrix);     // 横方向余白チェック
        chkYPosition(mBitmap, values[Matrix.MSCALE_Y], matrix);     // 縦方向余白チェック

        // 補正値が設定されたMatrixオブジェクトをViewに設定
        super.setImageMatrix(matrix);

        // 描画開始
        super.onDraw(canvas);
    }


    /**
     * 画像ファイルパス設定
     * @param filePath
     */
    public void setImage(String filePath) {

        if(filePath == null) {
            return;
        }
        mFilePath = filePath;

        if(super.getWidth() == 0) {
            return;
        }
        // 設定されたイメージを描画
        drawInitial();
    }



    /**
     * 最大拡大率を設定する
     * @param scale
     */
    public void setMaxScale(float scale) {

        if(mInitialScale > scale) {
            return;
        }
        mMaxScale = scale;
    }



    /**
     * 動作モード設定
     * @param mode
     */
    private void setMode(int mode) {
        mMode = mode;
    }



    /**
     * 初期描画
     */
    private void drawInitial() {

        if(mBitmap != null) {
            // ビットマップを作成しなおす。
            // 現在のビットマップが使用しているメモリを解放
            mBitmap.recycle();
        }

        // ビットマップ作成
        mBitmap = getBitmap(mFilePath);

        // 描画
        super.setImageBitmap(mBitmap);

        // 描画位置センタリング
        float[] values = new float[MATRIX_VALUES_NUM];
        mImageMatrix.getValues(values);

        mInitialScale = getInitialScale(mBitmap);

        setCenteringY(mBitmap, mInitialScale, mImageMatrix);
        setValueToImageMatrix(Matrix.MTRANS_X, 0f, mImageMatrix);

        if(values[Matrix.MSCALE_X] == DEFAULT_SCALE) {
            // onLayoutは何度も呼ばれるので、
            // 拡縮率が1倍の時（初期ロード時）のみ初期Zoomを実行する
            mImageMatrix.postScale(mInitialScale, mInitialScale);
            mSavedImageMatrix.set(mImageMatrix);
        }
        // 描画処理起動
        super.setImageMatrix(mImageMatrix);
    }



    /**
     * 初期拡縮率を取得する
     * @param bitmap
     * @return
     */
    private float getInitialScale(Bitmap bitmap) {

        // Viewのサイズ
        float viewWidth = super.getWidth();
        float viewHeight = super.getHeight();

        // bitmapのサイズ
        float imageWidth = bitmap.getWidth();
        float imageHeight = bitmap.getHeight();

        // X軸、Y軸のサイズ比
        float scaleX = viewWidth / imageWidth;
        float scaleY = viewHeight / imageHeight;

        // 初期状態で画像の見切れをなくしたいので、
        // 小さいほうに合わせる
        return Math.min(scaleX, scaleY);
    }



    /**
     * ドラッグイベント処理
     * @param event
     * @param actionCode
     * @return
     */
    private boolean onTouchDragEvent(MotionEvent event, int actionCode) {
        boolean ret = false;

        switch(actionCode) {
            case MotionEvent.ACTION_DOWN:       // シングルタッチスタート
                actionDown(event);
                setMode(MODE_DRAG);             // ドラッグイベントモードへ
                ret = true;
                break;
            case MotionEvent.ACTION_MOVE:       // ドラッグ
                if(mMode == MODE_DRAG) {        // ドラッグイベントモードの場合の処理を実行
                    actionMove(event);
                    mMovePoint.set(event.getX(), event.getY());
                    ret = true;
                }
                break;
            case MotionEvent.ACTION_UP:         // タッチ終了
                setMode(MODE_NONE);             // ドラッグイベントモード終了
                mSavedImageMatrix.set(super.getImageMatrix());
                ret = true;
                break;
            default:
                break;
        }
        return ret;
    }



    /**
     * マルチタッチイベント（ピンチ操作）処理
     * @param event
     * @param actionCode
     * @return
     */
    private boolean onTouchPointerEvent(MotionEvent event, int actionCode) {
        boolean ret = false;

        switch(actionCode) {
            case MotionEvent.ACTION_POINTER_DOWN:   // マルチタッチスタート
                actionPointerDown(event);
                setMode(MODE_ZOOM);                 // ズームイベントモードへ
                ret = true;
                break;
            case MotionEvent.ACTION_MOVE:           // ピンチ操作
                if(mMode == MODE_ZOOM) {
                    ret = actionPointerMove(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:     // マルチタッチ終了
                setMode(MODE_NONE);                 // ズームイベントモード終了
                mSavedImageMatrix.set(super.getImageMatrix());
                ret = true;
                break;
            default:
                break;
        }
        return ret;
    }



    /**
     * Matrixオブジェクトに値を設定する
     * @param index
     * @param value
     * @param dst
     */
    private void setValueToImageMatrix(int index, float value, Matrix dst) {

        float[] values = new float[MATRIX_VALUES_NUM];
        dst.getValues(values);

        values[index] = value;
        dst.setValues(values);
    }



    /**
     * 縦方向センタリング
     * @param bitmap
     * @param scale
     * @param matrix
     */
    private void setCenteringY(Bitmap bitmap, float scale, Matrix matrix) {

        float viewHeight = (float)super.getHeight();        // Viewのサイズ

        float imageHeight = (float)bitmap.getHeight();
        imageHeight *= scale;                               // 画像サイズ

        float[] values = new float[MATRIX_VALUES_NUM];
        matrix.getValues(values);

        // 画像サイズがViewのサイズより小さい場合のみセンタリング
        float cal = viewHeight - imageHeight;
        if(cal > 0) {
            cal /= 2.0f;
            setValueToImageMatrix(Matrix.MTRANS_Y, cal, matrix);
        }
    }



    /**
     * X方向の余白チェック
     * @param bitmap
     * @param scale
     * @param matrix
     */
    private void chkXPosition(Bitmap bitmap, float scale, Matrix matrix) {

        float viewWidth = (float)super.getWidth();
        float imageWidth = (float)bitmap.getWidth();
        imageWidth *= scale;

        float[] values = new float[MATRIX_VALUES_NUM];
        matrix.getValues(values);

        float currentX = values[Matrix.MTRANS_X];

        if(currentX > 0) {
            // 画面左に余白あり
            setValueToImageMatrix(Matrix.MTRANS_X, 0f, matrix);
        }else if((imageWidth + currentX) < viewWidth){
            // 画面右に余白あり
            float cal = values[Matrix.MTRANS_X] + (viewWidth - (imageWidth + currentX));
            setValueToImageMatrix(Matrix.MTRANS_X, cal, matrix);
        }
    }



    /**
     * Y方向の余白チェック
     * @param bitmap
     * @param scale
     * @param matrix
     */
    private void chkYPosition(Bitmap bitmap, float scale, Matrix matrix) {

        float viewHeight = (float)super.getHeight();
        float imageHeight = (float)bitmap.getHeight();
        imageHeight *= scale;

        float[] values = new float[MATRIX_VALUES_NUM];
        matrix.getValues(values);

        float currentY = values[Matrix.MTRANS_Y];

        if(viewHeight > imageHeight) {
            return;
        }

        if(currentY > 0) {
            // 画面左に余白あり
            setValueToImageMatrix(Matrix.MTRANS_Y, 0f, matrix);
        }else if((imageHeight + currentY) < viewHeight){
            // 画面右に余白あり
            float cal = values[Matrix.MTRANS_Y] + (viewHeight - (imageHeight + currentY));
            setValueToImageMatrix(Matrix.MTRANS_Y, cal, matrix);
        }
    }



    /**
     * ファイルをビットマップに変換する
     * @param filePath
     * @return
     */
    private Bitmap getBitmap(String filePath) {

        if(mFilePath == null) {
            return null;
        }
        // Bitmap取得
        Bitmap bitmap = loadBitmap(mFilePath);
        return bitmap;
    }



    /**
     * ファイルをビットマップに変換する
     * @param filePath
     * @return
     */
    private Bitmap loadBitmap(String filePath) {

        int width = super.getWidth();
        int height = super.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        return loadImage(filePath, options, width, height);
    }



    /**
     * ビットマップをロードする
     * @param path
     * @param options
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    private Bitmap loadImage(String path, BitmapFactory.Options options, int maxWidth, int maxHeight) {

        Bitmap  bmpImage;

        // ビットマップデコード（生成後のビットマップ情報のみ取得）
        decode(options, path, true);

        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        // リサイズ必要かチェック
        if(chkSize(imageWidth, imageHeight, maxWidth, maxHeight)) {
            // リサイズする際の縮小率を取得
            int scale = getBmpImageScale(imageWidth, imageHeight, maxWidth, maxHeight);
            // 縮小率設定
            options.inSampleSize = scale;
        }
        // ビットマップデコード（画像をデコード）
        bmpImage = decode(options, path, false);

        Matrix mat = new Matrix();
        if(fromFragment == FROM_CAMERA) {
            mat.postRotate(90);
        }

        Bitmap bmp = Bitmap.createBitmap(bmpImage,0,0,bmpImage.getWidth(),
                bmpImage.getHeight(),mat,true);

        return bmp;
    }



    /**
     * ビットマップにする際にリサイズ必要かチェックする
     * @param imageWidth
     * @param imageHeight
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    private boolean chkSize(int imageWidth, int imageHeight, int maxWidth, int maxHeight) {

        boolean isResize = true;
        if(imageWidth <= maxWidth && imageHeight <= maxHeight) {
            // 縮小しないでも画面に収まる
            isResize = false;
        }
        return isResize;
    }


    /**
     * ビットマップへデコードする
     * @param options
     * @param path
     * @param decodeBounds
     * @return
     */
    private Bitmap decode(BitmapFactory.Options options, String path, boolean decodeBounds) {

        options.inJustDecodeBounds = decodeBounds;
        return BitmapFactory.decodeFile(path, options);
    }



    /**
     * ビットマップ生成時の縮尺率を取得する
     * @param imageWidth
     * @param imageHeight
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    private int getBmpImageScale(float imageWidth, float imageHeight, float maxWidth, float maxHeight) {

        int retScale = 1;

        float scaleX = (imageWidth / maxWidth) + 1.0f;
        float scaleY = (imageHeight / maxHeight) + 1.0f;

        // ここでは小数点以下を切り捨てていますが、
        // 小数点以下を切り捨てるとちょっとはみ出るので注意が必要
        retScale = Math.max((int)scaleX, (int)scaleY);

        return retScale;
    }



    /**
     * シングルタッチ（ドラッグ）イベント
     * @param event
     */
    private void actionDown(MotionEvent event) {

        // タッチ位置保持
        mMovePoint.set(event.getX(), event.getY());
        // Matrix値をロード
        mImageMatrix.set(mSavedImageMatrix);
    }



    /**
     * ドラッグイベント処理
     * @param event
     */
    private void actionMove(MotionEvent event) {

        PointF current = new PointF(event.getX(), event.getY());

        // X、Y座標の変化差分を取得
        float deltaX = current.x - mMovePoint.x;
        float deltaY = current.y - mMovePoint.y;

        // 画像の移動を実行
        mImageMatrix.postTranslate(deltaX, deltaY);
        super.setImageMatrix(mImageMatrix);
    }



    /**
     * マルチタッチ（ピンチ操作）イベント
     * @param event
     * @return
     */
    private boolean actionPointerDown(MotionEvent event) {

        float span = getSpan(event);
        if(span < 10f) {
            // 2点距離が短い場合は、無視。
            return false;
        }
        mSpan = span;

        // 拡縮の基点座標取得
        float deltaX = event.getX(0) + event.getX(1);
        float deltaY = event.getY(0) + event.getY(1);

        mMidPoint.set(deltaX / 2f, deltaY / 2f);
        mSavedImageMatrix.set(super.getImageMatrix());

        return true;
    }



    /**
     * ピンチ操作処理
     * @param event
     * @return
     */
    private boolean actionPointerMove(MotionEvent event) {

        mImageMatrix.set(mSavedImageMatrix);

        // scale
        float currentScale = getMatrixScale(mImageMatrix);
        float scale = getScale(event);

        float tmpScale = scale * currentScale;
        if(tmpScale < mInitialScale) {
            // 最小拡大率を下回る場合は、イベントを無視
            return false;
        }

        if(tmpScale > mMaxScale) {
            // 最大拡大率を超える場合は、イベントを無視
            return false;
        }

        // 拡縮率と基点を設定
        mImageMatrix.postScale(scale, scale, mMidPoint.x, mMidPoint.y);
        // 描画用にMatrixを設定
        super.setImageMatrix(mImageMatrix);

        return true;
    }



    /**
     * 拡縮率取得
     * @param event
     * @return
     */
    private float getScale(MotionEvent event) {

        float span = getSpan(event);

        return span / mSpan;
    }



    /**
     * 現在の拡縮率取得
     * @param matrix
     * @return
     */
    private float getMatrixScale(Matrix matrix) {

        float[] values = new float[MATRIX_VALUES_NUM];
        matrix.getValues(values);

        float currentScale = values[Matrix.MSCALE_X];
        if(currentScale == 0f) {
            return 1f;
        }
        return currentScale;
    }



    /**
     * 2点距離取得
     * @param event
     * @return
     */
    private float getSpan(MotionEvent event) {

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return FloatMath.sqrt(x * x + y * y);
    }
}
