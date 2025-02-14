package com.avocado.glamping.fragment.hosting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.adapter.BookingAdapter
import com.avocado.glamping.data.model.req.BookingRequest

class BookingFragment : Fragment(R.layout.fragment_booking), BookingAdapter.OnBookingActionListener {

    private lateinit var bookingList : MutableList<BookingRequest>
    private lateinit var adapter : BookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        bookingList = mutableListOf(
            BookingRequest("John Doe", "Sunset Camp", "2024-02-10"),
            BookingRequest("Emma Smith", "Mountain Retreat", "2024-02-15"),
            BookingRequest("Alex Brown", "Lakeside Camp", "2024-02-20"),
            BookingRequest("Avocado Hua", "Riverside Camp", "2024-02-22")
        )

        adapter = BookingAdapter(bookingList, this)
        recyclerView.adapter = adapter
    }

    override fun onAccept(booking: BookingRequest) {
        Toast.makeText(requireContext(), "Accepted: ${booking.userName}", Toast.LENGTH_SHORT).show()
    }

    override fun onDeny(booking: BookingRequest) {
        Toast.makeText(requireContext(), "Denied: ${booking.userName}", Toast.LENGTH_SHORT).show()
    }
}