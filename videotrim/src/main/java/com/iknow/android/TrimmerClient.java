package com.iknow.android;

import android.content.Context;
import android.util.Log;

import iknow.android.utils.BaseUtils;
import nl.bravobit.ffmpeg.FFmpeg;

/**
 * Author：J.Chou
 * Date：  2016.09.27 10:44 AM
 * Email： who_know_me@163.com
 * Describe:
 */
public final class TrimmerClient {

    public static void init(Context mContext) {
        BaseUtils.init(mContext);
        if (!FFmpeg.getInstance(mContext).isSupported()) {
            Log.e("ZApplication", "Android cup arch not supported!");
        }
    }

}
