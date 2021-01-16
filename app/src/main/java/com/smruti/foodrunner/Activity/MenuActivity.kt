package com.smruti.foodrunner.Activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smruti.foodrunner.Adapter.AllRestaurantAdapter
import com.smruti.foodrunner.Adapter.OrderAdapter
import com.smruti.foodrunner.R
import com.smruti.foodrunner.database.RestaurantDatabase
import com.smruti.foodrunner.database.RestaurantEntity
import com.smruti.foodrunner.model.MenuItems
import com.smruti.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    lateinit var menuRecyclerView: RecyclerView
    lateinit var menuAdapter: OrderAdapter
    var menuList = arrayListOf<MenuItems>()
    var orderList = arrayListOf<String>()
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var proceedToCartLayout: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var toolbar: Toolbar
    lateinit var addtocart: Button
    var resId =""
    var resName = ""

    var rating =""
    var price=""
    var imageurl=""
    lateinit var imgfav:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        proceedToCartLayout = findViewById(R.id.relativeLayoutProceedToCart)
        layoutManager = LinearLayoutManager(this)
        progressBar = findViewById(R.id.progressBar)
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        menuRecyclerView = findViewById(R.id.recyclermenu)
        addtocart = findViewById(R.id.cart)
        toolbar = findViewById(R.id.tootlbar)

        if (intent != null) {
            resId = intent.getIntExtra("resid", 111).toString()
            resName = intent.getStringExtra("resname")

            rating=intent.getStringExtra("rating")
            price=intent.getStringExtra("costforone")
            imageurl=intent.getStringExtra("imageurl")
        }

       setUpToolbar(resName)
        addtocart.setOnClickListener {
            proceedToCart()
        }

   imgfav=findViewById(R.id.imgfav)
      val restaurantEntity=RestaurantEntity(resId.toInt(),resName,rating,price,imageurl)

            val checkFav = DBAsyncTask(this, restaurantEntity, 1).execute()
            val isFav = checkFav.get()

            if (isFav)
            {
               imgfav.setImageResource(R.drawable.ic_fav)
            }
            else
            {
                imgfav.setImageResource(R.drawable.ic_favourite)
            }
            imgfav.setOnClickListener {
                if (!DBAsyncTask(
                        this,
                        restaurantEntity,
                        1
                    ).execute().get()
                ) {

                    val async =
                        DBAsyncTask(this, restaurantEntity, 2).execute()
                    val result = async.get()
                    if (result) {
                       imgfav.setTag("fav")
                       imgfav.setImageResource(R.drawable.ic_fav)

                    }
                } else {

                    val async = DBAsyncTask(this, restaurantEntity, 3).execute()
                    val result = async.get()

                    if (result) {

                imgfav.setTag("nonfav")
                       imgfav.setImageResource(R.drawable.ic_favourite)
                    }

                }
        }




        if (ConnectionManager().checkConnectivity(this)) {

            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
            val jsonObjectRequest = object :
                JsonObjectRequest(Method.GET, url + resId, null, Response.Listener {
                    progressLayout.visibility = View.GONE

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val menuObject = resArray.getJSONObject(i)
                                val foodItem = MenuItems(
                                    menuObject.getString("id").toInt(),
                                    menuObject.getString("name"),
                                    menuObject.getString("cost_for_one"),
                                    resId.toInt()
                                )
                                menuList.add(foodItem)
                                menuAdapter = OrderAdapter(
                                   this,
                                    menuList,
                                    object : OrderAdapter.OnItemClickListener {
                                        override fun onAddItemClick(menuItems: MenuItems) {
                                            orderList.add((menuItems.menuid).toString())

                                            if (orderList.size > 0) {
                                                proceedToCartLayout.visibility = View.VISIBLE
                                                OrderAdapter.isCartEmpty = false
                                            }
                                        }

                                        override fun onRemoveItemClick(menuItems: MenuItems) {
                                            orderList.remove((menuItems.menuid).toString())

                                            if (orderList.isEmpty()) {
                                                proceedToCartLayout.visibility = View.INVISIBLE
                                                OrderAdapter.isCartEmpty = true
                                            }
                                        }

                                    })

                                val mLayoutManager = LinearLayoutManager(this)
                                menuRecyclerView.layoutManager = mLayoutManager
                                menuRecyclerView.itemAnimator = DefaultItemAnimator()
                                menuRecyclerView.adapter = menuAdapter
                            }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"

                    headers["token"] = "ccf52a609cc3d2"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }





    }
    private fun proceedToCart() {


        val intent = Intent(this, CartActivity::class.java)
        intent.putExtra("resid", resId)
        intent.putExtra("resname", resName)
        intent.putExtra("orderlist", orderList)

        startActivity(intent)

    }
    fun getitemcount2():Int
    {
        return menuAdapter.getitemcount()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when (id) {
            android.R.id.home -> {

                if(getitemcount2()>0)

                {   val builder = AlertDialog.Builder(this)
                    builder.setTitle("Alert")
                        .setMessage("Going back will remove everything from cart")
                        .setPositiveButton("Okay") { _, _ ->
                            val intent =
                                Intent(this@MenuActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                        .setNegativeButton("No") { _, _ ->

                        }
                        .create()
                        .show()


                }
                else{
                    val intent =
                        Intent(this@MenuActivity, MainActivity::class.java)
                    startActivity(intent)
                }

            }

        }

        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
  if(getitemcount2()>0)

  {   val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
            .setMessage("Going back will remove everything from cart")
            .setPositiveButton("Okay") { _, _ ->
                val intent =
                    Intent(this@MenuActivity, MainActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("No") { _, _ ->

            }
            .create()
            .show()


    }
        else{
      val intent =
          Intent(this@MenuActivity, MainActivity::class.java)
      startActivity(intent)
  }

}
    fun setUpToolbar(title: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    class DBAsyncTask(context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {


            when (mode) {

                1 -> {
                    val res: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return res != null
                }

                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }
    }

