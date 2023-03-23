package com.uygulamalarim.kumbara.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uygulamalarim.kumbara.Activity.MainActivity
import com.uygulamalarim.kumbara.Adapter.RecyclerAdapter
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.fragment_landing.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_landing.animationView as animationView1

class MainFragment : Fragment() {
    private lateinit var savingsrecycler:RecyclerView
    private lateinit var recyclerAdapter: RecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {




        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savingsrecycler = requireView().findViewById(R.id.savingsrecycler)
        recyclerAdapter = RecyclerAdapter(requireContext())
        savingsrecycler.adapter = recyclerAdapter
        savingsrecycler.layoutManager = LinearLayoutManager(requireContext())

        if(recyclerAdapter.itemCount<=0){
            nosaving.visibility=View.VISIBLE
            yessaving.visibility=View.GONE
            animationView.playAnimation()
        }else{
            nosaving.visibility=View.GONE
            yessaving.visibility=View.VISIBLE
            animationView.pauseAnimation()
        }
        newgoalbtn.setOnClickListener{
            changeFragment(AddFragment())
        }

    }

    private fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = getParentFragmentManager().beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    override fun onPause() {
        super.onPause()
        changeFragment(MainFragment())
    }

    override fun onResume() {
        super.onResume()

    }



}