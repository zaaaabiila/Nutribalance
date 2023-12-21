package com.dicoding.appcapstone.ui.article

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.appcapstone.response.ResultsItem
import coil.load
import com.dicoding.appcapstone.R
import com.dicoding.appcapstone.ui.detail.DetailFragment

class ArticleAdapter(private val context: Context, private val resultsItemList: List<ResultsItem>) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val resultsItem = resultsItemList[position]

        // Bind data to views
        holder.imageView.load(resultsItem.thumbnailUrl)
        holder.titleTextView.text = resultsItem.name
        holder.contentTextView.text = resultsItem.description

        holder.itemView.setOnClickListener {
            // Create a new instance of DetailFragment and pass the ResultsItem to it
            val detailFragment = DetailFragment.newInstance(resultsItem)

            // Replace the current fragment with the new one
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
    }


    override fun getItemCount(): Int {
        return resultsItemList.size
    }

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
    }
}





