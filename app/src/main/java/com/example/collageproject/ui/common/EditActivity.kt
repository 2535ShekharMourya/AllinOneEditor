package com.example.collageproject.ui.common

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.collageproject.R
import com.example.collageproject.data.model.commonmodel.TemplateItem


class EditActivity : AppCompatActivity() {
    var feedData: TemplateItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit)
        feedData = intent.getParcelableExtra("feed_template")
        val fragment = ImageCollageFragment()
        val bundle = Bundle().apply {
            putParcelable("feed_template", feedData)
        }
        fragment.arguments = bundle

        loadFragment(fragment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.edit_fragment_container, fragment)
            .commit()
    }
}