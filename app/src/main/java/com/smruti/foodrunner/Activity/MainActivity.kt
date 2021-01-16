package com.smruti.foodrunner.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.smruti.foodrunner.Fragments.*
import com.smruti.foodrunner.R

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frame: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem? = null
    lateinit var textViewcurrentUser: TextView
    lateinit var textViewMobileNumber: TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       val sharedPreferences=getSharedPreferences(getString(R.string.sharedpreferences),Context.MODE_PRIVATE)
        drawerLayout = findViewById(R.id.drawerlayout)
        coordinatorLayout = findViewById(R.id.coordinatorlayout)
        toolbar = findViewById(R.id.tootlbar)
        frame = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationview)
        val headerView=navigationView.getHeaderView(0)
        textViewcurrentUser=headerView.findViewById(R.id.username)
        textViewMobileNumber=headerView.findViewById(R.id.usermobile)
        textViewcurrentUser.text=sharedPreferences.getString("name","john")
        textViewMobileNumber.text="+91-"+sharedPreferences.getString("mobile","2134678990")
        setUpToolbar()
        openHome()



        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null){
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){
                R.id.itemhome-> {
                    openHome()
                    drawerLayout.closeDrawers()

                }
                R.id.favRes -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            Favourites(this)
                        )
                        .commit()

                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.myProfile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame, MyProfile()
                        )
                        .commit()

                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.orderhis -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            OrderHistory()
                        )
                        .commit()

                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FAQs()
                        )
                        .commit()

                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {


                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want exit?")
                        .setPositiveButton("Yes") { _, _ ->
                            sharedPreferences.edit().putBoolean("login",false).apply()
                            val intent=Intent(this ,login::class.java)
                            finish()
                            startActivity(intent)

                        }
                        .setNegativeButton("No") { _, _ ->
                            drawerLayout.closeDrawers()
                        }
                        .create()
                        .show()

                }
            }
            return@setNavigationItemSelectedListener true
        }




    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }




    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    fun openHome(){
        val fragment = Home(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "Home"
        navigationView.setCheckedItem(R.id.itemhome)
    }
    override fun onBackPressed() {

        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when(frag){


            !is Home -> {

            openHome()}

            else -> finishAffinity()
        }
    }
    }

