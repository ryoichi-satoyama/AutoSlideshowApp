package jp.techacademy.ryoichi.satoyama.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI

class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE = 100
    private var uriList: ArrayList<Uri>? = null
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                uriList = getContentInfo()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
        } else {
            uriList = getContentInfo()
        }

        if(uriList != null) {
            imageView.setImageURI(uriList!!.get(0))
        }

        goButton.setOnClickListener {
            var size = uriList!!.size
            if((position+1) == size) {
                position = 0
            } else {
                position += 1
            }
            imageView.setImageURI(uriList!!.get(position))
        }
        backButton.setOnClickListener {
            var size = uriList!!.size
            if((position) == 0) {
                position = size - 1
            } else {
                position -= 1
            }
            imageView.setImageURI(uriList!!.get(position))
        }
    }

    private fun getContentInfo(): ArrayList<Uri> {
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null)
        val uriList = ArrayList<Uri>()
        if(cursor!!.moveToFirst()) {
            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                uriList.add(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id))
            } while (cursor!!.moveToNext())
        }
        cursor.close()
        return uriList
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uriList = getContentInfo()
                }
        }
    }
}