package ai.opencode.mobile.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OpenCodeApi {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val apiKey: String = System.getenv("OPENCODE_API_KEY") ?: ai.opencode.mobile.BuildConfig.OPENCODE_API_KEY
    private val baseUrl: String = System.getenv("OPENCODE_BASE_URL") ?: "https://api.opencode.ai/v1"
    
    suspend fun sendMessage(content: String): String {
        return try {
            // Try OpenCode API first
            val response = callOpenCodeApi(content)
            if (response != null) return response
            
            // Fallback to OpenAI-compatible API
            val fallbackResponse = callFallbackApi(content)
            if (fallbackResponse != null) return fallbackResponse
            
            // Final fallback: local response
            generateLocalResponse(content)
            
        } catch (e: Exception) {
            "Network error: ${e.message}\n\nNote: Full OpenCode functionality requires backend server. Please configure OPENCODE_API_KEY."
        }
    }
    
    private fun callOpenCodeApi(content: String): String? {
        return try {
            val json = JSONObject().apply {
                put("model", "opencode-v1")
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", content)
                    })
                })
                put("stream", false)
            }
            
            val request = Request.Builder()
                .url("$baseUrl/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()
            
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val jsonResponse = JSONObject(body)
                    jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun callFallbackApi(content: String): String? {
        // Try alternative APIs (OpenRouter, etc.)
        return try {
            val json = JSONObject().apply {
                put("model", "gpt-3.5-turbo")
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "You are OpenCode, an autonomous AI coding agent. Help users write code, debug issues, and build projects.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", content)
                    })
                })
            }
            
            val request = Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()
            
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val jsonResponse = JSONObject(body)
                    jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun generateLocalResponse(content: String): String {
        // Offline mode - no API available
        return "OpenCode Mobile is currently offline. The backend server is not reachable.\n\n" +
            "To get full AI capabilities, please:\n" +
            "1. Check your internet connection\n" +
            "2. Verify API key configuration (set OPENCODE_API_KEY)\n" +
            "3. Ensure OpenCode backend is accessible\n\n" +
            "Your message: \"$content\""
    }
    
    suspend fun executeCommand(command: String): String {
        return try {
            val json = JSONObject().apply {
                put("command", command)
                put("cwd", "/data/data/ai.opencode.mobile/files")
            }
            
            val request = Request.Builder()
                .url("$baseUrl/execute")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()
            
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string() ?: "No output"
                } else {
                    "Error: ${response.code}"
                }
            }
        } catch (e: Exception) {
            "Command execution requires OpenCode backend. Error: ${e.message}"
        }
    }
}
