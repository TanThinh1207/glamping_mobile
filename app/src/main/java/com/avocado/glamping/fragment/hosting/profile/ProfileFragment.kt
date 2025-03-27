package com.avocado.glamping.fragment.hosting.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.avocado.glamping.R
import com.avocado.glamping.UserPreferences
import com.avocado.glamping.activity.LoginRegisterActivity
import com.avocado.glamping.data.model.resp.UserResponse
import com.avocado.glamping.viewmodel.AuthViewModel
import com.avocado.glamping.viewmodel.CampSiteViewModel
import com.avocado.glamping.viewmodel.DeleteFcmTokenState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val authViewModel : AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("Profile Fragment", "onCreateView")
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.visibility = View.VISIBLE

        val logOutBtn : MaterialButton = view.findViewById(R.id.logOutBtn)

        view.findViewById<TextView>(R.id.user_name).text = UserPreferences.getUser(requireContext())?.user?.firstname
        val user = UserPreferences.getUser(requireContext())?.user

        Log.e("UserResponse", user.toString())

        if (user != null && user.isRestricted){
            view.findViewById<CardView>(R.id.card_warning).visibility = View.VISIBLE
        }

        view.findViewById<ImageView>(R.id.img_edit_profile).setOnClickListener {
            val action = UserPreferences.getUser(requireContext())?.user?.let { it1 ->
                ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment2(
                    it1
                )
            }
            if (action != null) {
                findNavController().navigate(action)
            }
        }

        view.findViewById<ImageView>(R.id.img_insight).setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_statisticFragment)
        }

        Log.e("Profile View", "Created")

        logOutBtn.setOnClickListener {
            Log.e("Log out Btn", "Here")
            val userId = UserPreferences.getUser(requireContext())?.user?.id
            authViewModel.signOut(userId, requireContext())
        }
        authViewModel.deleteFcmTokenState.observe(viewLifecycleOwner){ state ->
            when(state){
                is DeleteFcmTokenState.Loading ->{

                }
                is DeleteFcmTokenState.Success ->{
                    UserPreferences.clearUser(requireContext())
                    val intent = Intent(requireContext(), LoginRegisterActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is DeleteFcmTokenState.Error ->{
                    Log.e("Logout Error", state.mess)
                }
            }

        }
    }


}