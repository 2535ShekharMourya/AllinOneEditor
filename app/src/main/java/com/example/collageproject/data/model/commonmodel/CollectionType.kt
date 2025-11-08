package com.example.collageproject.data.model.commonmodel

import com.example.collageproject.data.model.homepagemodel.CollectionsItem

data class CollectionType(val type:String, val title:String,val collectionId:String,
                          val classic:List<CollectionsItem>?= null,
                          val popular:List<CollectionsItem>?= null,
                          val trending:List<CollectionsItem>?= null)
