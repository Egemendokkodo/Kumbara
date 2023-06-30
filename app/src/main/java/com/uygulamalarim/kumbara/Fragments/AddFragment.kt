package com.uygulamalarim.kumbara.Fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.fragment_add.*
import java.text.SimpleDateFormat
import java.util.*


class AddFragment : Fragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val deadline = view.findViewById<EditText>(R.id.deadline)
        deadline.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    requireActivity(),
                    { _, selectedYear, selectedMonth, selectedDay ->
                        val selectedDate = "$selectedDay/${selectedMonth+1}/$selectedYear"
                        deadline.setText(selectedDate)
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.show()
            }
        }

        saveGoal.setOnClickListener {
            if (goalTitle.text.isNullOrEmpty() || targetAmount.text.isNullOrEmpty()){

                Toast.makeText(requireActivity(), getString(R.string.input_required), Toast.LENGTH_SHORT).show()
            }else{

                //todo:: burada firebase e savingsleri ekle

                val user = Firebase.auth.currentUser
                user?.let {
                    val email = it.email
                    val collectionRef = FirebaseFirestore.getInstance().collection("user-data")
                    collectionRef.whereEqualTo("email", email.toString())
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                for (document in querySnapshot.documents) {
                                    val username = document.getString("username")
                                    val currencyIcon = document.getString("currencyIcon")
                                    val currencyName = document.getString("currencyName")
                                    val email = document.getString("email")


                                    val currentTime = Date().time
                                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                    val formattedTime = sdf.format(currentTime)
                                    val transaction = hashMapOf(
                                        "description" to getString(R.string.you_created_this_on)+"$formattedTime"
                                    )

                                    val goalData = hashMapOf(
                                        "target-amount" to targetAmount.text.toString(),
                                        "deadline" to deadline.text.toString(),
                                        "notes" to notes.text.toString(),
                                        "goal-title" to goalTitle.text.toString(),
                                        "current-money" to "0",
                                        "transactions" to mutableListOf(transaction)
                                    )

                                    val documentRef = collectionRef.document(document.id)
                                    documentRef.update("goals.${goalTitle.text}", goalData)
                                        .addOnSuccessListener {
                                            changeFragment(MainFragment())
                                        }
                                        .addOnFailureListener { exception ->
                                            Toast.makeText(requireContext(), "something went wrong.", Toast.LENGTH_SHORT).show()
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

        backbtn.setOnClickListener{
            changeFragment(MainFragment())
        }




    }



    private fun changeFragment(fragment: Fragment){
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentHolderFrameLayout, fragment)
        fragmentTransaction.commit()
    }




}