package com.zhihu.matisse.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorInt
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.zhihu.matisse.R

private const val TAG_STATUS_BAR = "TAG_STATUS_BAR"
const val TAG_OFFSET = "TAG_OFFSET"
private const val KEY_OFFSET = -123

/**
 * 获取状态栏的高度
 */
inline val Context.statusBarHeight: Int
    get() {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

/**
 * Set the status bar's light mode.
 *
 */
inline var Activity.statusBarLightMode: Boolean
    set(value) {
        window.statusBarLightMode = value
    }
    get() = window.statusBarLightMode

inline var Window.statusBarLightMode: Boolean
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var vis = decorView.systemUiVisibility
            vis = if (value) {
                this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decorView.systemUiVisibility = vis
        }
    }
    get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val vis = decorView.systemUiVisibility
            return vis == vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        return false
    }

/**
 * Add the top margin size equals status bar's height for view.
 *
 */
fun View.addMarginTopEqualStatusBarHeight() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    this.tag = TAG_OFFSET
    val haveSetOffset = getTag(KEY_OFFSET)
    if (haveSetOffset != null && haveSetOffset as Boolean) return
    val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(
        layoutParams.leftMargin,
        layoutParams.topMargin + context.statusBarHeight,
        layoutParams.rightMargin,
        layoutParams.bottomMargin
    )
    setTag(KEY_OFFSET, true)
}

@SuppressLint("ObsoleteSdkInt")
fun Activity.setStatusBarColor(@ColorInt color: Int = Color.WHITE) {
    val contentView = findViewById<ViewGroup>(android.R.id.content)
    contentView.getChildAt(0)?.addMarginTopEqualStatusBarHeight()

    val mColor = if (color == Color.WHITE && !isVersion6OrAbove()) {
        resources.getColor(R.color.default_statusbar_color)
    } else {
        color
    }

    val fakeStatusBarView = contentView.findViewWithTag<View>(TAG_STATUS_BAR)
    if (fakeStatusBarView != null) {
        fakeStatusBarView.setBackgroundColor(mColor)
    } else {
        contentView.addView(createColorStatusBarView(mColor))
    }
}

private fun Context.createColorStatusBarView(color: Int): View {
    val statusBarView = View(this)
    statusBarView.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, this.statusBarHeight
    )
    statusBarView.setBackgroundColor(color)
    statusBarView.tag = TAG_STATUS_BAR
    return statusBarView
}


fun Activity.transparentStatusBar() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
    } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}