package com.yuliyang.picchoose

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import com.zhihu.matisse.internal.utils.MediaStoreCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_CHOOSE = 0
        const val REQUEST_CODE_CAPTURE = 1
        const val REQUEST_CODE_CROP = 2
    }

    var currentType = 0

    val mMediaStoreCompat = MediaStoreCompat(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                100
            )
        }

        startPic.setOnClickListener {
            if (currentType == 0) {
                Matisse.from(this@MainActivity)
                    .choose(MimeType.ofVideo())
                    .countable(false)
                    .maxSelectable(1)
                    .theme(R.style.Matisse_RightWay)
                    .showSingleMediaType(true)
                    .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(Glide4Engine())
                    .forResult(REQUEST_CODE_CHOOSE)

            } else {
                mMediaStoreCompat.setCaptureStrategy(CaptureStrategy(false, "com.yuliyang.picchoose.fileprovider"))
                mMediaStoreCompat.dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAPTURE && resultCode == RESULT_OK) {
            //拍照
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                val contentPath = mMediaStoreCompat.currentPhotoPath
                cropImage(Uri.fromFile(File(contentPath)))
            } else {
                val contentUri = mMediaStoreCompat.currentPhotoUri
                cropImage(contentUri)
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            //从相册选取
            cropImage(Matisse.obtainResult(data)[0])
        } else if (requestCode == REQUEST_CODE_CROP && resultCode == RESULT_OK) {
            println("success")
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = String.format("JPEG_%s.jpg", timeStamp)
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val outFile = File(storageDir, imageFileName)
        outFile.createNewFile()
        return outFile
    }

    private fun cropImage(contentUri: Uri) {
        val intent = Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(contentUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        val destUri = Uri.fromFile(createImageFile())
        intent.putExtra(MediaStore.EXTRA_OUTPUT, destUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }
}
