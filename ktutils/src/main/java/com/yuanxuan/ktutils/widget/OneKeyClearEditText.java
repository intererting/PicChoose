package com.yuanxuan.ktutils.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.yuanxuan.ktutils.R;


/**
 * 一键清除的EditText
 */
public class OneKeyClearEditText extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {

    private Drawable mClearDrawable;// 一键删除的按钮
    //    private int colorAccent;// 获得主题的颜色
    private int DrawableColor = 0;
    private boolean hasFocus;// 控件是否有焦点

    public OneKeyClearEditText(Context context) {
        this(context, null);
    }

    public OneKeyClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public OneKeyClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.OneKeyClearEditText);
        DrawableColor = typedArray.getColor(R.styleable.OneKeyClearEditText_deleteColor, Color.LTGRAY);
        typedArray.recycle();
        initClearDrawable();
//        setMaxLines(1);
//        setSingleLine();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (text != null)
            setSelection(text.length());
    }

    private void initClearDrawable() {
        setClearIconVisible(false);
        // 设置焦点改变的监听
        setOnFocusChangeListener(this);
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mClearDrawable = getCompoundDrawables()[2];// 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(R.drawable.delete_all);
            setCompoundDrawablePadding(5);
        }
        DrawableCompat.setTint(mClearDrawable, DrawableColor);// 设置删除按钮的颜色和TextColor的颜色一致
        int size = (int) (getTextSize());

        mClearDrawable.setBounds(0, 0, size, size);// 设置Drawable的宽高

    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     */
    private void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mClearDrawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            // 判断触摸点是否在水平范围内
            boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight() - 15))
                    && (x < (getWidth() - getPaddingRight()));
            // 获取删除图标的边界，返回一个Rect对象
            Rect rect = mClearDrawable.getBounds();
            // 获取删除图标的高度
            int height = rect.height();
            int y = (int) event.getY();
            // 计算图标底部到控件底部的距离
            int distance = getHeight() - height - getPaddingTop();
            // 判断触摸点是否在竖直范围内(可能会有点误差)
            // 触摸点的纵坐标在distance到（distance+图标自身的高度）之内，则视为点中删除图标
            boolean isInnerHeight = (y > distance - 15) && (y < (distance + height + 5));
            if (isInnerHeight && isInnerWidth) {
                this.setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (hasFocus) {
            setClearIconVisible(text.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText() != null && getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }

    }

    public @NonNull
    String getInputText() {
        if (getText() == null) return "";
        return getText().toString().trim();
    }
}
