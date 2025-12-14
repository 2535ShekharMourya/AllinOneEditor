package com.example.collageproject.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collageproject.data.model.commonmodel.TemplateItem
import com.example.collageproject.databinding.CollageItemBinding

class CollageChildAdapter(private val collageItemList: List<TemplateItem>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class CollageViewHolder(private val binding: CollageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindCollageView(recyclerItem: TemplateItem) {
            Glide.with(binding.root.context).load(recyclerItem.image_url).into(binding.collageTemplateImage)
//            binding.bestSellerImage.setImageResource(recyclerItem.image)
//            binding.bestSellerText.text = recyclerItem.offer
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = CollageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollageViewHolder(binding)


    }

    override fun getItemCount(): Int {
        return collageItemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val recyclerItem = collageItemList[position]
        (holder as CollageViewHolder).bindCollageView(recyclerItem)
    }
}