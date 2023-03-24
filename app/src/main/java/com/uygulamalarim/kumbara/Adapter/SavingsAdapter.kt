package com.uygulamalarim.kumbara.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.PorterDuff
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.uygulamalarim.kumbara.Database.KumbaraDatabaseHelper
import com.uygulamalarim.kumbara.Fragments.MainFragment
import com.uygulamalarim.kumbara.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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
        val id:TextView=view.findViewById(R.id.id)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.savingsrecycleritem, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currencyType = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("myString", "")
        val db = KumbaraDatabaseHelper(context)
        val cursor = db.getAllData()


        if (cursor!!.moveToPosition(position)) {
            holder.id.text=cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_ID))
            holder.savingtitle.text = cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_TITLE))
            holder.howmuchsaved.text = "Currently saved ${currencyType}${cursor.getDouble(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_SAVED_MONEY))} out of ${currencyType}${cursor.getDouble(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_AMOUNT))}"
            if(cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_DEADLINE)).isNullOrEmpty()){
                holder.howmanyday.text ="You have not entered a deadline."
            }else{
                holder.howmanyday.text = "You have until "+cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_DEADLINE))+"(${howManyDaysLeft(cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_DEADLINE)))}) days left."
            }
            holder.progressBar.max=cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_AMOUNT)).toInt()
            holder.progressBar.progress=cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_SAVED_MONEY)).toInt()
            if(holder.progressBar.progress!=holder.progressBar.max){
                val color = ContextCompat.getColor(context, R.color.my_color)
                holder.progressBar.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            }else{
                val color = ContextCompat.getColor(context, R.color.my_color2)
                holder.progressBar.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        //holder.howmuchsavebytime.text = "You need to save around ${cursor.getDouble(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_AMOUNT))/howManyDaysLeft(cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_DEADLINE))).toDouble()}/day, ${cursor.getDouble(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_AMOUNT))/(howManyDaysLeft(cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_DEADLINE))).toDouble()/7)}/week, ${cursor.getDouble(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_AMOUNT))/(howManyDaysLeft(cursor.getString(cursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_DEADLINE))).toDouble()/30)}/month."
        }


        holder.depositbtn.setOnClickListener {
            val clickeditem = holder.savingtitle.text.toString()
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogLayout = inflater.inflate(R.layout.alertdepositlayout, null)
            val enteredamount = dialogLayout.findViewById<TextInputEditText>(R.id.enteramount)
            with(builder) {
                setPositiveButton("Confirm") { dialog, which ->
                    if (enteredamount.text.isNullOrEmpty()) {
                        Toast.makeText(context, "Please enter the amount.", Toast.LENGTH_SHORT).show()
                    } else {
                        val convertedamount = enteredamount.text.toString().toDoubleOrNull() ?: 0.0
                        db.depositMoney(clickeditem,convertedamount)
                        notifyDataSetChanged()
                    }
                }
                setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                setView(dialogLayout)
            }
            builder.show()


        }


        holder.withdrawbtn.setOnClickListener {
            val clickeditem = holder.savingtitle.text.toString()
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogLayout = inflater.inflate(R.layout.alertwithdrawlayout, null)
            val enteredamount = dialogLayout.findViewById<TextInputEditText>(R.id.enteramount)
            with(builder) {
                setPositiveButton("Confirm") { dialog, which ->
                    if (enteredamount.text.isNullOrEmpty()) {
                        Toast.makeText(context, "Please enter the amount.", Toast.LENGTH_SHORT).show()
                    } else {
                        val convertedamount = enteredamount.text.toString().toDoubleOrNull() ?: 0.0
                        db.withdrawMoney(clickeditem,convertedamount)
                        notifyDataSetChanged()
                    }
                }
                setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                setView(dialogLayout)
            }
            builder.show()
        }

        holder.infobtn.setOnClickListener {

        }

        holder.editbtn.setOnClickListener {

            val savingsCursor = db.showSavingsById(holder.id.text.toString().toInt())
            if (savingsCursor.moveToFirst()) {
                val oldName = savingsCursor.getString(savingsCursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_TITLE))
                val oldAmount = savingsCursor.getDouble(savingsCursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_AMOUNT))
                val oldDeadline=savingsCursor.getString(savingsCursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_DEADLINE))
                val oldNotes=savingsCursor.getString(savingsCursor.getColumnIndexOrThrow(KumbaraDatabaseHelper.COLUMN_NOTES))

                val builder = AlertDialog.Builder(context)
                val inflater = LayoutInflater.from(context)
                val dialogLayout = inflater.inflate(R.layout.editpagelayout, null)
                val alertDialog = builder.setView(dialogLayout).show()

                val editgoalTitle=dialogLayout.findViewById<TextInputEditText>(R.id.editGoalTitle)
                val editTargetAmount=dialogLayout.findViewById<TextInputEditText>(R.id.edittargetAmount)
                val editDeadline=dialogLayout.findViewById<TextInputEditText>(R.id.editdeadline)
                val editNotes=dialogLayout.findViewById<TextInputEditText>(R.id.editnotes)
                val editGoalBtn=dialogLayout.findViewById<Button>(R.id.editsaveGoal)
                editgoalTitle.text= Editable.Factory.getInstance().newEditable(oldName)
                editTargetAmount.text= Editable.Factory.getInstance().newEditable(oldAmount.toString())
                editDeadline.text= Editable.Factory.getInstance().newEditable(oldDeadline)
                editNotes.text= Editable.Factory.getInstance().newEditable(oldNotes)

                editGoalBtn.setOnClickListener {
                    db.editGoal(holder.id.text.toString().toInt(),editgoalTitle.text.toString(),editTargetAmount.text.toString(),editDeadline.text.toString(),editNotes.text.toString())
                    notifyDataSetChanged()
                    alertDialog.dismiss()
                    Toast.makeText(context, "Edited Successfully", Toast.LENGTH_SHORT).show()

                }





            }

        }

        holder.deletebtn.setOnClickListener {
            val clickeditem = holder.savingtitle.text.toString()
            AlertDialog.Builder(context)
                .setTitle("Are You Sure?")
                .setMessage("This action will delete your saving goal.")
                .setPositiveButton("Yes"){ dialog, which ->
                    db.deleteSavingsByTitle(clickeditem)
                    notifyDataSetChanged()
                    MainFragment()
                }.setNegativeButton("No"){dialog,which->
                    dialog.dismiss()
                }.show()
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

        val date = LocalDate.parse(date, DateTimeFormatter.ofPattern("d/M/yyyy"))
        val daysLeft = ChronoUnit.DAYS.between(today, date)


        return daysLeft.toString()
    }





}