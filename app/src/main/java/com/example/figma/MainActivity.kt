package com.example.figma

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager: ViewPager2 = findViewById(R.id.viewpager)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)

        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = getString(R.string.home)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val adapter = ViewPager(this)
        viewPager.adapter = adapter

        // Disable swipe navigation between fragments
        viewPager.isUserInputEnabled = false

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.profile -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.lang -> {
                    viewPager.currentItem = 2
                    true
                }
                R.id.mode -> {
                    viewPager.currentItem = 3
                    true
                }
                else -> false
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNav.menu.getItem(position).isChecked = true

                when (position) {
                    0 -> toolbarTitle.text = getString(R.string.home)
                    1 -> toolbarTitle.text = getString(R.string.profil_title)
                    2 -> toolbarTitle.text = getString(R.string.lang)
                    3 -> toolbarTitle.text = getString(R.string.mode)
                    else -> toolbarTitle.text = ""
                }
            }
        })
    }
}
