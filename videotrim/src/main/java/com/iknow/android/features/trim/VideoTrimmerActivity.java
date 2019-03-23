package com.iknow.android.features.trim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.WindowManager;

import com.iknow.android.R;
import com.iknow.android.features.common.ui.BaseActivity;
import com.iknow.android.features.compress.VideoCompressor;
import com.iknow.android.interfaces.VideoCompressListener;
import com.iknow.android.interfaces.VideoTrimListener;
import com.iknow.android.utils.StorageUtil;
import com.iknow.android.widget.VideoTrimmerView;

import java.io.File;

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public class VideoTrimmerActivity extends BaseActivity implements VideoTrimListener {

    private static final String VIDEO_PATH_KEY = "video-file-path";
    private static final String COMPRESSED_VIDEO_FILE_NAME = "compress.mp4";
    public static final int VIDEO_TRIM_REQUEST_CODE = 0x001;
    private ProgressDialog mProgressDialog;
    private VideoTrimmerView trimmerView;

    public static void call(FragmentActivity from, String videoPath) {
        if (!TextUtils.isEmpty(videoPath)) {
            Bundle bundle = new Bundle();
            bundle.putString(VIDEO_PATH_KEY, videoPath);
            Intent intent = new Intent(from, VideoTrimmerActivity.class);
            intent.putExtras(bundle);
            from.startActivityForResult(intent, VIDEO_TRIM_REQUEST_CODE);
        }
    }

    @Override
    public void initUI() {
        super.initUI();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_video_trim);
        trimmerView = findViewById(R.id.trimmer_view);
        Bundle bd = getIntent().getExtras();
        String path = "";
        if (bd != null) path = bd.getString(VIDEO_PATH_KEY);
        if (trimmerView != null) {
            trimmerView.setOnTrimVideoListener(this);
            trimmerView.initVideoByURI(Uri.parse(path));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        trimmerView.onVideoPause();
        trimmerView.setRestoreState(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        trimmerView.onDestroy();
    }

    @Override
    public void onStartTrim() {
        buildDialog(getResources().getString(R.string.trimming)).show();
    }

    @Override
    public void onFinishTrim(String in) {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        String out = StorageUtil.getCacheDir() + File.separator + COMPRESSED_VIDEO_FILE_NAME;
        buildDialog(getResources().getString(R.string.compressing)).show();
        VideoCompressor.compress(this, in, out, new VideoCompressListener() {
            @Override
            public void onSuccess(String message) {
            }

            @Override
            public void onFailure(String message) {
            }

            @Override
            public void onFinish() {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public void onCancel() {
        trimmerView.onDestroy();
        finish();
    }

    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "", msg);
        }
        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }
}
