package com.uygulamalarim.kumbara.Activity

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.uygulamalarim.kumbara.Fragments.FragmentHolder
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {


    val db = FirebaseFirestore.getInstance()
    val collectionRef = db.collection("user-data")

    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        auth = Firebase.auth

        backbtn2.setOnClickListener {
            this.finish()
        }
        val signInBtn:Button=findViewById(R.id.signInBtn)

        signInBtn.setOnClickListener {

            if (enterUsername.text.isNullOrEmpty()||enterEmail.text.isNullOrEmpty()||enterPassword.text.isNullOrEmpty()||repeatPassword.text.isNullOrEmpty()){
                showSnackbar(getString(R.string.error1),0xffb00020.toInt())
            }else{
                if(!enterPassword.text.toString().equals(repeatPassword.text.toString())){
                    showSnackbar(getString(R.string.error2),0xffb00020.toInt())
                }
                else if (enterUsername.text.toString().length>12 ||enterUsername.text.toString().length<5){
                    showSnackbar(getString(R.string.error3),0xffb00020.toInt())
                }else if(enterUsername.text.toString().contains(Regex("[@;<>+/\\[\\]{}()]"))){
                    showSnackbar("Your username cannot contain an of @;<>+/\\[\\]{}()",0xffb00020.toInt())
                }else {




                    val userEmail = enterEmail.text.toString()

                    collectionRef.whereEqualTo("email", userEmail)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) {
                                val user = hashMapOf(
                                    "username" to enterUsername.text.toString(),
                                    "email" to userEmail,
                                    "password" to enterPassword.text.toString(),
                                    "currencyName" to intent.getStringExtra("currencyName"),
                                    "currencyIcon" to intent.getStringExtra("currencyIcon")
                                )

                                collectionRef.add(user)
                                    .addOnSuccessListener { documentReference ->
                                        val docId = documentReference.id


                                        auth.createUserWithEmailAndPassword(enterEmail.text.toString(), enterPassword.text.toString())
                                            .addOnCompleteListener(this) { task ->
                                                if (task.isSuccessful) {

                                                    val user = auth.currentUser
                                                    val intent = Intent(this,FragmentHolder::class.java)
                                                    startActivity(intent)
                                                    this.finish()
                                                } else {

                                                    Toast.makeText(
                                                        baseContext,
                                                        "Authentication failed.",
                                                        Toast.LENGTH_SHORT,
                                                    ).show()

                                                }
                                            }




                                    }
                                    .addOnFailureListener { e ->
                                        e.printStackTrace()
                                    }
                            } else {
                                showSnackbar(getString(R.string.error5),0xffb0002e.toInt())
                            }
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }


                }
            }


        }
    }
    private fun showSnackbar(message:String,color:Int){
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(color)

            .show()
    }

}