package com.smruti.foodrunner.Fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.smruti.foodrunner.Adapter.AllRestaurantAdapter

import com.smruti.foodrunner.R
import com.smruti.foodrunner.database.RestaurantDatabase
import com.smruti.foodrunner.database.RestaurantEntity
import com.smruti.foodrunner.model.Restaurants
import kotlinx.android.synthetic.main.sort_radio_button.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class Favourites(val contextParam:Context) : Fragment() {
    private lateinit var recyclerRestaurant: RecyclerView
    private lateinit var allRestaurantsAdapter: AllRestaurantAdapter
    private var restaurantList = arrayListOf<Restaurants>()
    private lateinit var rlLoading: RelativeLayout
    private lateinit var rlFav: RelativeLayout
    private lateinit var rlNoFav: RelativeLayout
    lateinit var radioButtonView:View
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
       val view=inflater.inflate(R.layout.fragment_favourites, container, false)
        setHasOptionsMenu(true)
        rlFav = view.findViewById(R.id.rlFavorites)
        rlNoFav = view.findViewById(R.id.rlNoFavorites)
        rlLoading = view.findViewById(R.id.rlprogressLayout)
        rlLoading.visibility = View.VISIBLE
        setUpRecycler(view)
        return view
    }

    private fun setUpRecycler(view: View) {
        recyclerRestaurant = view.findViewById(R.id.recyclerRestaurants)


        val backgroundList = FavouritesAsync(activity as Context).execute().get()
        if (backgroundList.isEmpty()) {
            rlLoading.visibility = View.GONE
            rlFav.visibility = View.GONE
            rlNoFav.visibility = View.VISIBLE
        } else {
            rlFav.visibility = View.VISIBLE
            rlLoading.visibility = View.GONE
            rlNoFav.visibility = View.GONE
            for (i in backgroundList) {
                restaurantList.add(
                    Restaurants(
                        i.id,
                        i.name,
                        i.rating,
                        i.costForTwo,
                        i.imageUrl
                    )
                )
            }

            allRestaurantsAdapter = AllRestaurantAdapter(activity as Context,restaurantList)
            val mLayoutManager = LinearLayoutManager(activity)
            recyclerRestaurant.layoutManager = mLayoutManager
            recyclerRestaurant.itemAnimator = DefaultItemAnimator()
            recyclerRestaurant.adapter = allRestaurantsAdapter
            recyclerRestaurant.setHasFixedSize(true)
        }

    }



    class FavouritesAsync(context: Context) : AsyncTask<Void, Void, List<RestaurantEntity>>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {

            return db.restaurantDao().getAllRestaurants()
        }

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
                            Collections.sort(restaurantList, costComparator)
                            restaurantList.reverse()
                            allRestaurantsAdapter.notifyDataSetChanged() //updates the adapter
                        }
                        if (radioButtonView.radio_low_to_high.isChecked) {
                            Collections.sort(restaurantList, costComparator)
                            allRestaurantsAdapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.radio_rating.isChecked) {
                            Collections.sort(restaurantList, ratingComparator)
                            restaurantList.reverse()
                            allRestaurantsAdapter.notifyDataSetChanged()//updates the adapter
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


}
