package com.avocado.glamping.fragment.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.avocado.glamping.R
import com.avocado.glamping.activity.HostingActivity
import com.google.android.material.button.MaterialButton

class FragmentAuthentication : Fragment(R.layout.fragment_authentication) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_authentication, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val skipForNow : TextView = view.findViewById(R.id.skipForNow)
        val googleBtn : MaterialButton = view.findViewById(R.id.googleBtn)

        skipForNow.setOnClickListener{
            val intent = Intent(requireContext(), HostingActivity::class.java)
            startActivity(intent)
        }

        googleBtn.setOnClickListener{
            findNavController().navigate(R.id.action_fragmentAuthentication_to_fragmentRegisterInformation)
        }
    }
}