package com.yuanxuan.ktutils.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.yuanxuan.ktutils.R
import com.yuanxuan.ktutils.core.getColor
import com.yuanxuan.ktutils.core.screenHeight
import com.yuanxuan.ktutils.core.screenWidth
import kotlinx.android.synthetic.main.toast_view_actionsheet.view.*
import org.jetbrains.anko.dip

typealias BottomDialogItemClickCallback = (Int) -> Unit

class MyBottomDialog : DialogFragment() {

    private lateinit var mContext: Context
    private lateinit var rootView: View
    private var itemClickCallback: BottomDialogItemClickCallback? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val myDialog = Dialog(mContext, R.style.ActionSheetDialogStyle)
        myDialog.setContentView(rootView)
        myDialog.window?.apply {
            val params = attributes
            params.width = (mContext.screenWidth)
            setGravity(Gravity.BOTTOM)
            attributes = params
        }
        return myDialog
    }

    fun build(context: Context,
              title: String?,
              actionSheetItems: List<ActionSheetItem>,
              itemClickCallback: BottomDialogItemClickCallback? = null
    ): MyBottomDialog {
        mContext = context
        this.itemClickCallback = itemClickCallback
        rootView = LayoutInflater.from(mContext).inflate(R.layout.toast_view_actionsheet, LinearLayout(mContext), false)
        rootView.txt_cancel.setOnClickListener { dismiss() }
        rootView.txtTitle.text = title
        rootView.txtTitle.visible = !title.isNullOrBlank()
        //设置标题
        if (actionSheetItems.isEmpty()) {
            return this
        }
        val size = actionSheetItems.size
        // 添加条目过多的时候控制高度
        if (size >= 7) {
            val params = rootView.sLayout_content.layoutParams
            params.height = mContext.screenHeight / 2
            rootView.sLayout_content.layoutParams = params
        }
        var textView: TextView
        for (i in 0 until size) {
            val item = actionSheetItems[i]
            textView = TextView(context)
            textView.text = item.content
            textView.textSize = 16f
            textView.setTextColor(item.color)
            textView.gravity = Gravity.CENTER
            if (size == 1) {
                if (title.isNullOrBlank()) {
                    textView.setBackgroundResource(R.drawable.actionsheet_single_selector)
                } else {
                    textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector)
                }
            } else {
                if (title.isNullOrBlank()) {
                    if (i == 0) {
                        textView.setBackgroundResource(R.drawable.actionsheet_top_selector)
                    } else if (i < size - 1) {
                        textView.setBackgroundResource(R.drawable.actionsheet_middle_selector)
                    } else {
                        textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector)
                    }
                } else {
                    if (i in 0 until size - 1) {
                        textView.setBackgroundResource(R.drawable.actionsheet_middle_selector)
                    } else {
                        textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector)
                    }
                }
            }

            // 高度
            textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContext.dip(52f))
            // 点击事件
            textView.setOnClickListener {
                dismiss()
                itemClickCallback?.invoke(i)
            }
            rootView.lLayout_content.addView(textView)
        }
        return this
    }

    data class ActionSheetItem(
            val content: String,
            val color: Int = R.color.ios_blue.getColor())
}