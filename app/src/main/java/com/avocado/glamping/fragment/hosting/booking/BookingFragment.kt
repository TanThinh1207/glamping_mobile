package com.avocado.glamping.fragment.hosting.booking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.UserPreferences
import com.avocado.glamping.adapter.BookingAdapter
import com.avocado.glamping.adapter.StatusAdapter
import com.avocado.glamping.viewmodel.BookingViewModel
import com.avocado.glamping.viewmodel.GetBookingsState
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.ceil

@AndroidEntryPoint
class BookingFragment : Fragment(R.layout.fragment_booking) {

    private val bookingViewModel : BookingViewModel by viewModels()

    private lateinit var pageDropdown : AutoCompleteTextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = UserPreferences.getUser(requireContext())?.user
        view.findViewById<TextView>(R.id.bookingTitle).text = "Hello, ${user?.firstname}"

        val bookingRecyclerView : RecyclerView = view.findViewById(R.id.bookingRecyclerView)
        bookingRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        pageDropdown = view.findViewById(R.id.pageDropdown)
        pageDropdown.setText("Page 1", false)

        val statusList = listOf("Deposit", "Pending", "Accepted", "Cancelled", "Refund", "Check_In", "Completed")

        val recyclerView : RecyclerView = view.findViewById(R.id.statusRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val statusAdapter = StatusAdapter(statusList) { status ->
            bookingViewModel.setSelectedStatus(status)
            pageDropdown.setText("Page 1", false)
        }
        recyclerView.adapter = statusAdapter

        // Observe selected status and fetch bookings accordingly
        bookingViewModel.selectedStatus.observe(viewLifecycleOwner) { selectedStatus ->
            statusAdapter.setSelectedStatus(selectedStatus)
            fetchBookings(0) // Fetch data AFTER status updates
        }

        bookingViewModel.getBookingsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is GetBookingsState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    bookingRecyclerView.visibility = View.GONE
                }
                is GetBookingsState.Success -> {
                    progressBar.visibility = View.GONE
                    bookingRecyclerView.visibility = View.VISIBLE


                    val totalPages = state.response.totalPages
                    val pageNumbers = (1..totalPages).map { "Page $it" }

                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, pageNumbers)
                    pageDropdown.setAdapter(adapter)

                    pageDropdown.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) (v as AutoCompleteTextView).showDropDown()
                    }

                    pageDropdown.setOnClickListener { pageDropdown.showDropDown() }

                    pageDropdown.setOnItemClickListener { _, _, position, _ ->
                        pageDropdown.setText("Page ${position + 1}", false) // Fix: Page should start from 1
                        fetchBookings(position)
                    }

                    val bookingAdapter = BookingAdapter(state.response.content) { booking ->
                        Log.d("BookingFragment", "Navigating with booking: $booking")
                        val action = BookingFragmentDirections.actionBookingFragmentToBookingDetailFragment(booking)
                        findNavController().navigate(action)
                    }
                    bookingRecyclerView.adapter = bookingAdapter
                }
                is GetBookingsState.Error -> {
                    Log.e("Booking Error", state.mess)
                    progressBar.visibility = View.GONE
                    bookingRecyclerView.visibility = View.GONE
                }
                else -> Unit
            }
        }
    }

    private fun fetchBookings(page: Int) {
        val userId = UserPreferences.getUser(requireContext())?.user?.id

        val filter = mapOf(
            "status" to bookingViewModel.selectedStatus.value,
            "hostId" to userId.toString()
        )

        bookingViewModel.getBookings(
            filter,
            page,
            10,
            "",
            "id",
            "ASC"
        )
    }
}
