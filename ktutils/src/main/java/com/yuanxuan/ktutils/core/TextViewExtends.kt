package com.yuanxuan.ktutils.core

import android.graphics.drawable.Drawable
import android.widget.TextView
import org.jetbrains.anko.dip

fun TextView.setDrawableLeft(resId: Int) {
    if (resId <= 0) {
        return
    }
    val drawable = resId.getDrawable()
    this.compoundDrawablePadding = dip(5.0f)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(drawable, null, null, null)
}

fun TextView.setDrawableLeft(drawable: Drawable?) {
    if (drawable == null) {
        this.setCompoundDrawables(null, null, null, null)
    } else {
        this.compoundDrawablePadding = dip(5.0f)
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        this.setCompoundDrawables(drawable, null, null, null)
    }
}

fun TextView.setDrawableRight(resId: Int) {
    val drawable = resId.getDrawable()
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.compoundDrawablePadding = dip(5.0f)
    this.setCompoundDrawables(null, null, drawable, null)
}

fun TextView.setDrawableTop(resId: Int, drawablePadding: Float = 5.0f) {
    val drawable = resId.getDrawable()
    this.compoundDrawablePadding = dip(drawablePadding)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(null, drawable, null, null)
}

fun TextView.setDrawableBottom(resId: Int) {
    val drawable = resId.getDrawable()
    this.compoundDrawablePadding = dip(5.0f)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(null, null, null, drawable)
}

