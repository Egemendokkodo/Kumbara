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
import com.uygulamalarim.kumbara.Database.KumbaraDatabaseHelper
import com.uygulamalarim.kumbara.Database.KumbaraDatabaseHelper.Companion.COLUMN_AMOUNT
import com.uygulamalarim.kumbara.Database.KumbaraDatabaseHelper.Companion.COLUMN_DEADLINE
import com.uygulamalarim.kumbara.Database.KumbaraDatabaseHelper.Companion.COLUMN_ID
import com.uygulamalarim.kumbara.Database.KumbaraDatabaseHelper.Companion.COLUMN_NOTES
import com.uygulamalarim.kumbara.Database.KumbaraDatabaseHelper.Companion.COLUMN_SAVED_MONEY
import com.uygulamalarim.kumbara.Database.KumbaraDatabaseHelper.Companion.COLUMN_TITLE
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.fragment_add.*
import java.util.*


class AddFragment : Fragment(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = KumbaraDatabaseHelper(requireActivity())

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
                val cursor = db.getAllData()

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val idIndex = cursor.getColumnIndex(COLUMN_ID)
                        val id = if (idIndex != -1) cursor.getInt(idIndex) else -1

                        val titleIndex = cursor.getColumnIndex(COLUMN_TITLE)
                        val title = if (titleIndex != -1) cursor.getString(titleIndex) else null

                        val amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT)
                        val amount = if (amountIndex != -1) cursor.getDouble(amountIndex) else -1.0

                        val deadlineIndex = cursor.getColumnIndex(COLUMN_DEADLINE)
                        val deadline = if (deadlineIndex != -1) cursor.getString(deadlineIndex) else null

                        val notesIndex = cursor.getColumnIndex(COLUMN_NOTES)
                        val notes = if (notesIndex != -1) cursor.getString(notesIndex) else null

                        val savedMoneyIndex = cursor.getColumnIndex(COLUMN_SAVED_MONEY)
                        val savedMoney = if (savedMoneyIndex != -1) cursor.getDouble(savedMoneyIndex) else -1.0


                        Log.d("DATABASE RESPONSE", "ID: $id, Title: $title, Amount: $amount, Deadline: $deadline, Notes: $notes, Saved Money: $savedMoney")
                    } while (cursor.moveToNext())
                }

                cursor?.close()
                Toast.makeText(requireActivity(), "Please input all the required fields", Toast.LENGTH_SHORT).show()
            }else{

                db.insertData(goalTitle.text.toString(),targetAmount.text.toString(), deadline.text.toString(),notes.text.toString())
                val cursor = db.getAllData()

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val idIndex = cursor.getColumnIndex(COLUMN_ID)
                        val id = if (idIndex != -1) cursor.getInt(idIndex) else -1

                        val titleIndex = cursor.getColumnIndex(COLUMN_TITLE)
                        val title = if (titleIndex != -1) cursor.getString(titleIndex) else null

                        val amountIndex = cursor.getColumnIndex(COLUMN_AMOUNT)
                        val amount = if (amountIndex != -1) cursor.getDouble(amountIndex) else -1.0

                        val deadlineIndex = cursor.getColumnIndex(COLUMN_DEADLINE)
                        val deadline = if (deadlineIndex != -1) cursor.getString(deadlineIndex) else null

                        val notesIndex = cursor.getColumnIndex(COLUMN_NOTES)
                        val notes = if (notesIndex != -1) cursor.getString(notesIndex) else null

                        val savedMoneyIndex = cursor.getColumnIndex(COLUMN_SAVED_MONEY)
                        val savedMoney = if (savedMoneyIndex != -1) cursor.getDouble(savedMoneyIndex) else -1.0


                        Log.d("DATABASE RESPONSE", "ID: $id, Title: $title, Amount: $amount, Deadline: $deadline, Notes: $notes, Saved Money: $savedMoney")
                    } while (cursor.moveToNext())
                }

                cursor?.close()
                changeFragment(MainFragment())
            }

        }

        backbtn.setOnClickListener{
            changeFragment(MainFragment())
        }



    }



    private fun changeFragment(fragment: Fragment){
        val fragmentTransaction = getParentFragmentManager().beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }


}