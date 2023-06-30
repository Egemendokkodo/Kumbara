package com.uygulamalarim.kumbara.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.uygulamalarim.kumbara.Activity.ProfileActivity
import com.uygulamalarim.kumbara.Activity.SettingsActivity
import com.uygulamalarim.kumbara.Adapter.RecyclerAdapter
import com.uygulamalarim.kumbara.R

import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {
    private lateinit var savingsrecycler:RecyclerView
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        val navDrawerBtn = toolbar.findViewById<ImageButton>(R.id.navDraBtn)

        drawerLayout = activity?.findViewById(R.id.drawer_layout) as DrawerLayout
        navDrawerBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        requireFragmentManager().beginTransaction().detach(this).attach(this).commit()

        pullData()






        val navigationView = requireActivity().findViewById<NavigationView>(R.id.navigation_view)


        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    val intent=Intent(context, ProfileActivity::class.java)
                    startActivity(intent)

                    true
                }
                R.id.nav_settings -> {
                    val intent=Intent(context, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }







        val searchView: SearchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                recyclerAdapter.filter(newText)
                return true
            }
        })





        newgoalbtn.setOnClickListener{
            changeFragment(AddFragment())

        }




    }


    private fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = getParentFragmentManager().beginTransaction()
        fragmentTransaction.replace(R.id.fragmentHolderFrameLayout, fragment)
        fragmentTransaction.commit()
    }

    fun pullData(){
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.navigation_view)
        val headerView = navigationView.getHeaderView(0)
        val usernameTextView = headerView.findViewById<TextView>(R.id.usernameTv)
        val emailTextView = headerView.findViewById<TextView>(R.id.emailTv)
        val dataList = mutableListOf<Map<String, Any>>()
        val userr = Firebase.auth.currentUser
        userr?.let {
            val email = it.email
            emailTextView.text = email
            val collectionRef = FirebaseFirestore.getInstance().collection("user-data")
            collectionRef.whereEqualTo("email", email.toString())
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documents = querySnapshot.documents

                        for (document in documents) {
                            val currencyName = document.get("currencyName")
                            val currencyIcon = document.get("currencyIcon")
                            val username = document.get("username")
                            usernameTextView.text=username.toString()
                            val goals = document.get("goals") as? Map<String, Map<String, String>>

                            goals?.let {
                                for ((_, goalData) in it) {
                                    val targetAmount = goalData["target-amount"]
                                    val deadline = goalData["deadline"]
                                    val notes = goalData["notes"]
                                    val goalTitle = goalData["goal-title"]
                                    println(goalTitle.toString())
                                    val currentMoney = goalData["current-money"]


                                    mapOf(
                                        "targetAmount" to targetAmount,
                                        "currentMoney" to currentMoney,
                                        "goalTitle" to goalTitle,
                                        "notes" to notes,
                                        "deadline" to deadline,
                                        "currencyIcon" to currencyIcon
                                    )?.let { it1 ->
                                        dataList.add(
                                            it1 as Map<String, Any>

                                        )

                                        savingsrecycler = requireView().findViewById(R.id.savingsrecycler)
                                        recyclerAdapter = RecyclerAdapter(requireContext(), dataList)
                                        savingsrecycler.adapter = recyclerAdapter
                                        savingsrecycler.layoutManager = LinearLayoutManager(requireContext())

                                        if (recyclerAdapter.itemCount<=0) {
                                            nosaving.visibility = View.VISIBLE
                                            yessaving.visibility = View.GONE
                                            animationView.playAnimation()
                                        } else {
                                            nosaving.visibility = View.GONE
                                            yessaving.visibility = View.VISIBLE
                                            animationView.pauseAnimation()
                                        }

                                    }
                                }
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