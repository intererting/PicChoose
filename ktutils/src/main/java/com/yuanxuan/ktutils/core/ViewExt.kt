package com.yuanxuan.ktutils.core

import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor

@ObsoleteCoroutinesApi
fun View.onSingleClickStart(action: suspend () -> Unit) {
    val eventActor = GlobalScope.actor<Unit>(Dispatchers.Main) {
        for (event in channel) {
            action()
        }
    }
    setOnClickListener {
        eventActor.offer(Unit)
    }
}