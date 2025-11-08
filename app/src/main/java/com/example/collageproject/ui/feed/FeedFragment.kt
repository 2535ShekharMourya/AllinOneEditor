package com.example.collageproject.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.collageproject.CollageApplication
import com.example.collageproject.R
import com.example.collageproject.data.model.commonmodel.TemplateItem
import com.example.collageproject.data.model.feedmodel.Resource
import com.example.collageproject.databinding.FragmentFeedBinding
import com.example.collageproject.extensions.logMessage
import com.example.collageproject.utils.NetworkUtils
import com.example.collageproject.utils.NoInternetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var feedAdapter: FeedPagerAdapter
    private lateinit var binding: FragmentFeedBinding

    private val feedViewModel: FeedViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentFeedBinding.inflate(inflater, container, false)
       // return inflater.inflate(R.layout.fragment_feed, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (NetworkUtils.isInternetAvailable(requireContext())){
            feedViewModel.fetchFeeds()
        }else{
            showNoInternetDialog()
        }

        setupRecyclerView(view)
        feedViewModel.feedState .observe(viewLifecycleOwner) { resource ->
            handleFeedsResource(resource)
        }
        // Initial data fetch
        feedViewModel.fetchFeeds()
    }
    private fun setupRecyclerView(view: View){
        viewPager = view.findViewById(R.id.feed_viewpager)
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        feedAdapter = FeedPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        viewPager.adapter = feedAdapter

    }
    private fun showNoInternetDialog() {
            val dialog = NoInternetDialog.newInstance()
            dialog.setOnRetryClickListener {
                // Handle retry
                retryOperation()
            }
            dialog.show(parentFragmentManager, NoInternetDialog.TAG)
    }

    private fun retryOperation() {
        feedViewModel.fetchFeeds()
    }
    private fun handleFeedsResource(resource: Resource<List<TemplateItem>?>) {
        when (resource) {
            is Resource.Loading -> {
                resource.message.logMessage()
                binding.lottieAnimationViewFeed.visibility = View.VISIBLE
                binding.lottieAnimationViewFeed.playAnimation()
            }
            is Resource.Success -> {
                binding.lottieAnimationViewFeed.visibility = View.GONE
                binding.lottieAnimationViewFeed.pauseAnimation()
                resource.message.logMessage()
                resource.data?.let { feeds -> feedAdapter.submitList(feeds) } }
            is Resource.Error -> {
                resource.message.logMessage()
                binding.lottieAnimationViewFeed.visibility = View.VISIBLE
                binding.lottieAnimationViewFeed.playAnimation()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.lottieAnimationViewFeed.cancelAnimation()
    }

}