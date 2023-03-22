package com.uygulamalarim.kumbara.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uygulamalarim.kumbara.Activity.MainActivity
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.fragment_landing.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newgoalbtn.setOnClickListener{
            changeFragment(AddFragment())
        }

    }

    private fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = getParentFragmentManager().beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }


}