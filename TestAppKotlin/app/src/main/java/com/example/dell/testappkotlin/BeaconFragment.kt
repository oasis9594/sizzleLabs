package com.example.dell.testappkotlin


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_beacon.*
import android.support.v4.view.ViewPager
import android.util.Log
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*


class BeaconFragment : Fragment() {


    val TAG="Movie.TAG"
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_beacon, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        setupViewPager()
        if(tabLayout!=null&&mViewPager!=null)
        tabLayout.setupWithViewPager(mViewPager)
    }

    private fun setupViewPager() {
        val adapter = SectionsPagerAdapter(activity.supportFragmentManager)
        adapter.addFragment(BeaconListFragment())
        adapter.addFragment(AllBeacons())
        mViewPager.adapter = adapter
    }

    fun initToolbar()
    {
        activity.toolbar.hideOverflowMenu()
        activity.toolbar.setTitleTextColor(Color.WHITE);
        activity.toolbar.title = "Beacons"
        Log.d(TAG, "Beacons")
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val mFragmentList = ArrayList<Fragment>()
        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return mFragmentList.get(position)
        }

        override fun getCount(): Int {
            // Show 2 total pages.
            return 2
        }
        fun addFragment(fragment : Fragment)
        {
            mFragmentList.add(fragment)
        }
        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "Nearby"
                1 -> return "All"
            }
            return null
        }
    }
}
