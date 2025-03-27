package com.avocado.glamping.fragment.hosting.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.UserPreferences
import com.avocado.glamping.adapter.ChatListAdapter
import com.avocado.glamping.viewmodel.ChatViewModel
import com.avocado.glamping.viewmodel.GetUsersState
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListFragment : Fragment(R.layout.fragment_chat_list) {

    private val chatViewModel : ChatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.visibility = View.VISIBLE


        fetchChatUsers()
        observeFetchChatUsers(view)


    }

    private fun observeFetchChatUsers(view : View){
        chatViewModel.getUsersState.observe(viewLifecycleOwner) {state ->
            when(state) {
                is GetUsersState.Loading -> {
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                    view.findViewById<RecyclerView>(R.id.messageRecyclerView).visibility = View.GONE
                }
                is GetUsersState.Success -> {
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    val messageRecyclerView : RecyclerView = view.findViewById(R.id.messageRecyclerView)
                    messageRecyclerView.visibility = View.VISIBLE
                    messageRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    messageRecyclerView.adapter = ChatListAdapter(state.response) { user ->
                        val action = ChatListFragmentDirections.actionChatListFragmentToChatFragment(
                            user
                        )
                        findNavController().navigate(action)
                    }
                }
                is GetUsersState.Error -> {
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    view.findViewById<RecyclerView>(R.id.messageRecyclerView).visibility = View.VISIBLE
                }
            }
        }
    }

    private fun fetchChatUsers(){
        UserPreferences.getUser(requireContext())?.user?.id?.let {
            chatViewModel.getUsers(
                it
            )
        }
    }
}