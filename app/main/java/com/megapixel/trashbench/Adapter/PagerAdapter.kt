package com.megapixel.trashbench.Adapter

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.megapixel.trashbench.Fragments.ContentFragment
import com.megapixel.trashbench.Fragments.MessagesFragment
import com.megapixel.trashbench.Fragments.ProfileFragment
import com.megapixel.trashbench.R

class PagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    var fragments: ArrayList<Fragment> = arrayListOf(
        ContentFragment(),
        MessagesFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}