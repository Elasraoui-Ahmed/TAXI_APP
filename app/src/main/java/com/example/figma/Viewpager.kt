package com.example.figma

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager(activity: AppCompatActivity): FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentHome()  // Correct
            1 -> fragmentProfile()  // Correct capitalization
            2 -> LanguageSettingsFragment()  // Correct capitalization
            3 -> ModeToggleFragment()  // Correct capitalization

            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
