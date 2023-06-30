package com.uygulamalarim.kumbara.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.uygulamalarim.kumbara.Model.GoalData

class GoalListAdapter(private val context: Context, private val dataList: List<GoalData>) : BaseAdapter() {

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val goalData: GoalData = getItem(position) as GoalData

        val textView: TextView = view.findViewById(android.R.id.text1)
        textView.text = goalData.description
        textView.textSize = 18f


        return view
    }

}
