package com.yuanxuan.ktutils.core

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.yuanxuan.ktutils.widget.BottomDialogItemClickCallback
import com.yuanxuan.ktutils.widget.MyBottomDialog

/**
 * @author: limuyang
 * @date: 2019/2/26
 * @Description:
 */

fun Fragment.hide() {
    this.fragmentManager?.hide(this)
}

fun Fragment.show() {
    this.fragmentManager?.show(this)
}

fun Fragment.remove() {
    this.fragmentManager?.remove(this)
}

fun Fragment.showHide(vararg hideFragment: Fragment,
                      transaction: Int = FragmentTransaction.TRANSIT_NONE) {
    this.fragmentManager?.showHide(this, *hideFragment, transaction = transaction)
}

fun Fragment.showFragment(fragment: Fragment) {
    childFragmentManager.show(fragment)
}

fun Fragment.addFragment(
        addFragment: Fragment,
        @IdRes containerId: Int,
        isHide: Boolean = false,
        isAddStack: Boolean = false,
        inAnim: Int = -1,
        outAnim: Int = -1,
        tag: String = addFragment::class.java.name
) {
    childFragmentManager.add(addFragment, containerId, isHide, isAddStack, inAnim, outAnim, tag)
}

fun Fragment.hideFragment(vararg fragment: Fragment) {
    childFragmentManager.hide(*fragment)
}

/**
 * 选择弹出框
 */
fun Fragment.showConfirmDialog(title: String? = null, msg: String, leftBtnText: String = "确定", leftClickListener: (() -> Unit)? = null
                               , rightBtnText: String = "取消", rightClickListener: (() -> Unit)? = null) =
        activity?.showConfirmDialog(title, msg, leftBtnText, leftClickListener, rightBtnText, rightClickListener)

/**
 * 底部弹出框
 */
fun Fragment.showBottomDialog(title: String?,
                              items: List<MyBottomDialog.ActionSheetItem>,
                              itemClickCallback: BottomDialogItemClickCallback? = null
) = activity?.showBottomDialog(title, items, itemClickCallback)

