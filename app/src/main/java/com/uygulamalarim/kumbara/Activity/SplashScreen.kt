package com.uygulamalarim.kumbara.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.savingsrecycleritem.*

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)




        animationView.alpha = 0f
        animationView.animate().setDuration(1500).alpha(1f).withEndAction{
               val intent = Intent(this@SplashScreen, MainActivity::class.java)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            startActivity(intent)
            finish()
            }
        }



}