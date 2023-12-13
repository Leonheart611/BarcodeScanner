package dynamia.com.barcodescanner.ui.history.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class HistoryPagerAdapter(lifecycle: Lifecycle, fragmentManager: FragmentManager) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
      return mFragmentList[position]
    }

}