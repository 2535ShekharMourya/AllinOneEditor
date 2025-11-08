package com.example.collageproject.ui.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.collageproject.R
import com.example.collageproject.data.model.feedmodel.Resource
import com.example.collageproject.data.model.homepagemodel.AllCollections
import com.example.collageproject.data.model.homepagemodel.CollectionsItem
import com.example.collageproject.databinding.FragmentHomeBinding
import com.example.collageproject.extensions.logMessage
import com.example.collageproject.utils.NetworkUtils
import com.example.collageproject.utils.NoInternetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private val homeViewModel: HomeViewmodel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    lateinit var adapter: TabViewPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (NetworkUtils.isInternetAvailable(requireContext())){
                homeViewModel.fetchCollections()
            }else{ showNOInternetDialog() }
        homeViewModel.collections.observe(viewLifecycleOwner) { collections ->
            handleCollectionsResource(collections)
        }
        tabLayout = view.findViewById(R.id.fragment_quizzes_tabs)
        viewPager = view.findViewById(R.id.view_pager)

    }
    private fun handleCollectionsResource(resource: Resource<AllCollections>) {
        when (resource) {
            is Resource.Loading -> {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            }
            is Resource.Success -> {
                binding.lottieAnimationView.visibility = View.GONE
                binding.lottieAnimationView.pauseAnimation()
                resource.data?.data?.let { collections ->
                    val allTab = collections.filter { it.title.equals("All", ignoreCase = true) }
                    val otherTabs = collections.filterNot { it.title.equals("All", ignoreCase = true) }
                    val reorderedList = allTab + otherTabs
                    "reorderedList: $reorderedList".logMessage()
                    adapter = TabViewPagerAdapter(this)
                    viewPager.adapter = adapter
                    adapter.addCollections(reorderedList)
                    // Attach TabLayout to ViewPager
                    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                        tab.text = reorderedList[position].title
                        val customView = LayoutInflater.from(tabLayout.context)
                            .inflate(R.layout.custom_tab, null)

                        val textView = customView.findViewById<TextView>(R.id.tabText)
                        textView.text = reorderedList[position].title
                        tab.customView = customView
                    }.attach()
                    tabLayout.post {
                        adjustTabMargins(tabLayout)
                    }

                }
            }
            is Resource.Error -> {
                binding.lottieAnimationView.visibility = View.GONE
                binding.lottieAnimationView.pauseAnimation()
            }
        }
    }
    private fun adjustTabMargins(tabLayout: TabLayout) {
        val tabStrip = tabLayout.getChildAt(0) as ViewGroup
        tabStrip.setPadding(0, 0, 0, 0) // remove default padding

        for (i in 0 until tabStrip.childCount) {
            val tabView = tabStrip.getChildAt(i)

            // remove default internal padding
            tabView.setPadding(0, tabView.paddingTop, 0, tabView.paddingBottom)
            tabView.minimumWidth = 0

            // apply even margins
            val lp = tabView.layoutParams as ViewGroup.MarginLayoutParams
            val margin = (6 * tabLayout.resources.displayMetrics.density).toInt()
            lp.marginStart = margin
            lp.marginEnd = margin
            tabView.layoutParams = lp
        }
    }


    fun switchToTab(collectionId: String) {
        val index = adapter.collections.indexOfFirst { it.collection_id == collectionId }

        if (index != -1) {
            binding.viewPager.currentItem = index
            "Switched to tab at index: $index for collectionId: $collectionId".logMessage()
        } else {
            "CollectionId: $collectionId not found in tab list".logMessage()
        }
//        binding.viewPager.currentItem = position
        "collectionId: $collectionId".logMessage()
    }
    private fun showNOInternetDialog() {
            val dialog = NoInternetDialog.newInstance()
            dialog.setOnRetryClickListener {
                // Handle retry
                retryOperation()
            }
            dialog.show(parentFragmentManager, NoInternetDialog.TAG)
    }

    private fun retryOperation() {
        homeViewModel.fetchCollections()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.lottieAnimationView.cancelAnimation()
    }
}