package com.example.growinganchovyman

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val pageTitles = listOf("Page 1", "Page 2", "Page 3")

    override fun getItemCount(): Int = pageTitles.size

    override fun createFragment(position: Int): Fragment {
        // 각 페이지에 맞는 프래그먼트를 생성합니다.
        return when (position) {
            0 -> PageFragment.newInstance("페이지 1 콘텐츠")
            1 -> PageFragment.newInstance("페이지 2 콘텐츠")
            2 -> PageFragment.newInstance("페이지 3 콘텐츠")
            else -> PageFragment.newInstance("기본 콘텐츠")
        }
    }
}