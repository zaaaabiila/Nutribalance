package com.dicoding.appcapstone.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.dicoding.appcapstone.R
import com.dicoding.appcapstone.response.ResultsItem

class HomeArticleAdapter(private val context: Context) :
    RecyclerView.Adapter<HomeArticleAdapter.ViewHolder>() {

    private var itemList: List<ResultsItem> = emptyList()

    fun setItems(items: List<ResultsItem>) {
        itemList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        // Load image using Coil
        holder.itemImage.load(item.thumbnailUrl) {
            placeholder(R.drawable.logonutribalance)
            error(R.drawable.error_image)
            transformations(RoundedCornersTransformation(radius = 8f))
            // Add more customization options as needed
        }

        holder.itemName.text = item.name
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        val itemName: TextView = itemView.findViewById(R.id.itemName)
    }
}
