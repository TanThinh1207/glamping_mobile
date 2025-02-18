package com.avocado.glamping.fragment.authentication

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.avocado.glamping.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentRegisterInformation : Fragment(R.layout.fragment_register_information) {

    private val args : FragmentRegisterInformationArgs by navArgs()

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

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        editTextDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    editTextDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }

        view.findViewById<TextInputEditText?>(R.id.editTextLastName).setText(args.lastname)
        view.findViewById<TextInputEditText>(R.id.editTextFirstName).setText(args.firstname)
        view.findViewById<TextInputEditText>(R.id.editTextEmail).setText(args.email)
        view.findViewById<TextInputEditText>(R.id.editTextPhone).setText(args.phone)

        backArrow.setOnClickListener{
            findNavController().navigate(R.id.action_fragmentRegisterInformation_to_fragmentAuthentication)
        }

        continueBtn.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentRegisterInformation_to_verificationFragment)
        }
    }
}