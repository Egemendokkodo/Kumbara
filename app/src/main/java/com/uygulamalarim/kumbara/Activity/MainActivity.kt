package com.uygulamalarim.kumbara.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uygulamalarim.kumbara.Fragments.FragmentHolder
import com.uygulamalarim.kumbara.Fragments.LandingFragment
import com.uygulamalarim.kumbara.Fragments.MainFragment
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.fragment_landing.*

class MainActivity : AppCompatActivity() {
        private lateinit var auth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
            auth = Firebase.auth
        changeFragment(LandingFragment())


    }
    fun changeFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()

    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent= Intent(this,FragmentHolder::class.java)
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}