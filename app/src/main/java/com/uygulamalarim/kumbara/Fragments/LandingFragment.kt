package com.uygulamalarim.kumbara.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.uygulamalarim.kumbara.Adapter.CurrencyAdapter
import com.uygulamalarim.kumbara.Model.Currency
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.alertdialoglayout.view.*
import java.io.BufferedReader
import java.io.InputStreamReader

class LandingFragment : Fragment() {

    private lateinit var pickedCurrency:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_landing, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val getstarted=view.findViewById<Button>(R.id.getStartedBtn)
        val currencyPicker=view.findViewById<Button>(R.id.currencyPicker)
        currencyPicker.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogLayout = inflater.inflate(R.layout.alertdialoglayout, null)
            val recycler = dialogLayout.findViewById<RecyclerView>(R.id.dialogrecycler)

            val currencyList = readJson()
            val currencyAdapter = CurrencyAdapter(currencyList)

            with(builder) {
                setTitle("Select Preferred Currency")
                setPositiveButton("Confirm") { dialog, which ->
                    val selectedPosition = currencyAdapter.getSelectedPosition()
                    if (selectedPosition != -1) {
                        val selectedCurrency = currencyList[selectedPosition]
                        //TODO: SEÇİLMİŞ CURRENCY BURADA
                        pickedCurrency=selectedCurrency.currencyName+" "+selectedCurrency.currencyIcon
                        currencyPicker.setText(selectedCurrency.currencyName+" "+selectedCurrency.currencyIcon)
                        }
                }
                setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                setView(dialogLayout)
            }

            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = currencyAdapter

            builder.show()




        }




        getstarted.setOnClickListener{
            changeFragment(MainFragment())
        }
    }


    private fun changeFragment(fragment: Fragment){
        val fragmentTransaction = getParentFragmentManager().beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
    private fun readJson(): List<Currency> {
        val inputStream = resources.openRawResource(R.raw.currency)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        val json = stringBuilder.toString()
        val currencyList = Gson().fromJson(json, CurrencyList::class.java)
        return currencyList.data
    }



}
data class CurrencyList(val data: List<Currency>)