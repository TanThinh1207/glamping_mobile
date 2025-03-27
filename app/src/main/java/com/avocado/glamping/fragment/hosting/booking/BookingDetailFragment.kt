package com.avocado.glamping.fragment.hosting.booking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.R
import com.avocado.glamping.adapter.BookingCampTypeAdapter
import com.avocado.glamping.adapter.BookingDetailAdapter
import com.avocado.glamping.adapter.BookingServiceAdapter
import com.avocado.glamping.data.model.req.OrderRequest
import com.avocado.glamping.data.model.resp.BookingDetailResponse
import com.avocado.glamping.data.model.resp.BookingResponse
import com.avocado.glamping.data.model.resp.CampTypeItem
import com.avocado.glamping.data.model.resp.CampTypeResponse
import com.avocado.glamping.di.PriceFormat
import com.avocado.glamping.fragment.dialog.BookingDenyBottomSheetDialog
import com.avocado.glamping.viewmodel.BookingViewModel
import com.avocado.glamping.viewmodel.UpdateBookingState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text
import java.util.Locale

@AndroidEntryPoint
class BookingDetailFragment : Fragment(R.layout.fragment_booking_detail) {
    private val bookingViewModel : BookingViewModel by viewModels()
    private val args : BookingDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val booking = args.booking

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE
        var remain = 0.0
        if (booking.paymentResponseList.isNotEmpty()) {
            remain = booking.totalAmount - booking.paymentResponseList[0].totalAmount
        }


        displayButton(view, booking.status)

