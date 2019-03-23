package com.zhihu.matisse.ext

import android.content.Context
import android.os.Build

/**
 * 是否6.0以上
 */
fun Context.isVersion6OrAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

/**
 * 是否7.0以上
 */
fun Context.isVersion7OrAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

/**
 * 是否8.0以上
 */
fun Context.isVersion8OrAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

/**
 * 是否5.0以上
 */
fun Context.isVersion5OrAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
