package com.smruti.foodrunner.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.smruti.foodrunner.R
import com.smruti.foodrunner.R.color
import com.smruti.foodrunner.model.MenuItems

class OrderAdapter(val context: Context, var menuitems: ArrayList<MenuItems>,private val listener:OnItemClickListener):RecyclerView.Adapter<OrderAdapter.OrderViewHolder>()
{
    var item=0
    companion object{
        var isCartEmpty=true
    }
    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val menuname:TextView=view.findViewById(R.id.txtmenuname)
        val menuprice:TextView=view.findViewById(R.id.txtmenuprice)
        val menuadd:Button=view.findViewById(R.id.btnmenuadd)

        val menutext:TextView=view.findViewById(R.id.slno)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_order_row, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuitems.size
    }
interface OnItemClickListener{
    fun onAddItemClick(menuItems: MenuItems)
    fun onRemoveItemClick(menuItems: MenuItems)
}
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val menu = menuitems[position]
        val menuid = menu.menuid
        val pos: Int = position
        holder.menutext.text = (pos + 1).toString()
        holder.menuname.text = menu.menuname
        val menucost = menu.menucost
        holder.menuprice.text = "Rs.${menucost}"
        val restaurantid = menu.restaurantid


        holder.menuadd.setOnClickListener {
            if(holder.menuadd.text.toString().equals("Add"))
            { holder.menuadd.text="Remove"

            val nofav   =ContextCompat.getColor(context, R.color.nofav)
               holder.menuadd.setBackgroundColor(nofav)
            listener.onAddItemClick(menu)
            item++}
            else{
                val fav   =ContextCompat.getColor(context, R.color.fav)
                holder.menuadd.text="Add"
                holder.menuadd.setBackgroundColor(fav)
                listener.onRemoveItemClick(menu)
                item--
            }

        }

    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

public fun getitemcount():Int{
    return item
}

}
