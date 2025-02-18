package com.avocado.glamping.fragment.hosting.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.adapter.BookingAdapter
import com.avocado.glamping.adapter.StatusAdapter
import com.avocado.glamping.data.model.req.BookingRequest

class BookingFragment : Fragment(R.layout.fragment_booking) {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookingRecyclerView : RecyclerView = view.findViewById(R.id.bookingRecyclerView)
        bookingRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val bookingList = mutableListOf(
            BookingRequest(1,"John Doe", "Sunset Camp", "2024-02-10", "2025-02-10", 1500000.0, "Chờ xủa lý"),
            BookingRequest(2,"John Thinh", "Sunset Camp", "2024-02-10", "2025-02-10", 1500000.0, "Chờ xủa lý"),
            BookingRequest(3,"John Hua", "Sunset Camp", "2024-02-10", "2025-02-10", 1500000.0, "Chờ xủa lý"),
            BookingRequest(4,"John Tan", "Sunset Camp", "2024-02-10", "2025-02-10", 1500000.0, "Chờ xủa lý"),
            BookingRequest(5,"John Avocado", "Sunset Camp", "2024-02-10", "2025-02-10", 1500000.0, "Chờ xủa lý"),

            )
        bookingRecyclerView.adapter = BookingAdapter(bookingList){ booking ->
            val action = BookingFragmentDirections.actionBookingFragmentToBookingDetailFragment(booking.id.toString())
            findNavController().navigate(action)
        }
//
//        adapter = BookingAdapter(bookingList, this)
//        recyclerView.adapter = adapter

        val statusList = listOf("Chờ xử lý", "Đã đặt cọc", "Đã chấp nhận", "Bị hủy", "Bị từ chối", "Đã hoàn thành")

        val recyclerView : RecyclerView = view.findViewById(R.id.statusRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = StatusAdapter(statusList){
            status -> Toast.makeText(requireContext(), "Clicked: $status", Toast.LENGTH_SHORT).show()
        }
    }

}