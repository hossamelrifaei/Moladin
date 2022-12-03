package com.moladin.ui_common.ui

import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


fun AppCompatImageView.load(url: String?) {
    Glide.with(this.context)
        .load(url)
        .apply(
            RequestOptions()
                .centerCrop()
        )
        .into(this)
}