package com.example.collageproject.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.collageproject.data.model.homepagemodel.CollectionsItem

class TabViewPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    var collections:MutableList<CollectionsItem> = mutableListOf()
    override fun getItemCount(): Int = collections.size
    override fun createFragment(position: Int): Fragment{
        return if (position==0){
            HomePageFragment.newInstance(collections)
        }else{
            MoreCollectionsFragment.newInstance(collections[position])
        }

    }
    fun addCollections(collectionsList: List<CollectionsItem>){
        collections.clear()
        this.collections.addAll(collectionsList)
        notifyDataSetChanged()
    }
}