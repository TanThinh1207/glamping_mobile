package com.avocado.glamping.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avocado.glamping.R
import com.avocado.glamping.data.model.req.OrderRequest
import com.avocado.glamping.data.model.resp.BookingDetailResponse
import com.avocado.glamping.fragment.dialog.OrderBottomSheetDialog

class BookingDetailAdapter(
    private val activity: FragmentActivity,
    private val bookingDetails: List<BookingDetailResponse>,
    private val onTotalAddon: (Double) -> Unit
) : RecyclerView.Adapter<BookingDetailAdapter.BookingDetailViewHolder>() {

    class BookingDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.img_camp_type)
        val campTypeName: TextView = view.findViewById(R.id.tv_camp_type_name)
        val campName: TextView = view.findViewById(R.id.tv_camp_name)
        val checkIn: TextView = view.findViewById(R.id.tv_booking_detail_check_in)
        val recyclerDropDown: RecyclerView = view.findViewById(R.id.recycler_dropdown_list)
        val dropDownBtn: ImageView = view.findViewById(R.id.img_dropdown)
        val addOrderBtn: ImageView = view.findViewById(R.id.img_add_order)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking_detail, parent, false)
        return BookingDetailViewHolder(view)
    }

    override fun getItemCount(): Int = bookingDetails.count()

    override fun onBindViewHolder(holder: BookingDetailViewHolder, position: Int) {
        val bookingDetail = bookingDetails[position]

        holder.image.load(bookingDetail.campTypeResponse.image) {
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }
        holder.campTypeName.text = bookingDetail.campTypeResponse.type
        holder.campName.text = bookingDetail.campResponse?.campName
        holder.checkIn.text = bookingDetail.checkInAt

        val orderAdapter = OrderAdapter(bookingDetail.orders ?: mutableListOf())
        holder.recyclerDropDown.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerDropDown.adapter = orderAdapter

        var isExpanded = false
        holder.dropDownBtn.setOnClickListener {
            isExpanded = !isExpanded
            holder.recyclerDropDown.visibility = if (isExpanded) View.VISIBLE else View.GONE
            holder.addOrderBtn.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }

        holder.addOrderBtn.setOnClickListener {
            val bottomSheet = OrderBottomSheetDialog.newInstance(bookingDetail.bookingDetailId)
            bottomSheet.setOnOrderAddedListener { newOrder ->
                if (bookingDetail.orders == null) {
                    bookingDetail.orders = mutableListOf()
                }
                bookingDetail.orders!!.add(newOrder)

                Log.e("Booking Detail Order Empty", bookingDetail.orders!!.isEmpty().toString())
                holder.recyclerDropDown.adapter = OrderAdapter(bookingDetail.orders!!)

                val newTotalAddOn = bookingDetails.sumOf { detail ->
                    detail.orders?.sumOf { order -> order.totalAmount ?: 0.0 } ?: 0.0
                }
                onTotalAddon(newTotalAddOn)
                Log.e("Booking Detail Order", bookingDetail.orders.toString())
            }
            bottomSheet.show(activity.supportFragmentManager, "OrderDialog")
        }
    }
}
