package com.uygulamalarim.kumbara.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.uygulamalarim.kumbara.Activity.GoalInfoActivity
import com.uygulamalarim.kumbara.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class RecyclerAdapter(private val context: Context,private val dataList:MutableList<Map<String, Any>>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){

    private val fullDataList: MutableList<Map<String, Any>> = mutableListOf()

    init {
        fullDataList.addAll(dataList)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val progressBar:ProgressBar=view.findViewById(R.id.progressBar)
        val savingtitle:TextView=view.findViewById(R.id.savingtitle)
        val description:TextView=view.findViewById(R.id.description)
        val howmuchsaved:TextView=view.findViewById(R.id.howmuchsaved)
        val howmanyday:TextView=view.findViewById(R.id.howmanyday)
        val howmuchsavebytime:TextView=view.findViewById(R.id.howmuchsavebytime)
        val depositbtn:TextView=view.findViewById(R.id.depositbtn)
        val withdrawbtn:TextView=view.findViewById(R.id.withdrawbtn)
        val infobtn:ImageButton=view.findViewById(R.id.infobtn)
        val editbtn:ImageButton=view.findViewById(R.id.editbtn)
        val deletebtn:ImageButton=view.findViewById(R.id.deletebtn)
        val id:TextView=view.findViewById(R.id.id)
        var itemId: String? = null



    }
    fun filter(query: String) {
        dataList.clear()

        if (query.isEmpty()) {
            dataList.addAll(fullDataList)
        } else {
            val filteredList = fullDataList.filter { data ->
                val goalTitle = data["goalTitle"].toString()
                goalTitle.contains(query, ignoreCase = true)
            }
            dataList.addAll(filteredList)
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.savingsrecycleritem, parent, false)
        return ViewHolder(view).apply {
            infobtn.setOnClickListener {
                val intent = Intent(context, GoalInfoActivity::class.java)
                intent.putExtra("itemId", itemId)
                context.startActivity(intent)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {




        if (position < dataList.size) {






            val data = dataList[position]


            val targetAmount = data["targetAmount"].toString()
            val currentMoney = data["currentMoney"].toString()
            val goalTitle = data["goalTitle"].toString()
            val notes = data["notes"].toString()
            val deadline = data["deadline"].toString()
            val currencyIcon = data["currencyIcon"].toString()

            holder.progressBar.max = targetAmount.toInt()
            holder.progressBar.progress = currentMoney.toInt()

            holder.progressBar.max = targetAmount.toInt()
            holder.progressBar.progress = currentMoney.toInt()
            if (holder.progressBar.progress != holder.progressBar.max) {
                val color = ContextCompat.getColor(context, R.color.my_color)
                holder.progressBar.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            } else {
                val color = ContextCompat.getColor(context, R.color.my_color2)
                holder.progressBar.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
            holder.savingtitle.text = goalTitle
            if (notes.isEmpty()) {
                holder.description.text = " "
            } else {
                holder.description.text = notes
            }

            if (deadline.isEmpty()) {
                holder.howmanyday.text = context.getString(R.string.you_have_not_entered_deadline)
                holder.howmuchsavebytime.text = ""
            } else {
                holder.howmanyday.text = context.getString(R.string.you_have)+" " +howManyDaysLeft(deadline).toString() +"days until"+" " +deadline.toString()
                try{
                    val day = (targetAmount.toInt() - currentMoney.toInt()) / howManyDaysLeft(deadline).toInt()
                    holder.howmuchsavebytime.text = context.getString(R.string.you_need_to_save_around)+" " +day.toString()+context.getString(
                        R.string.day)
                }catch (e:java.lang.ArithmeticException){
                    holder.howmuchsavebytime.text=""
                }



            }
            holder.howmuchsaved.text = context.getString(R.string.currently_saved)+" "+ currencyIcon.toString()+currentMoney.toString()+" "+ context.getString(
                            R.string.out_of)+" "+ currencyIcon.toString()+targetAmount.toString()
        }

        holder.deletebtn.setOnClickListener {
            val selectedData = dataList[position]
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
                                val goals = document.get("goals") as? Map<String, Map<String, String>>

                                goals?.let {
                                    for ((goalId, goalData) in it) {
                                        val targetAmount = goalData["target-amount"]
                                        val deadline = goalData["deadline"]
                                        val notes = goalData["notes"]
                                        val goalTitle = goalData["goal-title"]
                                        val currentMoney = goalData["current-money"]

                                        collectionRef.document(document.id)
                                            .update("goals.$goalId", FieldValue.delete())
                                            .addOnSuccessListener {
                                                dataList.removeAt(position)
                                                notifyItemRemoved(position)
                                                notifyItemRangeChanged(position, dataList.size)
                                            }
                                            .addOnFailureListener { exception ->
                                            }

                                        break
                                    }
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->

                    }
            }
        }


        holder.depositbtn.setOnClickListener {
            var selectedData: MutableMap<String, Any>
            selectedData = mutableMapOf()
            selectedData.clear()
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogLayout = inflater.inflate(R.layout.alertdepositlayout, null)
            val enteredAmount = dialogLayout.findViewById<TextInputEditText>(R.id.enteramount)

            with(builder) {
                setPositiveButton(context.getString(R.string.confirm)) { dialog, which ->
                    if (enteredAmount.text.isNullOrEmpty()) {
                        Toast.makeText(context, context.getString(R.string.please_enter_amount), Toast.LENGTH_SHORT).show()
                    } else {
                        val convertedAmount = enteredAmount.text.toString().toInt()

                        val selectedDataIndex = position
                        selectedData = dataList[selectedDataIndex].toMutableMap()
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
                                            val goals = document.get("goals") as? MutableMap<String, MutableMap<String, Any>>
                                            val currencyName = document.get("currencyName")
                                            val currencyIcon = document.get("currencyIcon")
                                            goals?.let {
                                                var foundGoal = false

                                                for ((goalId, goalData) in it) {
                                                    if (goalData["goal-title"] == selectedData["goalTitle"]) {
                                                        foundGoal = true

                                                        val currentMoney = goalData["current-money"]
                                                        val newCurrentMoneyValue = (currentMoney?.toString()?.toIntOrNull() ?: 0) + convertedAmount


                                                        goals[goalId]?.set("current-money", newCurrentMoneyValue.toString())


                                                        val transactionDescription = "${convertedAmount.toString()}"+currencyIcon+" "+context.getString(
                                                                                                                    R.string.has_been_added)


                                                        val transaction = mutableMapOf<String, Any>()
                                                        transaction["description"] = transactionDescription


                                                        val transactions = goalData["transactions"] as? MutableList<Map<String, Any>>
                                                        transactions?.add(transaction)

                                                        collectionRef.document(document.id)
                                                            .update("goals", goals)
                                                            .addOnSuccessListener {
                                                                selectedData["currentMoney"] = newCurrentMoneyValue.toString()
                                                                dataList[selectedDataIndex] = selectedData
                                                                notifyItemChanged(selectedDataIndex)
                                                            }
                                                            .addOnFailureListener { exception ->

                                                            }
                                                        break
                                                    }
                                                }

                                                if (!foundGoal) {

                                                }
                                            }
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->

                                }
                        }
                    }
                }
                setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                setView(dialogLayout)
            }
            builder.show()
        }





        holder.withdrawbtn.setOnClickListener {
            var selectedData: MutableMap<String, Any>
            selectedData = mutableMapOf()
            selectedData.clear()
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogLayout = inflater.inflate(R.layout.alertdepositlayout, null)
            val enteredAmount = dialogLayout.findViewById<TextInputEditText>(R.id.enteramount)

            with(builder) {
                setPositiveButton(context.getString(R.string.confirm)) { dialog, which ->
                    if (enteredAmount.text.isNullOrEmpty()) {
                        Toast.makeText(context, context.getString(R.string.please_enter_amount), Toast.LENGTH_SHORT).show()
                    } else {
                        val convertedAmount = enteredAmount.text.toString().toInt()

                        val selectedDataIndex = position
                        selectedData = dataList[selectedDataIndex].toMutableMap()
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
                                            val goals = document.get("goals") as? MutableMap<String, MutableMap<String, Any>>
                                            val currencyName = document.get("currencyName")
                                            val currencyIcon = document.get("currencyIcon")
                                            goals?.let {
                                                var foundGoal = false

                                                for ((goalId, goalData) in it) {
                                                    if (goalData["goal-title"] == selectedData["goalTitle"]) {
                                                        foundGoal = true

                                                        val currentMoney = goalData["current-money"]
                                                        val newCurrentMoneyValue = (currentMoney?.toString()?.toIntOrNull() ?: 0) - convertedAmount


                                                        goals[goalId]?.set("current-money", newCurrentMoneyValue.toString())


                                                        val transactionDescription = "${convertedAmount.toString()}"+currencyIcon+" "+context.getString(
                                                            R.string.has_removed)


                                                        val transaction = mutableMapOf<String, Any>()
                                                        transaction["description"] = transactionDescription


                                                        val transactions = goalData["transactions"] as? MutableList<Map<String, Any>>
                                                        transactions?.add(transaction)

                                                        collectionRef.document(document.id)
                                                            .update("goals", goals)
                                                            .addOnSuccessListener {
                                                                selectedData["currentMoney"] = newCurrentMoneyValue.toString()
                                                                dataList[selectedDataIndex] = selectedData
                                                                notifyItemChanged(selectedDataIndex)
                                                            }
                                                            .addOnFailureListener { exception ->

                                                            }
                                                        break
                                                    }
                                                }

                                                if (!foundGoal) {

                                                }
                                            }
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->

                                }
                        }
                    }
                }
                setNegativeButton(context.getString(R.string.cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                setView(dialogLayout)
            }
            builder.show()
        }


        holder.infobtn.setOnClickListener {
            val selectedData: MutableMap<String, Any> = mutableMapOf()

            val selectedDataIndex = position
            selectedData.clear()
            selectedData.putAll(dataList[selectedDataIndex])

            val user = Firebase.auth.currentUser
            user?.let {
                val email = it.email
                val collectionRef = FirebaseFirestore.getInstance().collection("user-data")

                collectionRef.whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val documents = querySnapshot.documents

                            for (document in documents) {
                                val goals = document.get("goals") as? MutableMap<String, MutableMap<String, Any>>

                                goals?.let {
                                    var foundGoal = false

                                    for ((goalId, goalData) in it) {
                                        if (goalData["goal-title"] == selectedData["goalTitle"]) {
                                            foundGoal = true

                                            val transactions = goalData["transactions"] as? MutableList<Map<String, Any>>

                                            transactions?.let {
                                                val intent = Intent(context, GoalInfoActivity::class.java)
                                                intent.putExtra("goalList", ArrayList(it))
                                                context.startActivity(intent)
                                            }
                                            break
                                        }
                                    }

                                    if (!foundGoal) {

                                    }
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->

                    }
            }
        }

        holder.editbtn.setOnClickListener {



            val data = dataList[position]


            var oldName=data["goalTitle"].toString()
            var oldAmount=data["targetAmount"].toString()
            var oldDeadline=data["deadline"].toString()
            var oldNotes=data["notes"].toString()


            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogLayout = inflater.inflate(R.layout.editpagelayout, null)
            val alertDialog = builder.setView(dialogLayout).show()
            alertDialog.setCancelable(true)
            val editgoalTitle=dialogLayout.findViewById<TextInputEditText>(R.id.editGoalTitle)
            val editTargetAmount=dialogLayout.findViewById<TextInputEditText>(R.id.edittargetAmount)
            val editDeadline=dialogLayout.findViewById<TextInputEditText>(R.id.editdeadline)
            val editNotes=dialogLayout.findViewById<TextInputEditText>(R.id.editnotes)
            val editGoalBtn=dialogLayout.findViewById<Button>(R.id.editsaveGoal)
            val editBackButton=dialogLayout.findViewById<ImageButton>(R.id.editbackbtn)
            editgoalTitle.text= Editable.Factory.getInstance().newEditable(oldName)
            editTargetAmount.text= Editable.Factory.getInstance().newEditable(oldAmount.toString())
            editDeadline.text= Editable.Factory.getInstance().newEditable(oldDeadline)
            editNotes.text= Editable.Factory.getInstance().newEditable(oldNotes)

            editBackButton.setOnClickListener {
                alertDialog.dismiss()
            }

            editGoalBtn.setOnClickListener {
                if (editgoalTitle.text.toString().isEmpty() || editTargetAmount.text.toString().isEmpty()) {
                    Toast.makeText(context, "Please input all the required fields.", Toast.LENGTH_SHORT).show()
                } else {
                    var selectedData: MutableMap<String, Any>
                    selectedData = mutableMapOf()
                    selectedData.clear()

                    val selectedDataIndex = position
                    selectedData = dataList[selectedDataIndex].toMutableMap()
                    val newGoalTitle = editgoalTitle.text.toString()
                    val newTargetAmount = editTargetAmount.text.toString()
                    val newDeadline = editDeadline.text.toString()
                    val newNotes = editNotes.text.toString()

                    val user = Firebase.auth.currentUser
                    user?.let { currentUser ->
                        val email = currentUser.email
                        val collectionRef = FirebaseFirestore.getInstance().collection("user-data")

                        collectionRef.whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (!querySnapshot.isEmpty) {
                                    val documents = querySnapshot.documents

                                    for (document in documents) {
                                        val goals = document.get("goals") as? MutableMap<String, MutableMap<String, Any>>

                                        goals?.let { goalData ->
                                            var foundGoal = false

                                            for ((goalId, goalItem) in goalData) {
                                                if (goalItem["goal-title"] == oldName) {
                                                    foundGoal = true


                                                    goalItem["goal-title"] = newGoalTitle
                                                    goalItem["target-amount"] = newTargetAmount
                                                    goalItem["deadline"] = newDeadline
                                                    goalItem["notes"] = newNotes

                                                    val updatedGoals = mutableMapOf<String, Any>()
                                                    updatedGoals.putAll(goalData)

                                                    val documentRef = collectionRef.document(document.id)
                                                    documentRef.update("goals", updatedGoals)
                                                        .addOnSuccessListener {

                                                            selectedData["goalTitle"] = newGoalTitle
                                                            selectedData["targetAmount"] = newTargetAmount
                                                            selectedData["deadline"] = newDeadline
                                                            selectedData["notes"] = newNotes
                                                            dataList[selectedDataIndex] = selectedData
                                                            notifyItemChanged(selectedDataIndex)
                                                            notifyDataSetChanged()
                                                            Toast.makeText(context, "Data updated successfully.", Toast.LENGTH_SHORT).show()
                                                            alertDialog.dismiss()
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Toast.makeText(context, "Error updating data: ${e.message}", Toast.LENGTH_SHORT).show()
                                                        }
                                                    break
                                                }
                                            }

                                            if (!foundGoal) {

                                                Toast.makeText(context, "Goal not found.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                            .addOnFailureListener { exception ->

                                Toast.makeText(context, "Error reading data: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }

                }
            }
    }


    }









    override fun getItemCount(): Int {
        return dataList.size
    }


    fun howManyDaysLeft(date:String):String{
        val today = LocalDate.now()

        val date = LocalDate.parse(date, DateTimeFormatter.ofPattern("d/M/yyyy"))
        val daysLeft = ChronoUnit.DAYS.between(today, date)


        return daysLeft.toString()
    }








}