package com.avocado.glamping.fragment.authentication

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
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
import com.avocado.glamping.viewmodel.RegistrationViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class FragmentRegisterInformation : Fragment(R.layout.fragment_register_information) {

    private val args : FragmentRegisterInformationArgs by navArgs()
    private val registrationViewModel : RegistrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backArrow : ImageView = view.findViewById(R.id.backArrow)
        val continueBtn : MaterialButton = view.findViewById(R.id.continueBtn)
        val editTextDate : TextInputEditText = view.findViewById(R.id.editTextDate)
        val progressBar : ProgressBar = view.findViewById(R.id.progressBar)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        editTextDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.format(selection)
                editTextDate.setText(date)
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        view.findViewById<TextInputEditText>(R.id.editTextEmail).setText(args.email)


        backArrow.setOnClickListener{
            findNavController().navigate(R.id.action_fragmentRegisterInformation_to_fragmentAuthentication)
        }

        continueBtn.setOnClickListener {
            val firstName = view.findViewById<TextInputEditText>(R.id.editTextFirstName).text.toString().trim()
            val lastName = view.findViewById<TextInputEditText>(R.id.editTextLastName).text.toString().trim()
            val phone = view.findViewById<TextInputEditText>(R.id.editTextPhone).text.toString().trim()
            val address = view.findViewById<TextInputEditText>(R.id.editTextAddress).text.toString().trim()
            val dobString = editTextDate.text?.toString()?.trim()

            if (dobString.isNullOrEmpty()) {
                editTextDate.error = "Please select a valid date of birth"
                return@setOnClickListener
            }

            registrationViewModel.update(args.id.toInt(), UserRequest(firstName, lastName, phone,
                address, LocalDate.parse(dobString).toString()))
        }

        registrationViewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    continueBtn.visibility = View.GONE
                }

                is RegisterState.Success -> {
                    progressBar.visibility = View.GONE
                    continueBtn.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Update user successful", Toast.LENGTH_SHORT)
                        .show()
                    val authResp = UserPreferences.getUser(requireContext())
                    if (authResp != null) {
                        authResp.user = state.response
                        UserPreferences.saveUser(requireContext(), authResp)
                    }
                    val intent = Intent(requireContext(), HostingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }

                is RegisterState.Error -> {
                    progressBar.visibility = View.GONE
                    continueBtn.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), state.mess, Toast.LENGTH_SHORT).show()
                    Log.e("Button Error", state.mess)
                }

            }
        }
    }
}