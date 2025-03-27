package com.avocado.glamping.di

import android.util.Log
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.data.model.req.ChatMessageRequest
import com.avocado.glamping.viewmodel.ChatViewModel
import com.google.gson.Gson
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WebSocketManager {
    private val client = OkHttpClient()
    private var webSocket : WebSocket? = null
    private val WEBSOCKET_URL = "ws://${BuildConfig.WEBSOCKET_URL}/ws/websocket"
    private var stompClient : StompClient? = null
    private var notificationSubscription: Disposable? = null
    private var privateMessageSubscription: Disposable? = null
    private var lifecycleSubscription: Disposable? = null
    private var sendMessageDisposable: Disposable? = null


    fun connect(onConnected: () -> Unit, onError: (Throwable) -> Unit) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL)
        stompClient?.connect()

        lifecycleSubscription = stompClient?.lifecycle()?.subscribe { event ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("WebSocket", "✅ Connected to WebSocket")
                    onConnected()
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.e("WebSocket", "⚠️ WebSocket Error: ${event.exception?.message}")
                    onError(event.exception ?: Exception("Unknown error"))
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.d("WebSocket", "❌ Disconnected from WebSocket")
                }
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                    Log.w("WebSocket", "⚠️ Server heartbeat failed")
                }
            }
        }
    }


    fun subscribeToNotifications(callback: (String) -> Unit) {
        if (stompClient != null) {
            notificationSubscription?.dispose() // Unsubscribe if already subscribed
            notificationSubscription = stompClient?.topic("/topic/notifications")?.subscribe { stompMessage ->
                Log.d("WebSocket", "🔔 Notification received: ${stompMessage.payload}")
                callback(stompMessage.payload)
            }
        } else {
            Log.e("WebSocket", "⚠️ WebSocket is not connected.")
        }
    }

    fun subscribeToPrivateMessages(userId: Int, viewModel: ChatViewModel) {
        if (stompClient != null) {
            privateMessageSubscription?.dispose()
            privateMessageSubscription = stompClient?.topic("/topic/private.$userId")?.subscribe({ stompMessage ->
                Log.d("WebSocket", "📩 Private message received: ${stompMessage.payload}")

                val receivedMessage = Gson().fromJson(stompMessage.payload, ChatMessageRequest::class.java)

                // Update ViewModel so RecyclerView gets updated
                viewModel.addMessage(receivedMessage, true)
            }, { throwable ->
                Log.e("WebSocket", "⚠️ Error subscribing to messages", throwable)
            })
        } else {
            Log.e("WebSocket", "⚠️ WebSocket is not connected.")
        }
    }


    fun sendMessageToUser(senderId: Int, recipientId: Int, content: String) {
        if (stompClient != null) {
            val message = """{"senderId": "$senderId", "recipientId": "$recipientId", "content": "$content"}"""

            sendMessageDisposable = stompClient?.send("/app/sendToUser", message)
                ?.subscribe({
                    Log.d("WebSocket", "📤 Sent message: $message")
                }, { error ->
                    Log.e("WebSocket", "⚠️ Error sending message: ${error.message}")
                })
        } else {
            Log.e("WebSocket", "⚠️ WebSocket is not connected.")
        }
    }

    fun disconnect() {
        stompClient?.disconnect()
        notificationSubscription?.dispose()
        privateMessageSubscription?.dispose()
    }
}