package com.smruti.foodrunner.Adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView

import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.smruti.foodrunner.Activity.MenuActivity
import com.smruti.foodrunner.R
import com.smruti.foodrunner.database.RestaurantDatabase
import com.smruti.foodrunner.database.RestaurantEntity
import com.smruti.foodrunner.model.Restaurants
import com.squareup.picasso.Picasso

class AllRestaurantAdapter(val context: Context,var restaurants: ArrayList<Restaurants>):RecyclerView.Adapter<AllRestaurantAdapter.AllRestaurantViewHolder>(){


    class AllRestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantname: TextView = view.findViewById(R.id.txtrestaurantName)
        val price: TextView = view.findViewById(R.id.txtPrice)
        val rating: TextView = view.findViewById(R.id.txtrating)
        val restaurantimage: ImageView = view.findViewById(R.id.imgrestaurantImage)
        val AddToFav:ImageView = view.findViewById(R.id.imgIsFav)
        val cardView:CardView=view.findViewById(R.id.cardRestaurant)




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllRestaurantViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_row_design, parent, false)

        return AllRestaurantViewHolder(view)


    }

    override fun getItemCount(): Int {
       return restaurants.size
    }

    override fun onBindViewHolder(holder: AllRestaurantViewHolder, position: Int) {
        val restaurant = restaurants[position]
        val id = restaurant.restaurantid
        holder.restaurantname.text = restaurant.restaurantname
        val costforone=(restaurant.price)

        holder.price.text = "Rs. ${costforone}/person"
        holder.rating.text = restaurant.rating
        Picasso.get().load(restaurant.restaurantimage).error(R.drawable.ic_launcher_background)
            .into(holder.restaurantimage)
        val imageurl = restaurant.restaurantimage
        val restaurantEntity=RestaurantEntity(id,holder.restaurantname.text.toString(), holder.rating.text.toString(),costforone,imageurl)

        val checkFav = DBAsyncTask(context,restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav)
        {
            holder.AddToFav.setImageResource(R.drawable.ic_fav)
        }
      else
        {
            holder.AddToFav.setImageResource(R.drawable.ic_favourite)
        }
        holder.AddToFav.setOnClickListener {
        if (!DBAsyncTask(context,
                restaurantEntity,
                1
            ).execute().get()
        ) {

            val async =
                DBAsyncTask(context, restaurantEntity, 2).execute()
            val result = async.get()
            if (result) {

                holder.AddToFav.setImageResource(R.drawable.ic_fav)

            }
        } else {

            val async = DBAsyncTask(context, restaurantEntity, 3).execute()
            val result = async.get()

            if (result) {


                holder.AddToFav.setImageResource(R.drawable.ic_favourite)
            }

        }
    }
        holder.cardView.setOnClickListener {

            val intent=Intent(context,MenuActivity::class.java)
             intent.putExtra("resid",id)
            intent.putExtra("resname",holder.restaurantname.text.toString())

            intent.putExtra("costforone",costforone)
            intent.putExtra("rating",holder.rating.text.toString())
            intent.putExtra("imageurl",imageurl)
          context.startActivity(intent)


        }

        }

    fun filterList(filteredList: ArrayList<Restaurants>) {//to update the recycler view depending on the search
        restaurants= filteredList
        notifyDataSetChanged()
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

    }}


    /*class GetAllFavAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<String>>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.restaurantDao().getAllRestaurants()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.id.toString())
            }
            return listOfIds
        }*/

