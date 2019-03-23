package com.yuliyang.picchoose

import android.app.Application
import com.iknow.android.TrimmerClient

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        TrimmerClient.init(this)
    }
}