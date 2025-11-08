package com.example.collageproject.ui.feed

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.collageproject.data.model.commonmodel.TemplateItem
import kotlin.collections.mutableListOf

class FeedPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
    var feedList: MutableList<TemplateItem> = mutableListOf()
    override fun createFragment(position: Int): Fragment {
        // Create fragment with data for specific position
        return FeedItemFragment.newInstance(feedList[position])
    }
    override fun getItemCount(): Int {
        return feedList.size
    }
    fun submitList(newList: List<TemplateItem>) {
        feedList.clear()
        feedList.addAll(newList)
        notifyDataSetChanged()
    }
}