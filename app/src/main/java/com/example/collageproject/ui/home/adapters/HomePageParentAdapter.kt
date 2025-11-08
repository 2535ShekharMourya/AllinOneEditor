package com.example.collageproject.ui.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collageproject.R
import com.example.collageproject.data.model.homepagemodel.CollectionsItem
import com.example.collageproject.extensions.logMessage


class HomePageParentAdapter(private val sections: MutableList<List<CollectionsItem>>, val onItemClick: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HORIZONTAL = 0
        private const val TYPE_GRID = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                "TYPE_HORIZONTAL".logMessage()
                TYPE_HORIZONTAL
            }
            1 -> {
                "TYPE_GRID".logMessage()
                TYPE_GRID
            }
            else -> {
                "TYPE_HORIZONTAL".logMessage()
                TYPE_HORIZONTAL
            } // fallback
        }
    }

    inner class HorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val innerRecycler = itemView.findViewById<RecyclerView>(R.id.innerRecyclerView)
        private val collectionTitle = itemView.findViewById<TextView>(R.id.sectionTitle)

        fun bind(collectionsList: List<CollectionsItem>) {
            collectionTitle.text = "Classic"
            "ChildHorizontalAdapter".logMessage()
            innerRecycler.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            innerRecycler.adapter = ChildHorizontalAdapter(collectionsList, onItemClick)
            innerRecycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    rv.parent.requestDisallowInterceptTouchEvent(true)
//                    when (e.action) {
//                        MotionEvent.ACTION_DOWN -> rv.parent.requestDisallowInterceptTouchEvent(true)
//                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> rv.parent.requestDisallowInterceptTouchEvent(false)
//                    }
                    return false
                }
                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
        }
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val innerRecycler = itemView.findViewById<RecyclerView>(R.id.innerRecyclerView)
        private val collectionTitle = itemView.findViewById<TextView>(R.id.sectionTitle)

        fun bind(collectionsList: List<CollectionsItem>) {
            "ChildAdapter".logMessage()
            collectionTitle.text = "Natural"
            innerRecycler.layoutManager =
                GridLayoutManager(itemView.context, 2) // 2 columns
            innerRecycler.adapter = ChildAdapter(collectionsList, onItemClick)
        }
    }

//    inner class CollectionSViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val collectionImage = itemView.findViewById<ImageView>(R.id.collection_image)
//        val collectionName = itemView.findViewById<TextView>(R.id.collection_type)
//        fun bind(item: CollectionsItem) {
//            collectionName.text = item.title
//            Glide.with(itemView.context)
//                .load(item.thumb)
//                .into(collectionImage)
//            itemView.setOnClickListener {
//                item.collection_id?.let { it1 -> onItemClick(it1) }
//            }
//        }
//
//
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HORIZONTAL -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_parent_recycler, parent, false)
                HorizontalViewHolder(view)
            }
            TYPE_GRID -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_parent_recycler, parent, false)
                GridViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val section = sections[position]
        when (holder) {
            is HorizontalViewHolder -> holder.bind(section)
            is GridViewHolder -> holder.bind(section)
        }
    }


    override fun getItemCount(): Int = sections.size
}