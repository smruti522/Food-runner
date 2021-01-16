package com.smruti.foodrunner.Fragments

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smruti.foodrunner.Adapter.OrderHistoryAdapter

import com.smruti.foodrunner.R
import com.smruti.foodrunner.model.CartItems
import com.smruti.foodrunner.model.OrderHistoryRestaurant
import com.smruti.foodrunner.util.ConnectionManager
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 */
class OrderHistory : Fragment() {
    lateinit var layoutManager1: RecyclerView.LayoutManager
    lateinit var orderHistoryAdapter: OrderHistoryAdapter
    lateinit var recyclerOrderHistory: RecyclerView
  lateinit var  llHasOrders:RelativeLayout

var orderHistoryList= arrayListOf<OrderHistoryRestaurant>()
    lateinit var order_activity_history_Progressdialog: RelativeLayout
    lateinit var order_history_fragment_no_orders: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        recyclerOrderHistory = view.findViewById(R.id.recyclerViewAllOrders)

    llHasOrders=view.findViewById(R.id.relativelayout)
        order_activity_history_Progressdialog =
            view.findViewById(R.id.order_activity_history_Progressdialog)
        layoutManager1= LinearLayoutManager(activity)
  val sharedPreferences=(activity as Context).getSharedPreferences(getString(R.string.sharedpreferences),Context.MODE_PRIVATE)
        order_history_fragment_no_orders = view.findViewById(R.id.order_history_fragment_no_orders)
        val userid=sharedPreferences.getString("userid","000")
        sendServerRequest(userid)
        return view

    }
    private fun sendServerRequest(userId: String?) {
        val queue = Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/orders/fetch_result/"
        val jsonObjectRequest = object :
            JsonObjectRequest(Method.GET, url+ userId, null, Response.Listener {
                order_activity_history_Progressdialog.visibility = View.GONE
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val resArray = data.getJSONArray("data")
                        if (resArray.length() == 0) {
                            llHasOrders.visibility = View.GONE
                            order_history_fragment_no_orders.visibility = View.VISIBLE

                        } else {
                            for (i in 0 until resArray.length()) {
                                val orderObject = resArray.getJSONObject(i)
                                val foodItems = orderObject.getJSONArray("food_items")
                                val orderDetails = OrderHistoryRestaurant(
                                    orderObject.getString("order_id"),
                                    orderObject.getString("restaurant_name"),
                                    orderObject.getString("order_placed_at"),
                                    foodItems
                                )
                                orderHistoryList.add(orderDetails)
                                if (orderHistoryList.isEmpty()) {
                                    llHasOrders.visibility = View.GONE
                                    order_history_fragment_no_orders.visibility = View.VISIBLE
                                } else {
                                    llHasOrders.visibility = View.VISIBLE
                                    order_history_fragment_no_orders.visibility = View.GONE
                                    if (activity != null) {
                                        orderHistoryAdapter = OrderHistoryAdapter(
                                            activity as Context,
                                            orderHistoryList
                                        )
                                        val mLayoutManager =
                                            LinearLayoutManager(activity as Context)
                                        recyclerOrderHistory.layoutManager = mLayoutManager
                                        recyclerOrderHistory.itemAnimator = DefaultItemAnimator()
                                        recyclerOrderHistory.adapter = orderHistoryAdapter
                                    } else {
                                        queue.cancelAll(this::class.java.simpleName)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener {
                Toast.makeText(activity as Context, it.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"

                headers["token"] = "ccf52a609cc3d2"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }


    }


