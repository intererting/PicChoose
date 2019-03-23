package com.yuanxuan.ktutils.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.yuanxuan.ktutils.KtUtilCode

/**
 * get SharedPreferences
 * @receiver Context
 * @param spName String
 * @param mode Int
 */
fun Context.getSP(spName: String = "right_way_sp", mode: Int = Context.MODE_PRIVATE): SharedPreferences =
        getSharedPreferences(spName, mode)

fun getSP(spName: String = "right_way_sp", mode: Int = Context.MODE_PRIVATE): SharedPreferences =
        KtUtilCode.app.applicationContext.getSP(spName, mode)

@SuppressLint("ApplySharedPref")
inline fun SharedPreferences.edit(
        commit: Boolean = false,
        action: SharedPreferences.Editor.() -> Unit
) {
    val editor = edit()
    action(editor)
    if (commit) {
        editor.commit()
    } else {
        editor.apply()
    }
}

inline fun SharedPreferences.read(action: SharedPreferences.() -> Unit) {
    action(this)
}
