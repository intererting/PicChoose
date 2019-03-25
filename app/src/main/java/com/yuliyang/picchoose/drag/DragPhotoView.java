package com.yuliyang.picchoose.drag;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.github.chrisbanes.photoview.PhotoView;
import org.jetbrains.anko.DimensionsKt;

/**
 * Created by wing on 2016/12/22.
 */

public class DragPhotoView extends PhotoView {
    private Paint mPaint;

    // downX
    private float mDownX;
    // down Y
    private float mDownY;

    private float mTranslateY;
    private float mTranslateX;
    private float mScale = 1;
    private int mWidth;
    private int mHeight;
    private float mMinScale = 0.5f;
    private int mAlpha = 255;
    private int MAX_TRANSLATE_Y;

    private final static long DURATION = 300;
    private boolean canFinish = false;
    private boolean isAnimate = false;

    //is event on PhotoView
    private boolean isTouchEvent = false;
    private OnTapListener mTapListener;
    private OnExitListener mExitListener;

    private float scaleCenterX = 0;
    private float scaleCenterY = 0;

    private float mTouchSlop;

    public DragPhotoView(Context context) {
        this(context, null);
    }

    public DragPhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public DragPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        MAX_TRANSLATE_Y = DimensionsKt.dip(this, 120);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setAlpha(mAlpha);
        canvas.drawRect(0, 0, mWidth, mHeight, mPaint);
        canvas.translate(mTranslateX, mTranslateY);
        canvas.scale(mScale, mScale, scaleCenterX, scaleCenterY);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (getScale() == 1 || getDisplayRect().top == 0) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    onActionDown(event);
                    //change the canFinish flag
                    canFinish = true;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    canFinish = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    //in viewpager
                    if (mTranslateY == 0 && mTranslateX != 0) {

                        //如果不消费事件，则不作操作
                        if (!isTouchEvent) {
                            mScale = 1;
                            return super.dispatchTouchEvent(event);
                        }
                    }

                    //single finger drag  down
                    if (mTranslateY >= 0 && event.getPointerCount() == 1) {
                        onActionMove(event);

                        //如果有上下位移 则不交给viewpager
                        if (mTranslateY != 0) {
                            isTouchEvent = true;
                        }
                        return true;
                    }

                    //防止下拉的时候双手缩放
                    if (mTranslateY >= 0 && mScale < 0.95) {
                        return true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    //防止下拉的时候双手缩放
                    if (event.getPointerCount() == 1) {
                        onActionUp(event);
                        isTouchEvent = false;
                        //judge finish or not
                        postDelayed(() -> {
                            if (mTranslateX == 0 && mTranslateY == 0 && canFinish && !isAnimate) {
                                if (mTapListener != null) {
                                    mTapListener.onTap(DragPhotoView.this);
                                }
                            }
                            canFinish = false;
                        }, DURATION - 1);
                    }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void onActionUp(MotionEvent event) {
        if (mTranslateY > MAX_TRANSLATE_Y && canFinish) {
            if (mExitListener != null) {
                mExitListener.onExit(this, mTranslateX, mTranslateY, scaleCenterX, scaleCenterY, mScale);
            } else {
                throw new RuntimeException("DragPhotoView: onExitLister can't be null ! call setOnExitListener() ");
            }
        } else {
            performAnimation();
        }
    }

    private void onActionMove(MotionEvent event) {

        float moveY = event.getY();
        float moveX = event.getX();
        mTranslateX = moveX - mDownX;
        mTranslateY = moveY - mDownY;

        //保证上划到到顶还可以继续滑动
        if (mTranslateY < 0) {
            mTranslateY = 0;
        }

        float percent = mTranslateY / MAX_TRANSLATE_Y;
        if (mScale >= mMinScale / getScale() && mScale <= 1f) {
            mScale = (1 - percent);

            mAlpha = (int) (255 * (1 - percent));
            if (mAlpha > 255) {
                mAlpha = 255;
            } else if (mAlpha < 0) {
                mAlpha = 0;
            }
        }
        if (mScale < mMinScale / getScale()) {
            mScale = mMinScale / getScale();
        } else if (mScale > 1f) {
            mScale = 1;
        }

        invalidate();
    }

    private void performAnimation() {
        getScaleAnimation().start();
        getTranslateXAnimation().start();
        getTranslateYAnimation().start();
        getAlphaAnimation().start();
    }

    private ValueAnimator getAlphaAnimation() {
        final ValueAnimator animator = ValueAnimator.ofInt(mAlpha, 255);
        animator.setDuration(DURATION);
        animator.addUpdateListener(valueAnimator -> mAlpha = (int) valueAnimator.getAnimatedValue());

        return animator;
    }

    private ValueAnimator getTranslateYAnimation() {
        final ValueAnimator animator = ValueAnimator.ofFloat(mTranslateY, 0);
        animator.setDuration(DURATION);
        animator.addUpdateListener(valueAnimator -> mTranslateY = (float) valueAnimator.getAnimatedValue());

        return animator;
    }

    private ValueAnimator getTranslateXAnimation() {
        final ValueAnimator animator = ValueAnimator.ofFloat(mTranslateX, 0);
        animator.setDuration(DURATION);
        animator.addUpdateListener(valueAnimator -> mTranslateX = (float) valueAnimator.getAnimatedValue());

        return animator;
    }

    private ValueAnimator getScaleAnimation() {
        final ValueAnimator animator = ValueAnimator.ofFloat(mScale, 1);
        animator.setDuration(DURATION);
        animator.addUpdateListener(valueAnimator -> {
            mScale = (float) valueAnimator.getAnimatedValue();
            invalidate();
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimate = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnimate = false;
                animator.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return animator;
    }

    private void onActionDown(MotionEvent event) {
        scaleCenterX = event.getX();
        scaleCenterY = event.getY();
        mDownX = event.getX();
        mDownY = event.getY();
    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }

    public void setOnTapListener(OnTapListener listener) {
        mTapListener = listener;
    }

    public void setOnExitListener(OnExitListener listener) {
        mExitListener = listener;
    }

    public interface OnTapListener {
        void onTap(DragPhotoView view);
    }

    public interface OnExitListener {
        void onExit(DragPhotoView view, float translateX, float translateY, float w, float h, float resultScale);
    }

    public void finishAnimationCallBack() {
        mTranslateX = scaleCenterX * (mScale - 1) - (getDisplayRect().left) * mScale;
        mTranslateY = -scaleCenterY + scaleCenterY * mScale;
        invalidate();
    }
}
