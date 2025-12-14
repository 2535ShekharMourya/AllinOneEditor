package com.example.collageproject.ui.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.collageproject.R
import com.example.collageproject.data.model.feedmodel.Resource
import com.example.collageproject.data.model.homepagemodel.AllCollections
import com.example.collageproject.data.model.homepagemodel.CollectionsItem
import com.example.collageproject.databinding.FragmentHomeBinding
import com.example.collageproject.extensions.logMessage
import com.example.collageproject.ui.home.adapters.DashboardAdapter
import com.example.collageproject.utils.NetworkUtils
import com.example.collageproject.utils.NoInternetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewmodel by activityViewModels()
    private lateinit var dashboardAdapter: DashboardAdapter
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        homeViewModel.getDashboardData(requireContext())
        homeViewModel.dashboardData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    // Update the UI with the data
                    val data = it.data
                    dashboardAdapter = DashboardAdapter(data)
                    binding.mainRecyclerView.adapter = dashboardAdapter
                }

                is Resource.Loading -> {}
                is Resource.Error<*> -> TODO()
            }


        }
    }

    private fun setUpRecyclerView() {
        // binding.mainRecyclerView.setHasFixedSize (true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager (requireContext(), LinearLayoutManager.VERTICAL, false)
    }
    override fun onDestroyView() {
        super.onDestroyView()

    }
}