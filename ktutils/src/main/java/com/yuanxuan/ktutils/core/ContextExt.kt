package com.yuanxuan.ktutils.core

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.os.Environment
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.dip
import com.yuanxuan.ktutils.widget.leftPadding
import com.yuanxuan.ktutils.widget.rightPadding
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

fun Context.showImgToast(@DrawableRes resId: Int) {
    val imageView = ImageView(this)
    // 设置尺寸
    val imageLength = dip(120)
    val mParams = ViewGroup.LayoutParams(imageLength, imageLength)
    imageView.layoutParams = mParams
    imageView.setBackgroundResource(resId)
    // new一个toast传入要显示的activity的上下文
    val mToast = Toast(this)
    // 显示的时间
    mToast.duration = Toast.LENGTH_SHORT
    // 显示的位置
    mToast.setGravity(Gravity.CENTER, 0, 0)
    // 重新给toast进行布局
    val toastLayout = RelativeLayout(this)
    // 把imageView添加到toastLayout的布局当中
    toastLayout.addView(imageView)
    // 把toastLayout添加到toast的布局当中
    mToast.view = toastLayout
    mToast.apply { show() }
}

fun Context.centerToast(msg: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    val mToast = Toast.makeText(this, msg, duration)
    try {
        val rectShape = RoundRectShape(floatArrayOf(100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f), null, null)
        val shapeDrawable = ShapeDrawable(rectShape).apply {
            paint.color = Color.argb(210, 255, 255, 255)
            paint.style = Paint.Style.FILL
            paint.isAntiAlias = true
            paint.flags = Paint.ANTI_ALIAS_FLAG
        }
        mToast.view.background = shapeDrawable
        mToast.view.leftPadding = dip(16)
        mToast.view.rightPadding = dip(16)
        val text = mToast.view.findViewById<TextView>(android.R.id.message)
        text.setTextColor(Color.BLACK)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    mToast.setGravity(Gravity.CENTER, 0, 0)
    mToast.show()
}

fun Context.tips(msg: CharSequence) {
    centerToast(msg)
}

/**
 * 获取网络缓存路径
 */
fun Context.provideNetCache(): File? {
    if (cacheDir.createDir()) {
        val result = File(cacheDir, "netCache")
        return if (!result.mkdirs() && (!result.exists() || !result.isDirectory)) null else result
    }
    return null
}

/**
 * 外部存储文件缓存路径
 */
fun Context.provideExternalFileCache(): File? {
    if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && externalCacheDir.createDir()) {
        val result = File(externalCacheDir, "fileCache")
        return if (!result.mkdirs() && (!result.exists() || !result.isDirectory)) null else result
    }
    return null
}

/**
 * 文件缓存路径
 */
fun Context.provideFileCache(): File? {
    if (cacheDir.createDir()) {
        val result = File(cacheDir, "fileCache")
        return if (!result.mkdirs() && (!result.exists() || !result.isDirectory)) null else result
    }
    return null
}

const val TYPE_PIC = 0
const val TYPE_MP4 = 1

/**
 * 视频录制存放地址
 */
fun provideMediaTempPath(type: Int = TYPE_MP4): File? {
    if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
        return null
    }
//        val mediaStorageDir = File(Environment.getExternalStorageDirectory(), "CameraSample")
    val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
            ), "RightWayVideo"
    )
    if (!mediaStorageDir.exists()) {
        if (!mediaStorageDir.mkdirs()) {
            return null
        }
    }

    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())

    val mediaFile: File
    if (type == TYPE_PIC) {
        mediaFile = File(
                mediaStorageDir.path + File.separator +
                        "RW_" + timeStamp + ".jpeg"
        )
    } else {
        mediaFile = File(
                mediaStorageDir.path + File.separator +
                        "RW_" + timeStamp + ".mp4"
        )
    }
    return mediaFile
}

fun Context.getProcessName(pid: Int): String? {
    var reader: BufferedReader? = null
    try {
        reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
        var processName = reader.readLine()
        if (!processName.isNullOrBlank()) {
            processName = processName.trim { it <= ' ' }
        }
        return processName
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
    } finally {
        try {
            if (reader != null) {
                reader.close()
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }
    return null
}

fun Context.inflateViewPattern(@LayoutRes resId: Int,
                               parent: ViewGroup = LinearLayout(this)): View {
    return LayoutInflater.from(this).inflate(
            resId,
            parent,
            false)
}

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
