package com.dicoding.appcapstone.ui.detail

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.dicoding.appcapstone.R
import com.google.android.exoplayer2.ui.PlayerView

class DetailArticleViewHolder(view: View) {
    val thumbnailImageView: ImageView = view.findViewById(R.id.article_thumbnail)
    val titleTextView: TextView = view.findViewById(R.id.article_title)
    val descriptionTextView: TextView = view.findViewById(R.id.article_description)
    val nutritionTextView: TextView = view.findViewById(R.id.article_nutrition)
    val playerView: PlayerView = view.findViewById(R.id.player_view)
    val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
}


