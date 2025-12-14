package com.example.collageproject.core

import com.example.collageproject.data.model.commonmodel.TemplateItem

sealed class MultiviewDataItem {
    data class Carousel(val carousels: List<TemplateItem>): MultiviewDataItem()
    data class RecentlyViewed(val recentlyViewed: List<TemplateItem>): MultiviewDataItem()
    data class Banner(val bannerItem: TemplateItem?): MultiviewDataItem()
    data class TopInIndia(val topInIndia: List<TemplateItem>): MultiviewDataItem()
    data class BigBanner(val bigBannerItem: TemplateItem?):MultiviewDataItem()
    data class Recommended(val recommended: List<TemplateItem>): MultiviewDataItem()
    data class FullScreenBanner(val fullScreenBanner: TemplateItem?):MultiviewDataItem()
    data class YouMayLike(val youMayLike: List<TemplateItem>): MultiviewDataItem()
    data class ExploreMore(val exploreMore: List<TemplateItem>): MultiviewDataItem()

}
data class CarouselItem(val image: Int)
data class BannerItem(val image: Int)
data class LargeBannerItem(val image: Int)
data class FullScreenBannerItem(val image: Int)
data class RecommendedItem(val image: Int)
data class TopInHindiItem(val image: Int)
data class TopInEnglishItem(val image: Int)
data class PopularInTravelItem(val image: Int)
data class RecentlyViewedItem(val image: Int)
data class YouMayLikeItem(val image: Int)
data class ExploreMoreItem(val image: Int)
