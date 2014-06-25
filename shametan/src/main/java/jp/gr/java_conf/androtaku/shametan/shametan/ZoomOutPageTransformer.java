package jp.gr.java_conf.androtaku.shametan.shametan;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by takuma on 2014/06/25.
 */
public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            AlphaAnimation animation = new AlphaAnimation(0,0);
            animation.setDuration(0);
            animation.setFillAfter(true);
            view.startAnimation(animation);
            //view.setAlpha(0);
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                TranslateAnimation animation = new TranslateAnimation(0,0,horzMargin - vertMargin / 2,0);
                animation.setDuration(0);
                animation.setFillAfter(true);
                view.startAnimation(animation);
                //view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                TranslateAnimation animation = new TranslateAnimation(0,0,-horzMargin + vertMargin / 2,0);
                animation.setDuration(0);
                animation.setFillAfter(true);
                view.startAnimation(animation);
                //view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            ScaleAnimation scaleAnimation = new ScaleAnimation(1,scaleFactor,1,scaleFactor);
            scaleAnimation.setDuration(0);
            //scaleAnimation.setFillAfter(true);
            view.startAnimation(scaleAnimation);
            //view.setScaleX(scaleFactor);
            //view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            AlphaAnimation alphaAnimation = new AlphaAnimation(0,MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            alphaAnimation.setDuration(0);
            alphaAnimation.setFillAfter(true);
            view.startAnimation(alphaAnimation);
            //view.setAlpha(MIN_ALPHA +
              //      (scaleFactor - MIN_SCALE) /
                //            (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            AlphaAnimation alphaAnimation = new AlphaAnimation(0,0);
            alphaAnimation.setDuration(0);
            alphaAnimation.setFillAfter(true);
            view.startAnimation(alphaAnimation);
            //view.setAlpha(0);
        }
    }
}
