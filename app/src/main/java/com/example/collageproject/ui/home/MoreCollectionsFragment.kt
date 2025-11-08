package com.example.collageproject.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.collageproject.CollageApplication
import com.example.collageproject.R
import com.example.collageproject.data.model.commonmodel.TemplateItem
import com.example.collageproject.data.model.feedmodel.Resource
import com.example.collageproject.data.model.homepagemodel.CollectionTemplate
import com.example.collageproject.data.model.homepagemodel.CollectionsItem
import com.example.collageproject.databinding.FragmentMoreCollections2Binding
import com.example.collageproject.ui.common.EditActivity
import com.example.collageproject.utils.NetworkUtils
import com.example.collageproject.utils.NoInternetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MoreCollectionsFragment : Fragment(), TemplateClickListener {
    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: CollectionsAdapter
    private var tabCollectionId: CollectionsItem? = null
    private val homeViewModel: HomeViewmodel by viewModels()
    lateinit var binding: FragmentMoreCollections2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tabCollectionId = it.getParcelable(TAB_DATA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoreCollections2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpeRecyclerView(view)
        homeViewModel.collectionTemplates.observe(viewLifecycleOwner) { collectionTemplates ->
            handleCollectionTemplatesResource(collectionTemplates)
        }
        homeViewModel.fetchCollectionTemplates(tabCollectionId?.collection_id)
    }

    private fun handleCollectionTemplatesResource(resource: Resource<CollectionTemplate>) {
        when (resource) {
            is Resource.Loading -> {
                // Handle loading state
                binding.lottieAnimationViewTabs.visibility = View.VISIBLE
                binding.lottieAnimationViewTabs.playAnimation()
            }

            is Resource.Success -> {
                // Handle success state
                binding.lottieAnimationViewTabs.visibility = View.GONE
                binding.lottieAnimationViewTabs.pauseAnimation()
                resource.data?.let { templates ->
                    templates.data?.items?.let { adapter.addTemplates(it) }
                }
            }

            is Resource.Error<*> -> {
                // Handle error state
                binding.lottieAnimationViewTabs.visibility = View.VISIBLE
                binding.lottieAnimationViewTabs.playAnimation()
                val errorMessage = resource.message ?: "Unknown error occurred"
                // Show error message to the user
            }
        }

    }

    fun setUpeRecyclerView(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.category_recycler_view)
        adapter = CollectionsAdapter(requireContext(), this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )    //GridLayoutManager(requireContext(),2)

    }

    override fun onTemplateClick(data: TemplateItem) {
        val intent = Intent(requireContext(), EditActivity::class.java)
        intent.putExtra("feed_template", data)
        startActivity(intent)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.lottieAnimationViewTabs.cancelAnimation()
    }

    companion object {
        private const val TAB_DATA = "tab_data"

        fun newInstance(data: CollectionsItem): MoreCollectionsFragment {
            val fragment = MoreCollectionsFragment()
            val args = Bundle().apply {
                putParcelable(TAB_DATA, data) // Data class needs to implement Parcelable
                // OR use putSerializable if Data implements Serializable
                // putSerializable(ARG_FEED_DATA, data)
            }
            fragment.arguments = args
            return fragment
        }
    }


}