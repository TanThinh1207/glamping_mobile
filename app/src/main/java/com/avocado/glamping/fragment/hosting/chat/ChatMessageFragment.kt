package com.avocado.glamping.fragment.hosting.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.UserPreferences
import com.avocado.glamping.adapter.ChatAdapter
import com.avocado.glamping.data.model.req.ChatMessageRequest
import com.avocado.glamping.di.WebSocketManager
import com.avocado.glamping.viewmodel.ChatViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var page = 0
    private val args : ChatFragmentArgs by navArgs()

    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE  // Hide Bottom Navigation

        val sender = args.sender

        val userId = UserPreferences.getUser(requireContext())?.user?.id

        view.findViewById<TextView>(R.id.title_recipient_name).text = sender.firstname

        view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            chatViewModel.clearMessages()
            chatAdapter.submitList(emptyList()) {  // Clear adapter data
                findNavController().popBackStack()
            }
        }

        if (userId != null) chatAdapter = ChatAdapter(userId)
        recyclerView = view.findViewById(R.id.messageRecyclerView)
        messageInput = view.findViewById(R.id.ed_message)
        sendButton = view.findViewById(R.id.layout_send)

        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (userId != null) {
            chatViewModel.getHistory(userId, sender.id)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (layoutManager.findFirstVisibleItemPosition() == 0) {
                    val senderId = args.sender.id
                    val userId = UserPreferences.getUser(requireContext())?.user?.id ?: return
                    chatViewModel.getHistory(userId, senderId)
                }
            }
        })


        chatViewModel.messages.observe(viewLifecycleOwner) { chatMessages ->
            Log.e("ChatMessages Update", "New messages: ${chatMessages.size}")

            val sortedMessages = chatMessages.sortedBy { parseTimestamp(it.timestamp) }

            val previousMessageCount = chatAdapter.itemCount
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()

            Log.e("Chat Messages", sortedMessages.toString())

            chatAdapter.submitList(sortedMessages) {
                recyclerView.post {
                    if (previousMessageCount == 0) {
                        // First load, scroll to bottom
                        recyclerView.scrollToPosition(chatMessages.size - 1)
                    } else if (firstVisiblePosition == 0) {
                        // Loading older messages, maintain position
                        val newMessageCount = chatMessages.size - previousMessageCount
                        recyclerView.scrollToPosition(newMessageCount)
                    } else {
                        // New message received, scroll to bottom
                        recyclerView.scrollToPosition(chatMessages.size - 1)
                    }
                }
            }
        }




        // ✅ Connect to WebSocket and subscribe to messages
        WebSocketManager.connect(
            onConnected = {
                if (userId != null) {
                    WebSocketManager.subscribeToPrivateMessages(userId, chatViewModel)
                }
            },
            onError = { error ->
                Log.e("WebSocket", "Connection failed: ${error.message}")
            }
        )

        // ✅ Send a message when clicking the send button
        sendButton.setOnClickListener {
            val messageContent = messageInput.text.toString()
            if (messageContent.isNotEmpty()) {
                val newMessage = userId?.let { it1 ->
                    ChatMessageRequest(
                        senderId = it1,
                        recipientId = sender.id,
                        content = messageContent,
                        timestamp = getFormattedTimestamp()
                    )
                }

                if (newMessage != null) {
                    chatViewModel.addMessage(newMessage, false)
                }  // Update UI immediately

                if (userId != null) {
                    WebSocketManager.sendMessageToUser(userId, sender.id, messageContent)
                }  // Send via WebSocket
                messageInput.text.clear()  // Clear input field
            }
        }
    }

    fun parseTimestamp(timestamp: String): Long {
        return try {
            timestamp.toLong() // ✅ Already a Unix timestamp in milliseconds
        } catch (e: NumberFormatException) {
            try {
                // ✅ Convert ISO 8601 string to milliseconds
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                sdf.parse(timestamp)?.time ?: 0L
            } catch (ex: Exception) {
                0L // Default to 0 if parsing fails
            }
        }
    }

    fun getFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(System.currentTimeMillis()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        WebSocketManager.disconnect()
    }
}
