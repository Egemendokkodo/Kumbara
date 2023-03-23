package com.uygulamalarim.kumbara.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.uygulamalarim.kumbara.Database.KumbaraDatabaseHelper
import com.uygulamalarim.kumbara.R
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.reflect.typeOf

class RecyclerAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){



    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val progressBar:ProgressBar=view.findViewById(R.id.progressBar)
        val savingtitle:TextView=view.findViewById(R.id.savingtitle)
        val howmuchsaved:TextView=view.findViewById(R.id.howmuchsaved)
        val howmanyday:TextView=view.findViewById(R.id.howmanyday)
        val howmuchsavebytime:TextView=view.findViewById(R.id.howmuchsavebytime)
        val depositbtn:TextView=view.findViewById(R.id.depositbtn)
        val withdrawbtn:TextView=view.findViewById(R.id.withdrawbtn)
        val infobtn:ImageButton=view.findViewById(R.id.infobtn)
        val editbtn:ImageButton=view.findViewById(R.id.editbtn)
        val deletebtn:ImageButton=view.findViewById(R.id.deletebtn)




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.savingsrecycleritem, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val db = KumbaraDatabaseHelper(context)
        val cursor = db.getAllData()

        if (cursor!!.moveToPosition(position)) {
            holder.savingtitle.text = cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_TITLE))
            holder.howmuchsaved.text = "Currently saved $${cursor.getDouble(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_SAVED_MONEY))} out of $${cursor.getDouble(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_AMOUNT))}"
            if(cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_DEADLINE)).isNullOrEmpty()){
                holder.howmanyday.text ="You have not entered a deadline."
            }else{
                holder.howmanyday.text = "You have until "+cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_DEADLINE))+"(${howManyDaysLeft(cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_DEADLINE)))}) days left."
            }
            holder.progressBar.max=cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_AMOUNT)).toInt()
            holder.progressBar.progress=cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_SAVED_MONEY)).toInt()
        //holder.howmuchsavebytime.text = String.format(cursor.getDouble(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_AMOUNT)), cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_DEADLINE)))
        }



        holder.depositbtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogLayout = inflater.inflate(R.layout.alertdepositlayout, null)
            val enteramount=dialogLayout.findViewById<TextInputEditText>(R.id.enteramount)
            with(builder) {

                setPositiveButton("Confirm") { dialog, which ->
                    if(enteramount.text.isNullOrEmpty()){
                        Toast.makeText(context, "Please enter the amount.", Toast.LENGTH_SHORT).show()
                    }else{

                    }
                }
                setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                setView(dialogLayout)
            }
        }

        holder.withdrawbtn.setOnClickListener {
            // withdraw button click action
        }

        holder.infobtn.setOnClickListener {
            // info button click action
        }

        holder.editbtn.setOnClickListener {
            // edit button click action
        }

        holder.deletebtn.setOnClickListener {
            // delete button click action
        }

        cursor.close()
        db.close()
    }



    override fun getItemCount(): Int {
        val db = KumbaraDatabaseHelper(context)
        return db.getAllData()?.count ?: 0
    }
    fun howManyDaysLeft(date:String):String{
        val today = LocalDate.now()

        val date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/M/yyyy"))
        val daysLeft = ChronoUnit.DAYS.between(today, date)


        return daysLeft.toString()
    }




}