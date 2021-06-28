package com.example.teddystagram.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("setImage")
fun setImage(imageView: ImageView, imageUrl: String) {
    Glide.with(imageView)
        .load(imageUrl)
        .into(imageView)
}