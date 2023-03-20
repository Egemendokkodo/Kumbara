package com.uygulamalarim.kumbara.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.uygulamalarim.kumbara.Fragments.LandingFragment
import com.uygulamalarim.kumbara.Fragments.MainFragment
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.fragment_landing.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeFragment(LandingFragment())


    }
    fun changeFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}