package com.example.collageproject.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collageproject.R
import com.example.collageproject.data.datamodel.Data
import com.example.collageproject.data.datamodel.TemplateImage

class VerticalImageGroupAdapter(private val items: MutableList<Data>, private val onImageClick: (TemplateImage) -> Unit) :
    RecyclerView.Adapter<VerticalImageGroupAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val horizontalRecyclerView: RecyclerView = view.findViewById(R.id.horizontalRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = items[position]
        holder.txtTitle.text = group.collection_title

//        holder.horizontalRecyclerView.apply {
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            adapter = HorizontalImageAdapter(group.data, onImageClick)
//        }
    }

    fun submitList(newItems: List<Data>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}