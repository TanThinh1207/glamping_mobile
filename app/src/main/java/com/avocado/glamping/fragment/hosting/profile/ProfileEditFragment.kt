package com.avocado.glamping.fragment.hosting.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.avocado.glamping.R
import com.avocado.glamping.UserPreferences
import com.avocado.glamping.activity.HostingActivity
import com.avocado.glamping.data.model.req.UserRequest
import com.avocado.glamping.viewmodel.RegisterState
import com.avocado.glamping.viewmodel.UpdateUserState
import com.avocado.glamping.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

@AndroidEntryPoint
class ProfileEditFragment : Fragment(R.layout.fragment_profile_edit) {

    private val args : ProfileEditFragmentArgs by navArgs()
    private val userViewModel : UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE

        val user = args.user

        val firstName = view.findViewById<TextInputEditText>(R.id.ed_first_name)
        firstName.setText(user.firstname)
        val lastName = view.findViewById<TextInputEditText>(R.id.ed_last_name)
        lastName.setText(user.lastname)
        val dob = view.findViewById<TextInputEditText>(R.id.ed_dob)
        val phone = view.findViewById<TextInputEditText>(R.id.ed_phone)
        phone.setText(user.phone)
        val address = view.findViewById<TextInputEditText>(R.id.ed_address)
        address.setText(user.address)
        view.findViewById<TextInputEditText>(R.id.ed_email).setText(user.email)

        val progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        val saveBtn : MaterialButton = view.findViewById(R.id.btn_save)

        dob.setText(user.birthday)
        dob.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.format(selection)
                dob.setText(date)
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        saveBtn.setOnClickListener {
            userViewModel.update(args.user.id, UserRequest(firstName.text.toString(), lastName.text.toString(), phone.text.toString(),
                address.text.toString(), LocalDate.parse(dob.text.toString()).toString())
            )
        }

        userViewModel.updateUserState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UpdateUserState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    saveBtn.visibility = View.GONE
                }

                is UpdateUserState.Success -> {
                    progressBar.visibility = View.GONE
                    saveBtn.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Update user successful", Toast.LENGTH_SHORT)
                        .show()
                    val authResp = UserPreferences.getUser(requireContext())
                    if (authResp != null) {
                        authResp.user = state.response
                        UserPreferences.saveUser(requireContext(), authResp)
                    }
                    findNavController().navigate(R.id.action_profileEditFragment_to_profileFragment)
                }

                is UpdateUserState.Error -> {
                    progressBar.visibility = View.GONE
                    saveBtn.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), state.mess, Toast.LENGTH_SHORT).show()
                    Log.e("Button Error", state.mess)
                }

            }
        }

        view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            findNavController().navigate(R.id.action_profileEditFragment_to_profileFragment)
        }

    }
}