        view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            findNavController().navigate(R.id.action_bookingDetailFragment_to_bookingFragment)
        }

        view.findViewById<TextView>(R.id.bookingIdTitle).text = String.format(Locale.US,"%010d", booking.id)
        view.findViewById<TextView>(R.id.bookingId).text = String.format(Locale.US,"%010d", booking.id)

        view.findViewById<ImageView>(R.id.image).load(
            booking.campSite.imageList.getOrNull(0)?.path
        )

        view.findViewById<TextView>(R.id.customer_name).text =
            getString(R.string.booking_user_name, booking.user.lastname ?: "", booking.user.firstname ?: "")

        when (booking.status) {
            "Check_In" -> {
                view.findViewById<TextView>(R.id.total_add_on).text = PriceFormat.formatPrice(0.0, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
                view.findViewById<TextView>(R.id.remaining_amount).text = PriceFormat.formatPrice(remain, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
                view.findViewById<TextView>(R.id.total_with_add_ons).text = PriceFormat.formatPrice(remain, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
            }
            "Completed" -> {
                val totalAddOn = booking.bookingDetailResponseList.sumOf { it.addOn }
                view.findViewById<TextView>(R.id.total_add_on).text = PriceFormat.formatPrice(totalAddOn, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
                view.findViewById<TextView>(R.id.remaining_amount).text = PriceFormat.formatPrice(remain, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
                view.findViewById<TextView>(R.id.total_with_add_ons).text = PriceFormat.formatPrice(remain + totalAddOn, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
            }
            else -> {
                view.findViewById<LinearLayout>(R.id.total_add_on_layout).visibility = View.GONE
                view.findViewById<LinearLayout>(R.id.remaining_amount_layout).visibility = View.GONE
                view.findViewById<LinearLayout>(R.id.total_with_add_ons_layout).visibility = View.GONE
            }
        }

        view.findViewById<TextView>(R.id.phone).text = booking.user.phone ?: "N/A"
        view.findViewById<TextView>(R.id.email).text = booking.user.email ?: "N/A"
        view.findViewById<TextView>(R.id.created_time).text = booking.createdAt
        view.findViewById<TextView>(R.id.check_in_date).text = booking.checkIn
        view.findViewById<TextView>(R.id.check_out_date).text = booking.checkOut
        view.findViewById<TextView>(R.id.deposit_date).text = if (booking.paymentResponseList.isEmpty()) "--" else booking.paymentResponseList[0].completedAt


        if (booking.status != "Check_In"){
            view.findViewById<RecyclerView>(R.id.recyclerViewBookingDetails).visibility = View.GONE
            val campTypeRecyclerView : RecyclerView = view.findViewById(R.id.recyclerViewCampTypes)
            campTypeRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            campTypeRecyclerView.adapter = BookingCampTypeAdapter(booking.campTypeItemResponse)
        }else {
            view.findViewById<RecyclerView>(R.id.recyclerViewCampTypes).visibility = View.GONE
            val bookingDetailRecyclerView : RecyclerView = view.findViewById(R.id.recyclerViewBookingDetails)
            bookingDetailRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            bookingDetailRecyclerView.adapter = BookingDetailAdapter(requireActivity(),booking.bookingDetailResponseList) { totalAddOn ->
                view.findViewById<TextView>(R.id.total_add_on).text = PriceFormat.formatPrice(totalAddOn, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
                view.findViewById<TextView>(R.id.total_with_add_ons).text = PriceFormat.formatPrice(totalAddOn + remain, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
            }
        }




        val serviceRecyclerView : RecyclerView = view.findViewById(R.id.recyclerViewService)
        serviceRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        serviceRecyclerView.adapter = BookingServiceAdapter(booking.bookingSelectionResponseList)

        view.findViewById<TextView>(R.id.total_amount).text = PriceFormat.formatPrice(booking.totalAmount, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
        view.findViewById<TextView>(R.id.deposit).text = if (booking.paymentResponseList.isEmpty() || booking.paymentResponseList[0].status == "Pending")  PriceFormat.formatPrice(0.0, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
        else PriceFormat.formatPrice(booking.paymentResponseList[0].totalAmount, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
        view.findViewById<TextView>(R.id.status).apply {
            text = booking.status
            setTextColor(
                when (booking.status) {
                    "Pending" -> ContextCompat.getColor(context, R.color.gray) // Gray
                    "Deposit" -> ContextCompat.getColor(context, R.color.yellow) // Yellow
                    "Accepted", "Check_In" -> ContextCompat.getColor(context, R.color.blue) // Blue
                    "Completed" -> ContextCompat.getColor(context, R.color.green) // Green
                    else -> ContextCompat.getColor(context, R.color.red) // Red
                }
            )
        }

        view.findViewById<MaterialButton>(R.id.acceptBtn).setOnClickListener {
            updateBooking(booking, "accept", "", emptyList())
        }

        view.findViewById<MaterialButton>(R.id.btn_check_in).setOnClickListener {
            updateBooking(booking, "checkin", "", emptyList())
        }

        view.findViewById<MaterialButton>(R.id.btn_check_out).setOnClickListener {
            val orders = booking.bookingDetailResponseList.flatMap { it.orders ?: emptyList() }
            updateBooking(booking, "checkout", "", orders)
        }

        view.findViewById<MaterialButton>(R.id.denyBtn).setOnClickListener {
            // showDenyReasonDialog(booking)
            showDenyBottomDialog(booking)
        }

        observeUpdateBooking(view)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.visibility = View.VISIBLE  // Show Bottom Navigation when returning
    }

    private fun updateBooking(booking : BookingResponse, status : String, reason : String, bookingDetailOrders : List<OrderRequest>){
        bookingViewModel.updateBooking(booking.id, status, reason, bookingDetailOrders)
    }

    private fun observeUpdateBooking(view: View){
        bookingViewModel.updateBookingState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UpdateBookingState.Loading ->{
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                    view.findViewById<LinearLayout>(R.id.btnLayout).visibility = View.GONE
                }
                is UpdateBookingState.Success ->{
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    Toast.makeText(requireContext(), "Updated Status Successfully", Toast.LENGTH_SHORT).show()
                }
                is UpdateBookingState.Error ->{
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    view.findViewById<LinearLayout>(R.id.btnLayout).visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Error: ${state.mess}", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    private fun showDenyBottomDialog(booking: BookingResponse){
        val bottomSheet = BookingDenyBottomSheetDialog.newInstance(booking)
        bottomSheet.show(parentFragmentManager, "DenyBookingSheet")
    }

    private fun displayButton(view : View, status : String){
        when (status) {
            "Deposit" -> {
                // Show both Accept and Deny buttons
                view.findViewById<MaterialButton>(R.id.acceptBtn).visibility = View.VISIBLE
                view.findViewById<MaterialButton>(R.id.denyBtn).visibility = View.VISIBLE
                view.findViewById<MaterialButton>(R.id.btn_check_in).visibility = View.GONE

            }
            "Accepted" -> {
                // Hide Accept and Deny buttons, show Check-In button
                view.findViewById<MaterialButton>(R.id.acceptBtn).visibility = View.GONE
                view.findViewById<MaterialButton>(R.id.denyBtn).visibility = View.GONE
                view.findViewById<MaterialButton>(R.id.btn_check_in).visibility = View.VISIBLE
            }
            "Check_In" ->{
                view.findViewById<MaterialButton>(R.id.acceptBtn).visibility = View.GONE
                view.findViewById<MaterialButton>(R.id.denyBtn).visibility = View.GONE
                view.findViewById<MaterialButton>(R.id.btn_check_out).visibility = View.VISIBLE
            }
            else -> {
                // Handle unexpected status (optional)
                view.findViewById<MaterialButton>(R.id.acceptBtn).visibility = View.GONE
                view.findViewById<MaterialButton>(R.id.denyBtn).visibility = View.GONE
                view.findViewById<MaterialButton>(R.id.btn_check_in).visibility = View.GONE
            }
        }
    }


}