package com.example.scanner.ui.fragment.add_item

import android.R
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import java.io.IOException

@BindingAdapter("bind_image_url")
fun bindImageUrl(imageView: ImageView, url : String?){
    Log.d("AddItem", "2:${url}")
    when(url){
        null -> Unit
        "" -> loadImageFromAssets(imageView)
        else -> Picasso.get().load(url).error(R.drawable.stat_notify_error).into(imageView)
    }

}
private fun loadImageFromAssets(imageView: ImageView) {
    try {
        val inputStream = imageView.context.assets.open("item-playstore.png")
        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        imageView.setImageBitmap(bitmap)
    } catch (e: IOException) {
        e.printStackTrace()
         }
}