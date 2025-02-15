package com.avocado.glamping.fragment.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.avocado.glamping.R
import com.google.android.material.button.MaterialButton

class FragmentRegisterInformation : Fragment(R.layout.fragment_register_information) {

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

        backArrow.setOnClickListener{
            findNavController().navigate(R.id.action_fragmentRegisterInformation_to_fragmentAuthentication)
        }

        continueBtn.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentRegisterInformation_to_verificationFragment)
        }
    }
}