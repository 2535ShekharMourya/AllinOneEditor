package com.example.collageproject.ui.feed

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.collageproject.CollageApplication
import com.example.collageproject.R
import com.example.collageproject.data.model.commonmodel.TemplateItem
import com.example.collageproject.databinding.FragmentFeedItemBinding
import com.example.collageproject.extensions.animateClick
import com.example.collageproject.ui.common.EditActivity

class FeedItemFragment : Fragment() {
    private var feedData: TemplateItem? = null
    private lateinit var binding: FragmentFeedItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let {
            feedData = it.getParcelable(ARG_FEED_DATA)
        }
        super.onCreate(savedInstanceState)
    }

    companion object {
        private const val ARG_FEED_DATA = "arg_feed_data"

        fun newInstance(data: TemplateItem): FeedItemFragment {
            val fragment = FeedItemFragment()
            val args = Bundle().apply {
                putParcelable(ARG_FEED_DATA, data) // Data class needs to implement Parcelable
                // OR use putSerializable if Data implements Serializable
                // putSerializable(ARG_FEED_DATA, data)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedItemBinding.inflate(inflater, container, false)
        return binding.root
      //  return inflater.inflate(R.layout.fragment_feed_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireContext().applicationContext as CollageApplication
        app.networkMonitor.startMonitoring(
            onLost = { requireActivity().runOnUiThread {
                // Show error UI
                Toast.makeText(requireContext(), "Network Lost FeedItemFragment", Toast.LENGTH_SHORT).show()
            }},
            onRestored = { requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Network Restored FeedItemFragment", Toast.LENGTH_SHORT).show()
                // Restore UI
            } }
        )
        val replaceImages = view.findViewById<LinearLayout>(R.id.replace_images)
        replaceImages.setOnClickListener {
            val intent = Intent(requireContext(), EditActivity::class.java)
            intent.putExtra("feed_template", feedData)
            startActivity(intent)
        }
        replaceImages.animateClick()
        feedData?.let { data ->
            displayFeedContent(data, view)
        }
    }

    fun displayFeedContent(data: TemplateItem, view: View) {
        val imageView = view.findViewById<ImageView>(R.id.template_image_feed)
        Glide.with(requireContext())
            .load(data.image_url)
            .placeholder(R.drawable.dall_e_test_image) // optional
            .into(imageView)
    }
}