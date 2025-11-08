package com.example.collageproject.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collageproject.R
import com.example.collageproject.data.model.commonmodel.TemplateItem

class CollectionsAdapter(val context: Context, val templateClickListener: TemplateClickListener): RecyclerView.Adapter<CollectionsAdapter.ViewHolder>() {
    var templatesDataList: MutableList<TemplateItem> = mutableListOf()
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val templateImage = itemView.findViewById<ImageView>(R.id.template_image)
       val noOfPhotos = itemView.findViewById<TextView>(R.id.txt_noOfPhotos)
//        val txtPhoto = itemView.findViewById<TextView>(R.id.txt_photo)

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CollectionsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collections_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionsAdapter.ViewHolder, position: Int) {
        val item = templatesDataList[position]
        Glide.with(context)
            .load(item.image_url)
            .placeholder(R.drawable.dall_e_test_image)
            .into(holder.templateImage)
        holder.noOfPhotos.text = item.image_coordinates?.size.toString()
//        holder.txtPhoto.text = "Photos"
        holder.itemView.setOnClickListener {
            templateClickListener.onTemplateClick(item)
        }
    }

    override fun getItemCount(): Int {
        return templatesDataList.size
    }
    fun addTemplates(newTemplates: List<TemplateItem>){
        templatesDataList.clear()
        templatesDataList.addAll(newTemplates)
        notifyDataSetChanged()
    }
}