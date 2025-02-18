package com.avocado.glamping.fragment.hosting.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.avocado.glamping.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class BookingDetailFragment : Fragment(R.layout.fragment_booking_detail) {

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

        view.findViewById<TextView>(R.id.bookingId).text = args.bookingId
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE  // Hide Bottom Navigation

        view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            findNavController().navigate(R.id.action_bookingDetailFragment_to_bookingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.visibility = View.VISIBLE  // Show Bottom Navigation when returning
    }
}