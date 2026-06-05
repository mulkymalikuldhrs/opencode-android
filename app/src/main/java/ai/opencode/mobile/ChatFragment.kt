package ai.opencode.mobile

import ai.opencode.mobile.api.OpenCodeClient
import ai.opencode.mobile.manager.SessionManager
import ai.opencode.mobile.model.*
import ai.opencode.mobile.security.SecureStorage
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var newSessionButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: MessageAdapter
    
    private var client: OpenCodeClient? = null
    private val sessionManager = SessionManager.getInstance()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.recyclerView)
        messageInput = view.findViewById(R.id.messageInput)
        sendButton = view.findViewById(R.id.sendButton)
        newSessionButton = view.findViewById(R.id.newSessionButton)
        progressBar = view.findViewById(R.id.progressBar)
        
        adapter = MessageAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        
        sendButton.setOnClickListener { sendMessage() }
        newSessionButton.setOnClickListener { createNewSession() }
        
        // Observe current session
        lifecycleScope.launch {
            sessionManager.currentSession.collectLatest { session ->
                if (session != null) {
                    loadSessionMessages(session.id)
                } else {
                    adapter.updateMessages(emptyList())
                }
            }
        }
        
        // Initialize client if connected (use encrypted storage)
        val serverUrl = context?.let { SecureStorage.getServerUrl(it) }
        val password = context?.let { SecureStorage.getServerPassword(it) } ?: ""
        
        if (serverUrl != null) {
            client = OpenCodeClient(serverUrl, password = password)
            initializeClient()
        }
    }
    
    fun setClient(newClient: OpenCodeClient) {
        client = newClient
        initializeClient()
    }
    
    private fun initializeClient() {
        lifecycleScope.launch {
            try {
                // Check server health
                val health = client?.checkHealth()
                if (health?.healthy == true) {
                    // Load existing sessions
                    val sessions = client?.listSessions()
                    sessions?.let { sessionManager.setSessions(it) }
                    
                    // Create or use existing session
                    if (sessionManager.sessions.value.isEmpty()) {
                        createNewSession()
                    } else {
                        sessionManager.setCurrentSession(sessionManager.sessions.value.first())
                    }
                    
                    // Connect to event stream
                    connectToEventStream()
                    
                    Toast.makeText(context, "Connected to OpenCode v${health.version}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to connect: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun connectToEventStream() {
        val serverUrl = context?.let { SecureStorage.getServerUrl(it) } ?: return
        val password = context?.let { SecureStorage.getServerPassword(it) } ?: ""
        
        sessionManager.connectToEventStream(
            serverUrl = serverUrl,
            username = "opencode",
            password = password,
            onEvent = { event ->
                handleServerEvent(event)
            },
            onError = { error ->
                activity?.runOnUiThread {
                    Toast.makeText(context, "Event stream error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    
    private fun handleServerEvent(event: ServerEvent) {
        when (event.type) {
            "session.status" -> {
                val sessionID = event.data.optString("sessionID")
                val status = event.data.optString("status")
                sessionManager.updateSessionStatus(sessionID, SessionStatus.fromString(status))
            }
            "message" -> {
                val sessionID = event.data.optString("sessionID")
                // Parse and add message
            }
            "server.connected" -> {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Real-time connection established", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun createNewSession() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val session = client?.createSession(title = "Mobile Session")
                session?.let {
                    sessionManager.addSession(it)
                    sessionManager.setCurrentSession(it)
                    Toast.makeText(context, "New session created", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun loadSessionMessages(sessionID: String) {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val messages = client?.listMessages(sessionID, limit = 100)
                messages?.let {
                    activity?.runOnUiThread {
                        adapter.updateMessages(it)
                        scrollToBottom()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load messages: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun sendMessage() {
        val text = messageInput.text.toString().trim()
        if (text.isEmpty()) return
        
        val session = sessionManager.currentSession.value
        if (session == null) {
            Toast.makeText(context, "No active session", Toast.LENGTH_SHORT).show()
            return
        }
        
        messageInput.text.clear()
        
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                
                // Send message and get response
                val response = client?.sendMessage(
                    sessionID = session.id,
                    content = text
                )
                
                response?.let { message ->
                    sessionManager.addMessage(session.id, message)
                    activity?.runOnUiThread {
                        val currentMessages = adapter.getMessages().toMutableList()
                        currentMessages.add(message)
                        adapter.updateMessages(currentMessages)
                        scrollToBottom()
                    }
                }
                
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun scrollToBottom() {
        recyclerView.post {
            recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        sessionManager.disconnectEventStream()
    }
}
