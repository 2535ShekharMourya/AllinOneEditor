package com.example.collageproject

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.collageproject.databinding.ActivityMainBinding
import com.example.collageproject.extensions.animateClick
import com.example.collageproject.ui.feed.FeedFragment
import com.example.collageproject.ui.home.HomeFragment
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

    }
    private fun initiallyShowHome(){
        // Initially show home fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_container, HomeFragment())
            .commit()
        setSelectedTab("home")
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
        setSelectedTab("feed")
    }
    private fun navigateToHome(){
       // supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_container, HomeFragment())
            .commit()
        setSelectedTab("home")
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_container, fragment)
            .commit()
    }
    private fun setSelectedTab(tab: String) {
        if (tab == "home") {
            bindig.iconHome.setImageResource(R.drawable.ic_home_selected)
            bindig.iconFeed.setImageResource(R.drawable.ic_feed_unselected)
            bindig.labelHome.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_selected))
            bindig.labelFeed.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_unselected))

        } else {
            bindig.iconHome.setImageResource(R.drawable.ic_home_unselected)
            bindig.iconFeed.setImageResource(R.drawable.ic_feed_selected)
            bindig.labelHome.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_unselected))
            bindig.labelFeed.setTextColor(ContextCompat.getColor(this, R.color.bottom_tab_text_selected))
        }
    }
    private fun handleBackPress() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            setSelectedTab("home")  // Make sure to highlight home tab
        } else {
            finish() // or super.onBackPressed() if using older Android
        }
    }


}


