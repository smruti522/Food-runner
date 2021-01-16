package com.smruti.foodrunner.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smruti.foodrunner.Adapter.AllRestaurantAdapter

import com.smruti.foodrunner.R
import com.smruti.foodrunner.model.Restaurants
import com.smruti.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.sort_radio_button.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap
import android.content.Context as Context1

/**
 * A simple [Fragment] subclass.
 */
class Home(val contextParam: Context1) : Fragment() {
lateinit var homerecyclerview:RecyclerView
    lateinit var homeadapter:AllRestaurantAdapter
   var restaurantlist= arrayListOf<Restaurants>()
    lateinit var progressBar: ProgressBar
    lateinit var editTextSearch:EditText
    lateinit var Norestaurant:RelativeLayout
    lateinit var radioButtonView:View
    lateinit var progressLayout: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    var ratingComparator= Comparator<Restaurants> { rest1, rest2 ->

        if(rest1.rating.compareTo(rest2.rating,true)==0){
            rest1.restaurantname.compareTo(rest2.restaurantname,true)
        }
        else{
            rest1.rating.compareTo(rest2.rating,true)
        }

    }

    var costComparator= Comparator<Restaurants> { rest1, rest2 ->

        rest1.price.compareTo(rest2.price,true)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

  layoutManager=LinearLayoutManager(activity)
        progressBar=view.findViewById(R.id.progressBar)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressLayout.visibility=View.VISIBLE
        editTextSearch=view.findViewById(R.id.editTextSearch)
        Norestaurant=view.findViewById(R.id.norestaurant)
  homerecyclerview=view.findViewById(R.id.recyclerRestaurants)
        fun filterFun(strTyped:String){//to filter the recycler view depending on what is typed
            val filteredList= arrayListOf<Restaurants>()

            for (item in restaurantlist){
                if(item.restaurantname.toLowerCase().contains(strTyped.toLowerCase())){//to ignore case and if contained add to new list

                    filteredList.add(item)

                }
            }

            if(filteredList.size==0){
                Norestaurant.visibility=View.VISIBLE
            }
            else{
                Norestaurant.visibility=View.INVISIBLE
            }

            homeadapter.filterList(filteredList)

        }

        editTextSearch.addTextChangedListener(object : TextWatcher {//as the user types the search filter is applied
        override fun afterTextChanged(strTyped: Editable?) {
            filterFun(strTyped.toString())
        }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        }
        )
        val queue = Volley.newRequestQueue(activity as Context1)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/"
        if (ConnectionManager().checkConnectivity(activity as Context1)) {


            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener<JSONObject> { response ->
                    progressLayout.visibility = View.GONE


                    try {
                        val data = response.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {

                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val resObject = resArray.getJSONObject(i)
                                val restaurant = Restaurants(
                                    resObject.getString("id").toInt(),
                                    resObject.getString("name"),
                                    resObject.getString("rating"),
                                    resObject.getString("cost_for_one"),
                                    resObject.getString("image_url")
                                )
                                restaurantlist.add(restaurant)
                                if (activity != null) {
                                    homeadapter =
                                        AllRestaurantAdapter(activity as Context1,restaurantlist)

                                   homerecyclerview.layoutManager =layoutManager

                                   homerecyclerview.adapter =homeadapter

                                }

                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError? ->
                    Toast.makeText(activity as Context1, error?.message, Toast.LENGTH_SHORT).show()
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
            val builder = AlertDialog.Builder(activity as Context1)
            builder.setTitle("Error")
            builder.setMessage("No Internet Connection found. Please connect to the internet and re-open the app.")
            builder.setCancelable(false)
            builder.setPositiveButton("Ok") { _, _ ->

            }
            builder.create()
            builder.show()
        }





        return view
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId

        when(id){

            R.id.sort->{
                radioButtonView= View.inflate(contextParam,R.layout.sort_radio_button,null)//radiobutton view for sorting display
                androidx.appcompat.app.AlertDialog.Builder(activity as android.content.Context)
                    .setTitle("Sort By?")
                    .setView(radioButtonView)
                    .setPositiveButton("OK") { text, listener ->
                        if (radioButtonView.radio_high_to_low.isChecked) {
                            Collections.sort(restaurantlist, costComparator)
                            restaurantlist.reverse()
                            homeadapter.notifyDataSetChanged() //updates the adapter
                        }
                        if (radioButtonView.radio_low_to_high.isChecked) {
                            Collections.sort(restaurantlist, costComparator)
                            homeadapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.radio_rating.isChecked) {
                            Collections.sort(restaurantlist, ratingComparator)
                            restaurantlist.reverse()
                            homeadapter.notifyDataSetChanged()//updates the adapter
                        }
                    }
                    .setNegativeButton("CANCEL") { text, listener ->

                    }
                    .create()
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }



    override fun onResume() {

        if (!ConnectionManager().checkConnectivity(activity as android.content.Context))
        {

            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(activity as android.content.Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                ActivityCompat.finishAffinity(activity as Activity)//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

        super.onResume()
    }

}
