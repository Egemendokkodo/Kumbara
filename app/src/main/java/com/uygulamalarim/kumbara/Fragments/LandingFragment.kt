package com.uygulamalarim.kumbara.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.uygulamalarim.kumbara.Activity.LoginActivity
import com.uygulamalarim.kumbara.Activity.SignInActivity
import com.uygulamalarim.kumbara.Adapter.CurrencyAdapter
import com.uygulamalarim.kumbara.Model.Currency
import com.uygulamalarim.kumbara.Model.CurrencyList
import com.uygulamalarim.kumbara.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.alertdialoglayout.view.*
import kotlinx.android.synthetic.main.fragment_landing.*
import java.io.BufferedReader
import java.io.InputStreamReader

class LandingFragment : Fragment() {

    private lateinit var pickedCurrency: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesForCurrencyName: SharedPreferences
    private lateinit var sharedPreferencesForCurrencyIcon: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_landing, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        alreadyHave.setOnClickListener {
            val intent=Intent(requireContext(),LoginActivity::class.java)
            startActivity(intent)
        }



        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferencesForCurrencyName = requireActivity().getSharedPreferences("MyPrefs2", Context.MODE_PRIVATE)
        sharedPreferencesForCurrencyIcon = requireActivity().getSharedPreferences("MyPrefs3", Context.MODE_PRIVATE)

        val currencyIcon = sharedPreferencesForCurrencyIcon.getString("currencyIcon", "")
        val currencyName = sharedPreferencesForCurrencyName.getString("currencyName", "")


        if (!currencyIcon.isNullOrEmpty() || !currencyName.isNullOrEmpty()) {
            currencyPicker.setText("$currencyName $currencyIcon")
        }

        val getStarted = view.findViewById<Button>(R.id.getStartedBtn)
        val currencyPicker = view.findViewById<Button>(R.id.currencyPicker)

        currencyPicker.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = LayoutInflater.from(requireContext())
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
                        pickedCurrency = "${selectedCurrency.currencyName} ${selectedCurrency.currencyIcon}"
                        sharedPreferencesForCurrencyName.edit().putString("currencyName", selectedCurrency.currencyName).apply()
                        sharedPreferencesForCurrencyIcon.edit().putString("currencyIcon", selectedCurrency.currencyIcon).apply()
                        currencyPicker.setText(pickedCurrency)
                    }
                }
                setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                setView(dialogLayout)
            }

            recycler.layoutManager = LinearLayoutManager(requireContext())
            recycler.adapter = currencyAdapter

            builder.show()
        }

        getStarted.setOnClickListener {
            //TODO:: SEÇİLMİŞ CURRENCY BURADA
            val currencyIcon = sharedPreferencesForCurrencyIcon.getString("currencyIcon", "$")
            val currencyName = sharedPreferencesForCurrencyName.getString("currencyName", "USD")
            //---
            val intent = Intent(requireContext(), SignInActivity::class.java)
            intent.putExtra("currencyName",currencyName)
            intent.putExtra("currencyIcon",currencyIcon)
            startActivity(intent)
        }
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

