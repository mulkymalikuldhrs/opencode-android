package ai.opencode.mobile

import ai.opencode.mobile.api.OpenCodeClient
import ai.opencode.mobile.security.SecureStorage
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ConnectionActivity : AppCompatActivity() {
    
    private lateinit var serverUrlInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var connectButton: Button
    private lateinit var progressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Migrate from plain SharedPreferences to encrypted storage (one-time)
        SecureStorage.migrateFromPlainPrefs(this)

        // Check if already connected
        val savedUrl = SecureStorage.getServerUrl(this)
        
        if (savedUrl != null) {
            // Skip to main activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_connection)
        
        serverUrlInput = findViewById(R.id.serverUrlInput)
        passwordInput = findViewById(R.id.passwordInput)
        connectButton = findViewById(R.id.connectButton)
        progressBar = findViewById(R.id.progressBar)
        
        connectButton.setOnClickListener { attemptConnection() }
    }
    
    private fun attemptConnection() {
        val serverUrl = serverUrlInput.text.toString().trim()
        val password = passwordInput.text.toString()
        
        if (serverUrl.isEmpty()) {
            Toast.makeText(this, "Please enter server URL", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate URL format
        if (!serverUrl.startsWith("https://") && !serverUrl.startsWith("http://")) {
            Toast.makeText(this, "URL must start with http:// or https://", Toast.LENGTH_SHORT).show()
            return
        }
        
        progressBar.visibility = ProgressBar.VISIBLE
        connectButton.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val client = OpenCodeClient(serverUrl, password = password)
                val health = client.checkHealth()
                
                if (health.healthy) {
                    // Save connection details to encrypted storage
                    SecureStorage.saveServerUrl(this@ConnectionActivity, serverUrl)
                    SecureStorage.saveServerPassword(this@ConnectionActivity, password)
                    
                    Toast.makeText(
                        this@ConnectionActivity, 
                        "Connected to OpenCode v${health.version}", 
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Start main activity
                    startActivity(Intent(this@ConnectionActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@ConnectionActivity, "Server not healthy", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ConnectionActivity, 
                    "Connection failed: ${e.message}", 
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                progressBar.visibility = ProgressBar.GONE
                connectButton.isEnabled = true
            }
        }
    }
}
