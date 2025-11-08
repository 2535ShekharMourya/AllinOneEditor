package com.example.collageproject.ui.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.collageproject.R
import com.example.collageproject.data.model.homepagemodel.CollectionsItem

class HomePageChildAdapter(private val images: List<CollectionsItem>) : RecyclerView.Adapter<HomePageChildAdapter.HorizontalViewHolder>() {

    inner class HorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      //  val imageView: ImageView = itemView.findViewById(R.id.templateImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_template_image, parent, false)
        return HorizontalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
//        val image = images[position]
//        image.
       // holder.imageView.setImageResource(R.drawable.image3)
    }

    override fun getItemCount(): Int = images.size
}
