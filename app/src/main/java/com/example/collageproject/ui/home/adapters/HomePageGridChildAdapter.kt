package com.example.collageproject.ui.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.collageproject.R
import com.example.collageproject.data.model.homepagemodel.CollectionsItem


class HomePageGridChildAdapter(private val data: List<CollectionsItem>) : RecyclerView.Adapter<HomePageGridChildAdapter.GridViewHolder>() {

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       // val imageView: ImageView = itemView.findViewById(R.id.)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_template_image, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
     //   holder.imageView.setImageResource(R.drawable.anotheranimalimage)
    }

    override fun getItemCount(): Int = data.size
}