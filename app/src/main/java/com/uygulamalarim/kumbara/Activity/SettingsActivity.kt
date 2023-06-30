package com.uygulamalarim.kumbara.Activity

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.uygulamalarim.kumbara.Adapter.CurrencyAdapter
import com.uygulamalarim.kumbara.Fragments.FragmentHolder
import com.uygulamalarim.kumbara.Model.Currency
import com.uygulamalarim.kumbara.Model.CurrencyList
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.change_username_dialog_layout.*
import kotlinx.android.synthetic.main.fragment_landing.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale

class SettingsActivity : AppCompatActivity() {
    lateinit var prefs:SharedPreferences

    private lateinit var pickedCurrency: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesForCurrencyName: SharedPreferences
    private lateinit var sharedPreferencesForCurrencyIcon: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        setContentView(R.layout.activity_settings)
        pullData()
        backbtn6.setOnClickListener {
            this.finish()
        }


        backbtn6.setOnClickListener {
            val intent=Intent(this,FragmentHolder::class.java)
            startActivity(intent)
            finish()
        }
        logoutBtn2.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
            this.finish()
        }


        changeCurrency.setOnClickListener {
            changeCurr()
        }
        changeUsernamea.setOnClickListener {

            changeUsername()
        }

        changeLanguage.setOnClickListener {
            Toast.makeText(this, "TILANDI.", Toast.LENGTH_SHORT).show()
            showChangeLanguageDialog()
        }




    }


    fun showChangeLanguageDialog(){
        val list = listOf<String>("English", "Portuguese", "Türkçe")
        val mBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        mBuilder.setTitle(getString(R.string.change_language))
        with(mBuilder){
            setSingleChoiceItems(list.toTypedArray(), -1) { dialogInterface, i ->
                if (i==0){
                    setLocale("En")
                    recreate()
                }
                else if (i==1){
                    setLocale("Pt")
                    recreate()
                }
                else if (i==2){
                    setLocale("Tr")
                    recreate()
                }
                dialogInterface.dismiss()
            }

        }
        val mDialog:AlertDialog=mBuilder.create()
        mDialog.show()

    }

    fun changeUsername(){

        val builder = android.app.AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogLayout = inflater.inflate(R.layout.change_username_dialog_layout, null)
        val etNewUsername = dialogLayout.findViewById<TextInputEditText>(R.id.enterNewUsername)

        with(builder) {
            setTitle(getString(R.string.please_enter_the_new_username))
            setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                if (!etNewUsername.text.toString().isEmpty()) {

                    if (etNewUsername.text.toString().length in 5..12) {
                        val userr = Firebase.auth.currentUser
                        userr?.let {
                            val email = it.email
                            val collectionRef = FirebaseFirestore.getInstance().collection("user-data")

                            collectionRef.whereEqualTo("email", email)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) {
                                        val documents = querySnapshot.documents

                                        for (document in documents) {
                                            document.reference.update("username", etNewUsername.text.toString())
                                                .addOnSuccessListener {
                                                    val intent=Intent(context,FragmentHolder::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                }.addOnFailureListener { exception ->
                                                    // Update failed
                                                    // Handle the failure case here
                                                }
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    // Query failed
                                    // Handle the failure case here
                                }
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.error3), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, getString(R.string.input_required), Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            setView(dialogLayout)
        }

        builder.show()


    }

    private fun changeCurr() {

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferencesForCurrencyName = getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE)
        sharedPreferencesForCurrencyIcon = getSharedPreferences("MyPrefs3", Context.MODE_PRIVATE)







            val builder = android.app.AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val dialogLayout = inflater.inflate(R.layout.alertdialoglayout, null)
            val recycler = dialogLayout.findViewById<RecyclerView>(R.id.dialogrecycler)
            val currencyList = readJson()
            val currencyAdapter = CurrencyAdapter(currencyList)

            with(builder) {
                setTitle(getString(R.string.select_preferred_currency))
                setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                    val selectedPosition = currencyAdapter.getSelectedPosition()
                    if (selectedPosition != -1) {
                        val selectedCurrency = currencyList[selectedPosition]
                        pickedCurrency = "${selectedCurrency.currencyName} ${selectedCurrency.currencyIcon}"
                        sharedPreferencesForCurrencyName.edit().putString("currencyName", selectedCurrency.currencyName).apply()
                        sharedPreferencesForCurrencyIcon.edit().putString("currencyIcon", selectedCurrency.currencyIcon).apply()


                        val userr = Firebase.auth.currentUser
                        userr?.let {
                            val email = it.email
                            val collectionRef = FirebaseFirestore.getInstance().collection("user-data")

                            collectionRef.whereEqualTo("email", email)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) {
                                        val documents = querySnapshot.documents

                                        for (document in documents) {


                                            document.reference.update(
                                                "currencyName", selectedCurrency.currencyName,
                                                "currencyIcon", selectedCurrency.currencyIcon
                                            ).addOnSuccessListener {

                                            }.addOnFailureListener { exception ->

                                            }

                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->

                                }
                        }



                    }
                }
                setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                setView(dialogLayout)
            }

            recycler.layoutManager = LinearLayoutManager(this)
            recycler.adapter = currencyAdapter

            builder.show()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent=Intent(this,FragmentHolder::class.java)
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
    private fun readJson(): List<Currency> {
        val inputStream = resources.openRawResource(R.raw.currency)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        val json = stringBuilder.toString()
        val currencyList = Gson().fromJson(json, CurrencyList::class.java)
        return currencyList.data
    }

    fun pullData(){


            val usernameTextView = findViewById<TextView>(R.id.usernameSettings)
            val emailTextView = findViewById<TextView>(R.id.emailSettings)
            val dataList = mutableListOf<Map<String, Any>>()
            val userr = Firebase.auth.currentUser
            userr?.let {
                val email = it.email
                emailTextView.text = email
                val collectionRef = FirebaseFirestore.getInstance().collection("user-data")
                collectionRef.whereEqualTo("email", email.toString())
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val documents = querySnapshot.documents

                            for (document in documents) {
                                val currencyName = document.get("currencyName")
                                val currencyIcon = document.get("currencyIcon")
                                val username = document.get("username")
                                usernameTextView.text=username.toString()
                                val goals = document.get("goals") as? Map<String, Map<String, String>>
                                var total=0
                                goals?.let {
                                    for ((_, goalData) in it) {
                                        val targetAmount = goalData["target-amount"]
                                        val deadline = goalData["deadline"]
                                        val notes = goalData["notes"]
                                        val goalTitle = goalData["goal-title"]
                                        println(goalTitle.toString())
                                        val currentMoney = goalData["current-money"]


                                        total+=currentMoney!!.toInt()




                                    }
                                }


                            }

                        } else {

                        }
                    }
                    .addOnFailureListener { exception ->

                    }
            }


    }
}