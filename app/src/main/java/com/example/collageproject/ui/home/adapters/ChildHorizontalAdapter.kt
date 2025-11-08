package com.example.collageproject.ui.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collageproject.R
import com.example.collageproject.data.model.homepagemodel.CollectionsItem

class ChildHorizontalAdapter(
    private val items: List<CollectionsItem>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ChildHorizontalAdapter.ChildViewHolder>() {

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.collection_image)
        private val title = itemView.findViewById<TextView>(R.id.txt_title)

        fun bind(item: CollectionsItem) {
            title.text = item.title
            Glide.with(itemView.context).load(item.thumb).into(image)
            itemView.setOnClickListener { item.collection_id?.let { onItemClick(it) } }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_child, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

