package com.yuliyang.picchoose.drag;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import com.yuliyang.picchoose.R;

import java.util.ArrayList;
import java.util.List;

public class DragPhotoActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private List<String> mList;
    private DragPhotoView[] mPhotoViews;

    int mOriginLeft;
    int mOriginTop;
    int mOriginHeight;
    int mOriginWidth;
    int mOriginCenterX;
    int mOriginCenterY;
    private float mTargetHeight;
    private float mTargetWidth;
    private float mScaleX;
    private float mScaleY;
    private float mTranslationX;
    private float mTranslationY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drag_photo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        mViewPager = findViewById(R.id.viewpager);

        mList = new ArrayList<>();

        mList.add("path");
        mList.add("path");
        mList.add("path");

        mPhotoViews = new DragPhotoView[mList.size()];

        for (int i = 0; i < mPhotoViews.length; i++) {
            mPhotoViews[i] = (DragPhotoView) View.inflate(this, R.layout.item_viewpager, null);
            mPhotoViews[i].setImageResource(R.drawable.wugeng);
            mPhotoViews[i].setOnTapListener(view -> finishWithAnimation());

            mPhotoViews[i].setOnExitListener(this::performExitAnimation);
        }

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mList.size();
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(mPhotoViews[position]);
                return mPhotoViews[position];
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(mPhotoViews[position]);
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        });

        mViewPager.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        mOriginLeft = getIntent().getIntExtra("left", 0);
                        mOriginTop = getIntent().getIntExtra("top", 0);
                        mOriginHeight = getIntent().getIntExtra("height", 0);
                        mOriginWidth = getIntent().getIntExtra("width", 0);
                        mOriginCenterX = mOriginLeft + mOriginWidth / 2;
                        mOriginCenterY = mOriginTop + mOriginHeight / 2;

                        int[] location = new int[2];

                        final DragPhotoView photoView = mPhotoViews[0];
                        photoView.getLocationOnScreen(location);

                        mTargetHeight = (float) photoView.getHeight();
                        mTargetWidth = (float) photoView.getWidth();
                        mScaleX = (float) mOriginWidth / mTargetWidth;
                        mScaleY = (float) mOriginHeight / mTargetHeight;

                        float targetCenterX = location[0] + mTargetWidth / 2;
                        float targetCenterY = location[1] + mTargetHeight / 2;

                        mTranslationX = mOriginCenterX - targetCenterX;
                        mTranslationY = mOriginCenterY - targetCenterY;
                        photoView.setTranslationX(mTranslationX);
                        photoView.setTranslationY(mTranslationY);

                        photoView.setScaleX(mScaleX);
                        photoView.setScaleY(mScaleY);

                        performEnterAnimation();

                        for (DragPhotoView mPhotoView : mPhotoViews) {
                            mPhotoView.setMinScale(mScaleX);
                        }
                    }
                });
    }

    /**
     * ===================================================================================
     * <p>
     * 底下是低版本"共享元素"实现   不需要过分关心  如有需要 可作为参考.
     * <p>
     * Code  under is shared transitions in all android versions implementation
     */
    private void performExitAnimation(final DragPhotoView view, float x, float y,
                                      float mScaleCenterX, float mScaleCenterY, float resultScale) {
        System.out.println(view.getDisplayRect().top);
        view.finishAnimationCallBack();
        float viewX = mScaleCenterX * (1 - resultScale) + (view.getDisplayRect().left) * resultScale + x;
        float viewY = mScaleCenterY + y - mScaleCenterY * (mScaleX);
        view.setX(viewX);
        view.setY(viewY);

        float centerX = view.getX() + mOriginWidth / 2f;
        float centerY = view.getY() + mOriginHeight / 2f;

        float translateX = mOriginCenterX - centerX;
        float translateY = mOriginCenterY - centerY - view.getDisplayRect().top * (mScaleX) * view.getScale();

        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(view.getX(), view.getX() + translateX);
        translateXAnimator.addUpdateListener(valueAnimator -> view.setX((Float) valueAnimator.getAnimatedValue()));
        translateXAnimator.setDuration(300);
        translateXAnimator.start();
        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(view.getY(), view.getY() + translateY);
        translateYAnimator.addUpdateListener(valueAnimator -> view.setY((Float) valueAnimator.getAnimatedValue()));
        translateYAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();
    }

    private void finishWithAnimation() {

        final DragPhotoView photoView = mPhotoViews[0];
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(0, mTranslationX);
        translateXAnimator.addUpdateListener(valueAnimator -> photoView.setX((Float) valueAnimator.getAnimatedValue()));
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(0, mTranslationY);
        translateYAnimator.addUpdateListener(valueAnimator -> photoView.setY((Float) valueAnimator.getAnimatedValue()));
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(1, mScaleY);
        scaleYAnimator.addUpdateListener(valueAnimator -> photoView.setScaleY((Float) valueAnimator.getAnimatedValue()));
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(1, mScaleX);
        scaleXAnimator.addUpdateListener(valueAnimator -> photoView.setScaleX((Float) valueAnimator.getAnimatedValue()));

        scaleXAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();
    }

    private void performEnterAnimation() {
        final DragPhotoView photoView = mPhotoViews[0];
        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(photoView.getX(), 0);
        translateXAnimator.addUpdateListener(valueAnimator -> photoView.setX((Float) valueAnimator.getAnimatedValue()));
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(photoView.getY(), 0);
        translateYAnimator.addUpdateListener(valueAnimator -> photoView.setY((Float) valueAnimator.getAnimatedValue()));
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(mScaleY, 1);
        scaleYAnimator.addUpdateListener(valueAnimator -> photoView.setScaleY((Float) valueAnimator.getAnimatedValue()));
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(mScaleX, 1);
        scaleXAnimator.addUpdateListener(valueAnimator -> photoView.setScaleX((Float) valueAnimator.getAnimatedValue()));
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();
    }

    @Override
    public void onBackPressed() {
        finishWithAnimation();
    }
}
