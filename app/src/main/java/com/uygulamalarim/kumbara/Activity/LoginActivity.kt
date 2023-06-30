package com.uygulamalarim.kumbara.Activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.uygulamalarim.kumbara.Fragments.FragmentHolder
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {




    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        backbtn.setOnClickListener {
            this.finish()
        }

        loginBtn.setOnClickListener {
            auth.signInWithEmailAndPassword(loginEmail.text.toString(), loginPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val intent =Intent(this,FragmentHolder::class.java)
                        startActivity(intent)
                        this.finish()

                    } else {
                        showSnackbar(getString(R.string.error6),0xffb00020.toInt())
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

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