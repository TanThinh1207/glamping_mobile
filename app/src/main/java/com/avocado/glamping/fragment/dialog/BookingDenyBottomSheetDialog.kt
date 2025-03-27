package com.avocado.glamping.fragment.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.BookingResponse
import com.avocado.glamping.di.PriceFormat
import com.avocado.glamping.viewmodel.BookingViewModel
import com.avocado.glamping.viewmodel.RefundState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingDenyBottomSheetDialog : BottomSheetDialogFragment() {

    private val bookingViewModel : BookingViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_deny_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val booking = arguments?.getParcelable<BookingResponse>("booking")

        val reason = view.findViewById<TextView>(R.id.et_reason)
        view.findViewById<TextView>(R.id.tv_refund).text = if (
            booking == null
        ) PriceFormat.formatPrice(0.0, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
        else PriceFormat.formatPrice(booking.paymentResponseList[0].totalAmount, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)

        view.findViewById<MaterialButton>(R.id.btn_save).setOnClickListener {
            booking?.id?.let { it1 -> handleRefund(it1, reason.text.toString()) }
        }
        Log.e("Booking", booking.toString())

        observeRefund(view)
    }

    private fun observeRefund(view : View){
        bookingViewModel.refundState.observe(viewLifecycleOwner){ state ->
            when(state){
                is RefundState.Loading -> {
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.GONE
                }
                is RefundState.Success -> {
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    dismiss()
                }
                is RefundState.Error -> {
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.VISIBLE
                    Toast.makeText(requireContext(), state.mess, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleRefund(bookingId : Int,reason: String) {
        bookingViewModel.refund(bookingId, reason)
    }

    companion object {
        fun newInstance(booking: BookingResponse): BookingDenyBottomSheetDialog {
            val fragment = BookingDenyBottomSheetDialog()
            val args = Bundle()
            args.putParcelable("booking", booking)
            fragment.arguments = args
            return fragment
        }
    }
}