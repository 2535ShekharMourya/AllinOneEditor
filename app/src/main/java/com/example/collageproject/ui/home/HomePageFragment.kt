package com.example.collageproject.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collageproject.CollageApplication
import com.example.collageproject.data.model.homepagemodel.CollectionsItem
import com.example.collageproject.databinding.FragmentHomePageBinding
import com.example.collageproject.extensions.logMessage
import com.example.collageproject.ui.home.adapters.HomePageParentAdapter

class HomePageFragment : Fragment() {
    lateinit var binding: FragmentHomePageBinding
    private var collections: List<CollectionsItem>? = null
    private var collectionList: MutableList<List<CollectionsItem>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            collections = it.getParcelableArrayList("collections")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
        /// return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        tempCollections = collections?.mapIndexed { index, item ->
//            val newType = when (index) {
//                1,2 -> "classic"
//                3,4,5 -> "popular"
//                6 -> "trending"
//                else -> item.type // keep original or set to null/other
//            }
//            item.copy(type = newType)
//        }?.toMutableList()

        val newList: List<CollectionsItem>? = collections?.filter { !it.type.equals("AllCollection") }
        "newList: ${newList?.size} and $newList".logMessage()
        collectionList = mutableListOf()

        val classicCollectionList= newList?.filter { it.type.equals("Classic") }
        val naturalCollectionList = newList?.filter { it.type.equals("Natural") }
        if (classicCollectionList != null  && naturalCollectionList!= null) {
            collectionList?.add(classicCollectionList)
            collectionList?.add(naturalCollectionList)
        }
"collectionList: ${collectionList?.size} and $collectionList".logMessage()

        binding.verticalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.verticalRecyclerView.adapter = collectionList?.let { HomePageParentAdapter(it){collectionId ->
            (parentFragment as? HomeFragment)?.switchToTab(collectionId)
        } }



        "Home Page Fragment collection list: ${collections?.size}, and ${collections}".logMessage()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomePageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(collections: List<CollectionsItem>): HomePageFragment {
            val fragment = HomePageFragment()
            val args = Bundle()
            args.putParcelableArrayList("collections", ArrayList(collections))
            fragment.arguments = args
            return fragment
        }
    }
}