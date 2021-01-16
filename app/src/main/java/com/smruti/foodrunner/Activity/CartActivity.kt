package com.smruti.foodrunner.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smruti.foodrunner.Adapter.CartAdapter
import com.smruti.foodrunner.R
import com.smruti.foodrunner.model.CartItems
import com.smruti.foodrunner.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.MutableMap
import kotlin.collections.arrayListOf
import kotlin.collections.set

class CartActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var CartRecycler: RecyclerView
    lateinit var proceedButton: Button
    lateinit var txtorderfrom: TextView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var cartAdapter: CartAdapter
    var restid = ""
    var userid: String? = ""
    lateinit var sharedPreferences: SharedPreferences
    var restname: String? = ""
     var totalamount=0
    var cartlist = arrayListOf<CartItems>()
    var cartlistid = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        sharedPreferences =
            getSharedPreferences(getString(R.string.sharedpreferences), Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("userid", "0")
        toolbar = findViewById(R.id.cartTool)
        proceedButton = findViewById(R.id.btnplace)
        txtorderfrom = findViewById(R.id.txtOrder)
        CartRecycler = findViewById(R.id.recyclercart)

        layoutManager= LinearLayoutManager(this)
       setSupportActionBar(toolbar)
        supportActionBar?.title="My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent != null) {
            restid = intent.getStringExtra("resid")
            restname = intent.getStringExtra("resname")
            cartlistid = intent.getStringArrayListExtra("orderlist")

        }
        txtorderfrom.text = "Ordering From: ${restname}"

        proceedButton.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this@CartActivity)) {
                val queue = Volley.newRequestQueue(this@CartActivity)
                val url = "http://13.235.250.119/v2/place_order/fetch_result/"

                val foodJsonArray = JSONArray()

                for (i in cartlistid) {
                    val singleItemObject = JSONObject()
                    singleItemObject.put("food_item_id", i)
                    foodJsonArray.put(singleItemObject)


                }
                val sendOrder = JSONObject()

                sendOrder.put("user_id", userid)
                sendOrder.put("restaurant_id", restid)
                sendOrder.put("total_cost", totalamount)
                sendOrder.put("food", foodJsonArray)
                val jsonObjectRequest =

                    object :
                        JsonObjectRequest(Request.Method.POST, url, sendOrder, Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val intent = Intent(this@CartActivity, SuccessOrder::class.java)
                                    startActivity(intent)

                                }
                                else{
                                Toast.makeText(
                                    this@CartActivity,
                                    "some error Occured",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()}
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }, Response.ErrorListener {
                            Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@CartActivity, "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
        }

if(ConnectionManager().checkConnectivity(this))
{
    val queue=Volley.newRequestQueue(this)
    val url="http://13.235.250.119/v2/restaurants/fetch_result/"
    cartlist.clear()
    totalamount=0
    val jsonObjectRequest= object : JsonObjectRequest(Method.GET,url+restid,null,Response.Listener {
        try{val data=it.getJSONObject("data")
        val success=data.getBoolean("success")
        if(success){
            val resArray=data.getJSONArray("data")
            for(i in 0 until resArray.length()){
                val menuObject=resArray.getJSONObject(i)

                    if(cartlistid.contains(menuObject.getString("id")) )
                    {
                        totalamount=totalamount+menuObject.getString("cost_for_one").toString().toInt()
                        val fooditem=CartItems(menuObject.getString("id"),
                        menuObject.getString("name"),
                        menuObject.getString("cost_for_one"),
                        restid)
                        cartlist.add(fooditem)
                    }

                        cartAdapter= CartAdapter(this@CartActivity,cartlist)
                        val mLayoutManager = LinearLayoutManager(this)
                        CartRecycler.layoutManager = mLayoutManager
                        CartRecycler.itemAnimator = DefaultItemAnimator()
                        CartRecycler.adapter = cartAdapter


            }
        }
            proceedButton.text="Proceed Total Amount: Rs."+totalamount+""
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    },Response.ErrorListener {
        Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
    }) { override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        headers["Content-type"] = "application/json"

        headers["token"] = "ccf52a609cc3d2"
        return headers
    }

    }
    queue.add(jsonObjectRequest)

}
else {
    Toast.makeText(this@CartActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
}


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        when(id){
            android.R.id.home->{
                super.onBackPressed()
            }

        }

        return super.onOptionsItemSelected(item)
    }
}