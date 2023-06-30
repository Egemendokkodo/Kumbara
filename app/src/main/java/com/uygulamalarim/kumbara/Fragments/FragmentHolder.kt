package com.uygulamalarim.kumbara.Fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.uygulamalarim.kumbara.Activity.SettingsActivity
import com.uygulamalarim.kumbara.R
import java.util.*

class FragmentHolder : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        setContentView(R.layout.activity_fragment_holder)
        changeFragment(MainFragment())




    }
    fun changeFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentHolderFrameLayout, fragment)
        fragmentTransaction.commit()

    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent= Intent(this,FragmentHolder::class.java)
        startActivity(intent)
        finish()
    }
    private fun setLocale(lang: String) {
        val locale:Locale=Locale(lang)
        Locale.setDefault(locale)
        val config:Configuration=Configuration()
        config.locale=locale
        baseContext.resources.updateConfiguration(config,baseContext.resources.displayMetrics)
        val editor=getSharedPreferences("Settings", MODE_PRIVATE).edit()
        editor.putString("My_Lang",lang)
        editor.apply()

    }
    fun loadLocale(){
        prefs=getSharedPreferences("Settings",Activity.MODE_PRIVATE)
        val language:String= prefs.getString("My_Lang"," ")!!
        setLocale(language)
    }
}