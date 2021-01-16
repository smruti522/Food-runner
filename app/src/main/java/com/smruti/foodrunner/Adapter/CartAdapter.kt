package com.smruti.foodrunner.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smruti.foodrunner.R
import com.smruti.foodrunner.model.CartItems

class CartAdapter(val context: Context,val cartitem:ArrayList<CartItems>):RecyclerView.Adapter<CartAdapter.CartViewHolder>(){
    class CartViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtorder:TextView=view.findViewById(R.id.txtMenu)
        val txtprice:TextView=view.findViewById(R.id.txtmenucost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.cart_single_row,parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
       return cartitem.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartitems= cartitem[position]
        holder.txtorder.text=cartitems.itemname
        holder.txtprice.text="Rs. ${cartitems.itemprice}"
    }
}