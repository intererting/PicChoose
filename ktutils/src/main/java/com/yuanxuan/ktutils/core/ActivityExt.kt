package com.yuanxuan.ktutils.core

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.yuanxuan.ktutils.widget.BottomDialogItemClickCallback
import com.yuanxuan.ktutils.widget.MyAlertDialog
import com.yuanxuan.ktutils.widget.MyBottomDialog

/**
 * @author: limuyang
 * @date: 2019/2/26
 * @Description: activity属性相关扩展
 */

fun Activity.setBackgroundColor(@ColorInt color: Int) {
    window.setBackgroundDrawable(ColorDrawable(color))
}

//////////////////////////////////////////////////
//                 Fragment相关                  //
//////////////////////////////////////////////////
fun FragmentActivity.addFragment(
        addFragment: Fragment,
        @IdRes containerId: Int,
        isHide: Boolean = false,
        isAddStack: Boolean = false,
        inAnim: Int = -1,
        outAnim: Int = -1,
        tag: String = addFragment::class.java.name
) {
    supportFragmentManager.add(addFragment, containerId, isHide, isAddStack, inAnim, outAnim, tag)
}

fun FragmentActivity.addFragment(
        addList: List<Fragment>,
        @IdRes containerId: Int,
        showIndex: Int = 0
) {
    supportFragmentManager.add(addList, containerId, showIndex)
}

fun FragmentActivity.replaceFragment(
        fragment: Fragment,
        @IdRes containerId: Int,
        isAddStack: Boolean = false,
        tag: String = fragment::class.java.name
) {
    supportFragmentManager.replace(fragment, containerId, isAddStack, tag)
}

fun FragmentActivity.showFragment(fragment: Fragment) {
    supportFragmentManager.show(fragment)
}

fun FragmentActivity.hideFragment(vararg fragment: Fragment) {
    supportFragmentManager.hide(*fragment)
}


fun FragmentActivity.hideFragmentWithAnim(
        hideFragment: Fragment,
        inAnim: Int = -1,
        outAnim: Int = -1) {
    val ft = supportFragmentManager.beginTransaction()
    ft.setCustomAnimations(inAnim, outAnim)
    ft.hide(hideFragment)
    ft.commit()
}

fun FragmentActivity.removeFragment(vararg fragment: Fragment) {
    supportFragmentManager.remove(*fragment)
}

fun FragmentActivity.removeAllFragment() {
    supportFragmentManager.removeAll()
}

fun FragmentActivity.switchFragment(
        showFragment: Fragment,
        @IdRes containerId: Int,
        transaction: Int = FragmentTransaction.TRANSIT_NONE
) {
    supportFragmentManager.switch(showFragment, containerId, transaction)
}

fun FragmentActivity.showConfirmDialog(title: String? = null, msg: String, leftBtnText: String = "确定", leftClickListener: (() -> Unit)? = null
                                       , rightBtnText: String = "取消", rightClickListener: (() -> Unit)? = null) {
    MyAlertDialog().build(this, title, msg, leftBtnText, leftClickListener, rightBtnText, rightClickListener).show(supportFragmentManager, "confirmDialog")
}

/**
 * 底部弹出框
 */
fun FragmentActivity.showBottomDialog(title: String?,
                                      items: List<MyBottomDialog.ActionSheetItem>,
                                      itemClickCallback: BottomDialogItemClickCallback? = null
) {
    MyBottomDialog().build(this, title, items, itemClickCallback)
            .show(supportFragmentManager, "bottomDialog")
}

