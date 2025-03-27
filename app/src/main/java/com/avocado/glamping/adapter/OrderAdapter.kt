package com.avocado.glamping.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.R
import com.avocado.glamping.data.model.req.OrderRequest
import com.avocado.glamping.di.PriceFormat

class OrderAdapter(
    private val orders : MutableList<OrderRequest>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>(){
    class OrderViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val orderName : TextView = view.findViewById(R.id.tv_order_name)
        val orderQuantity : TextView = view.findViewById(R.id.tv_order_quantity)
        val orderPrice : TextView = view.findViewById(R.id.tv_order_price)
        val orderTotal : TextView = view.findViewById(R.id.tv_order_total)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int = orders.count()

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.orderName.text = order.name
        holder.orderPrice.text = PriceFormat.formatPrice(order.price, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
        holder.orderQuantity.text = "x${order.quantity}"
        holder.orderTotal.text = PriceFormat.formatPrice(order.totalAmount, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
    }
}