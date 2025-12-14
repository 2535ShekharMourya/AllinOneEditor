package com.example.collageproject

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.collageproject.databinding.ActivityMainBinding
import com.example.collageproject.extensions.animateClick
import com.example.collageproject.ui.feed.FeedFragment
import com.example.collageproject.ui.home.CollageFragment
import com.example.collageproject.ui.home.HomeFragment
import com.example.collageproject.utils.BottomTabs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var bindig: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bindig = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindig.root)
        viewCompact()
        initiallyShowHome()
        onBackPressedDispatcher.addCallback(this) {
            handleBackPress()
        }
        bindig.tabHome.setOnClickListener {
            bindig.tabHome.animateClick()
           navigateToHome()
        }
        bindig.tabFeed.setOnClickListener {
            bindig.tabFeed.animateClick()
           navigateToFeed()
        }
        bindig.tabCollage.setOnClickListener {
            bindig.tabCollage.animateClick()
            navigateToCollage()
        }

    }
    private fun initiallyShowHome(){
        // Initially show home fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_container, HomeFragment())
            .commit()
        setSelectedTab(BottomTabs.HOME)
    }
    private fun viewCompact(){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_navigation)) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, 0, 0, 0)
            insets
        }
    }
    private fun navigateToFeed(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_container, FeedFragment())
            .addToBackStack(null)
            .commit()
        setSelectedTab(BottomTabs.FEED)
    }
    private fun navigateToHome(){
       // supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_container, HomeFragment())
            .commit()
        setSelectedTab(BottomTabs.HOME)
    }
    private fun navigateToCollage(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_container, CollageFragment())
            .commit()
        setSelectedTab(BottomTabs.COLLAGE)
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_container, fragment)
            .commit()
    }
    private fun setSelectedTab(tab: BottomTabs) {
        when (tab) {
            BottomTabs.HOME -> {
                // Handle Home tab
                bindig.iconHome.setImageResource(R.drawable.ic_home_selected)
                bindig.iconFeed.setImageResource(R.drawable.ic_feed_unselected)
                bindig.iconCollage.setImageResource(R.drawable.icon_collage_unselected)
                bindig.labelHome.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_selected))
                bindig.labelFeed.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_unselected))
                bindig.labelCollage.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_unselected))
            }

            BottomTabs.COLLAGE -> {
                // Handle Collage tab
                bindig.iconCollage.setImageResource(R.drawable.icon_collage_selected)
                bindig.iconFeed.setImageResource(R.drawable.ic_feed_unselected)
                bindig.iconHome.setImageResource(R.drawable.ic_home_unselected)
                bindig.labelCollage.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_selected))
                bindig.labelHome.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_selected))
                bindig.labelFeed.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_unselected))
            }

            BottomTabs.FEED -> {
                // Handle Feed tab
                bindig.iconHome.setImageResource(R.drawable.ic_home_unselected)
                bindig.iconCollage.setImageResource(R.drawable.icon_collage_unselected)
                bindig.iconFeed.setImageResource(R.drawable.ic_feed_selected)
                bindig.labelCollage.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_unselected))
                bindig.labelHome.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_unselected))
                bindig.labelFeed.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_selected))
            }
        }
//        if (tab == BottomTabs.HOME) {
//
//
//        } else {
//
//        }
    }
    private fun handleBackPress() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            setSelectedTab(BottomTabs.HOME)  // Make sure to highlight home tab
        } else {
            finish() // or super.onBackPressed() if using older Android
        }
    }


}


