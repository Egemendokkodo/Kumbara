package com.uygulamalarim.kumbara.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.firestore.FirebaseFirestore
import com.uygulamalarim.kumbara.Adapter.GoalListAdapter
import com.uygulamalarim.kumbara.Model.GoalData
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.activity_goal_info.*

class GoalInfoActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: GoalListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_info)

        val goalList = intent.getSerializableExtra("goalList") as List<HashMap<String, Any>>

        val convertedList = goalList.map { goalDataMap ->
            val description = goalDataMap["description"] as String
            GoalData(description)
        }

        listView = findViewById(R.id.listView)
        adapter = GoalListAdapter(this, convertedList)
        listView.adapter = adapter
        backbtn3.setOnClickListener {
            this.finish()
        }
    }
}


