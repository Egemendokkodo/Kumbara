package com.uygulamalarim.kumbara.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.uygulamalarim.kumbara.Adapter.RecyclerAdapter
import com.uygulamalarim.kumbara.Fragments.FragmentHolder
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_main.*

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        pullData()
        backbtn4.setOnClickListener {
            val intent= Intent(this,FragmentHolder::class.java)
            startActivity(intent)
            this.finish()
        }
        logoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
            this.finish()

        }


    }
    fun pullData(){

        val usernameTextView = findViewById<TextView>(R.id.usernameProfile)
        val emailTextView = findViewById<TextView>(R.id.emailProfile)
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
                            if (!goals.isNullOrEmpty()){
                                howmanyGoal.text=getString(R.string.from_your)+" "+ goals!!.size +" "+getString(R.string.goals_in_total)
                                howmuchTotal.text=getString(R.string.you_have_made)+" "+currencyIcon.toString()+total.toString()
                            }else{
                                howmanyGoal.text=getString(R.string.no_saving_goals_set)
                                howmuchTotal.text=""
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