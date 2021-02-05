package jp.techacademy.ryoichi.satoyama.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE = 100 //パーミッションリクエストコード
//    private var uriList: ArrayList<Uri>? = null //イメージのURIリスト
    private lateinit var uriList: ArrayList<Uri>
    private var position = 0 //表示するイメージの位置
    private var mHandler = Handler() //ハンドラーの取得
    private var mTimer: Timer? = null //タイマーの変数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                uriList = getContentInfo()
                showImage(position)
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            uriList = getContentInfo()
            showImage(position)
        }



        //進むボタン押下時の処理
        advanceButton.setOnClickListener {
            advanceImage()
        }

        //戻るボタン押下時の処理
        backButton.setOnClickListener {
            backImage()
        }

        //再生/停止ボタン押下時の処理
        slideshowButton.setOnClickListener {
            val b = it as Button
            if (b.text == "再生") {
                //各ボタンの操作
                b.text = "停止"
                advanceButton.isEnabled = false
                backButton.isEnabled = false

                //スライドショーの開始
                startSlide()
            } else if (b.text == "停止") {
                //各ボタンの操作
                b.text = "再生"
                advanceButton.isEnabled = true
                backButton.isEnabled = true

                //スライドショーの停止
                stopSlide()
            }
        }
    }

    //次のイメージを表示する処理
    private fun advanceImage() {
        val size = uriList.size
        if ((position + 1) == size) {
            position = 0
        } else {
            position += 1
        }
        showImage(position)
    }

    //前のイメージを表示する処理
    private fun backImage() {
        val size = uriList.size
        if ((position) == 0) {
            position = size - 1
        } else {
            position -= 1
        }
        showImage(position)
    }

    //イメージを表示する処理
    private fun showImage(position: Int) {
        imageView.setImageURI(uriList[position])
    }

    //スライドショーを開始する処理
    private fun startSlide() {
        mTimer = Timer()
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mHandler.post {
                    advanceImage()
                }
            }
        }, 2000, 2000)
    }

    //スライドショーを停止する処理
    private fun stopSlide() {
        mTimer!!.cancel()
    }

    //イメージ表示に必要な各イメージのURIを取得
    private fun getContentInfo(): ArrayList<Uri> {
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )
        val uriList = ArrayList<Uri>()

        if (cursor!!.moveToFirst()) {
            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                uriList.add(
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return uriList
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uriList = getContentInfo()
                    showImage(position)
                } else {
                    Toast.makeText(this, "ストレージアクセスを許可してください", Toast.LENGTH_SHORT).show()
                }
        }
    }


}