package com.example.collageproject.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collageproject.core.MultiviewDataItem
import com.example.collageproject.data.model.commonmodel.TemplateItem
import com.example.collageproject.databinding.BannerItemLayoutBinding
import com.example.collageproject.databinding.CarouselListItemLayoutBinding
import com.example.collageproject.databinding.CollageItemListLayoutBinding
import com.example.collageproject.databinding.FullBannerItemLayoutBinding
import com.example.collageproject.databinding.FullScreenBannerItemLayoutBinding
import com.example.collageproject.ui.home.DashboardDataItemType
import com.google.android.material.carousel.CarouselLayoutManager

class DashboardAdapter(private val dataList: List<MultiviewDataItem>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (dataList?.get(position)) {
            is MultiviewDataItem.Carousel -> DashboardDataItemType.CAROUSEL
            is MultiviewDataItem.RecentlyViewed -> DashboardDataItemType.RECENTLY_VIEWED
            is MultiviewDataItem.Banner -> DashboardDataItemType.BANNER
            is MultiviewDataItem.Recommended -> DashboardDataItemType.RECOMMENDED
            is MultiviewDataItem.TopInIndia -> DashboardDataItemType.TOP_IN_INDIA
            is MultiviewDataItem.BigBanner -> DashboardDataItemType.BIG_BANNER
            is MultiviewDataItem.YouMayLike -> DashboardDataItemType.YOU_MAY_LIKE
            is MultiviewDataItem.FullScreenBanner -> DashboardDataItemType.FULL_SCREEN_BANNER
            is MultiviewDataItem.ExploreMore -> DashboardDataItemType.EXPLORE_MORE
            null -> {0}
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DashboardDataItemType.CAROUSEL -> {
                val binding =
                    CarouselListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CarouselItemViewHolder(binding)
            }
            DashboardDataItemType.RECENTLY_VIEWED -> {
                val binding =
                    CollageItemListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CollageItemViewHolder(binding)
            }
            DashboardDataItemType.BANNER -> {
                val binding =
                    BannerItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                BannerItemViewHolder(binding)
            }
            DashboardDataItemType.RECOMMENDED -> {
                val binding =
                    CollageItemListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CollageItemViewHolder(binding)
            }
            DashboardDataItemType.TOP_IN_INDIA -> {
                val binding =
                    CollageItemListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CollageItemViewHolder(binding)
            }
            DashboardDataItemType.BIG_BANNER -> {
                val binding =
                    FullBannerItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FullBannerItemViewHolder(binding)
            }
            DashboardDataItemType.YOU_MAY_LIKE -> {
                val binding =
                    CollageItemListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CollageItemViewHolder(binding)
            }
            DashboardDataItemType.FULL_SCREEN_BANNER -> {
                val binding =
                    FullScreenBannerItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FullScreenBannerItemViewHolder(binding)
            }
            DashboardDataItemType.EXPLORE_MORE -> {
                val binding =
                    CollageItemListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CollageItemViewHolder(binding)
            }

            else -> {throw IllegalArgumentException("Invalid view type")}
        }

    }

    override fun getItemCount(): Int = dataList?.size?:0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = dataList?.get(position)) {
            is MultiviewDataItem.Carousel -> { (holder as CarouselItemViewHolder).bindCarouselItems(item.carousels) }
            is MultiviewDataItem.RecentlyViewed -> { (holder as CollageItemViewHolder).bingCollage(item.recentlyViewed) }
            is MultiviewDataItem.Banner -> {(holder as BannerItemViewHolder).bindBannerView(item.bannerItem) }
            is MultiviewDataItem.Recommended -> { (holder as CollageItemViewHolder).bingCollage(item.recommended) }
            is MultiviewDataItem.TopInIndia ->{ (holder as CollageItemViewHolder).bingCollage(item.topInIndia)}
            is MultiviewDataItem.BigBanner ->{ (holder as FullBannerItemViewHolder).bindFullBanner(item.bigBannerItem)}
            is MultiviewDataItem.YouMayLike ->{ (holder as CollageItemViewHolder).bingCollage(item.youMayLike)}
            is MultiviewDataItem.FullScreenBanner ->{ (holder as FullScreenBannerItemViewHolder).bindFullScreenBannerView(item.fullScreenBanner)}
            is MultiviewDataItem.ExploreMore ->{ (holder as CollageItemViewHolder).bingCollage(item.exploreMore)}
            null -> {}
        }
    }
    inner class CarouselItemViewHolder(private val binding: CarouselListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.carouselListRecyclerView.setHasFixedSize(true)
            binding.carouselListRecyclerView.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)

        }

        fun bindCarouselItems(carouselItemList: List<TemplateItem>) {
            val adapter = CarouselChildAdapter(carouselItemList)
            binding.carouselListRecyclerView.adapter = adapter
        }

    }

    inner class BannerItemViewHolder(private val binding: BannerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindBannerView(banner:TemplateItem?) {
            Glide.with(binding.root.context)
                .load(banner?.image_url)
                .into(binding.bannerIv)
          //  binding.bannerIv.setImageResource(banner.image_url)
        }

    }

    inner class FullScreenBannerItemViewHolder(private val binding: FullScreenBannerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindFullScreenBannerView(fullScreenBanner: TemplateItem?) {
            Glide.with(binding.root.context)
                .load(fullScreenBanner?.image_url)
                .into(binding.fullscreenBannerIv)
           // binding.fullscreenBannerIv.setImageResource(fullScreenBanner.image)
        }

    }
    inner class FullBannerItemViewHolder(private val binding: FullBannerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindFullBanner(fullBanner: TemplateItem?) {
            Glide.with(binding.root.context)
                .load(fullBanner?.image_url)
                .into(binding.fullBannerIv)
//            val adapter = TopShowsAdapter(DataItemType.CLOTHING, topShowsItemList)
//            binding.topShowsRecyclerView.adapter = adapter
        }

    }

    inner class CollageItemViewHolder(private val binding: CollageItemListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.collageListRecyclerView.setHasFixedSize(true)
            binding.collageListRecyclerView.layoutManager =
                LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false)
        }

        fun bingCollage(collageItemList: List<TemplateItem>) {
            val adapter = CollageChildAdapter( collageItemList)
            binding.collageListRecyclerView.adapter = adapter
        }

        }
    }


