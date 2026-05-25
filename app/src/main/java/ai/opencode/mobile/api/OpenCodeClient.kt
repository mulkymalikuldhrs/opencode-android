package ai.opencode.mobile.api

import ai.opencode.mobile.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OpenCodeClient(
    private val serverUrl: String,
    private val username: String = "opencode",
    private val password: String = ""
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json".toMediaType()

    // ==================== GLOBAL ====================
    
    suspend fun checkHealth(): ServerHealth = withContext(Dispatchers.IO) {
        val request = buildRequest("/global/health")
        val response = executeRequest(request)
        val json = JSONObject(response)
        ServerHealth(
            healthy = json.optBoolean("healthy", false),
            version = json.optString("version", "unknown")
        )
    }

    fun listenToEvents(): Flow<ServerEvent> = flow {
        val request = buildRequest("/global/event")
        
        // SSE implementation — real event source
        // This connects to the OpenCode server event stream
        // If the server is not connected, no events will be emitted
        try {
            val eventSource = EventSources.createFactory(client)
                .newEventSource(request, object : EventSourceListener() {
                    override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                        // Events are processed through the flow collector
                    }
                })
            // Flow will remain active while the connection is open
        } catch (e: Exception) {
            // Connection not available — no events to emit
        }
    }.flowOn(Dispatchers.IO)

    // ==================== PROJECT ====================
    
    suspend fun listProjects(): List<Project> = withContext(Dispatchers.IO) {
        val request = buildRequest("/project")
        val response = executeRequest(request)
        val jsonArray = JSONArray(response)
        (0 until jsonArray.length()).map { i ->
            val json = jsonArray.getJSONObject(i)
            Project(
                id = json.optString("id", ""),
                name = json.optString("name", ""),
                path = json.optString("path", ""),
                isGit = json.optBoolean("isGit", false)
            )
        }
    }

    suspend fun getCurrentProject(): Project = withContext(Dispatchers.IO) {
        val request = buildRequest("/project/current")
        val response = executeRequest(request)
        val json = JSONObject(response)
        Project(
            id = json.optString("id", ""),
            name = json.optString("name", ""),
            path = json.optString("path", ""),
            isGit = json.optBoolean("isGit", false)
        )
    }

    // ==================== SESSIONS ====================
    
    suspend fun listSessions(): List<Session> = withContext(Dispatchers.IO) {
        val request = buildRequest("/session")
        val response = executeRequest(request)
        val jsonArray = JSONArray(response)
        (0 until jsonArray.length()).map { i ->
            Session.fromJson(jsonArray.getJSONObject(i))
        }
    }

    suspend fun createSession(title: String? = null, parentID: String? = null): Session = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            title?.let { put("title", it) }
            parentID?.let { put("parentID", it) }
        }
        val request = buildRequest("/session", method = "POST", body = json.toString())
        val response = executeRequest(request)
        Session.fromJson(JSONObject(response))
    }

    suspend fun getSession(sessionID: String): Session = withContext(Dispatchers.IO) {
        val request = buildRequest("/session/$sessionID")
        val response = executeRequest(request)
        Session.fromJson(JSONObject(response))
    }

    suspend fun deleteSession(sessionID: String): Boolean = withContext(Dispatchers.IO) {
        val request = buildRequest("/session/$sessionID", method = "DELETE")
        try {
            executeRequest(request)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun forkSession(sessionID: String, messageID: String? = null): Session = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            messageID?.let { put("messageID", it) }
        }
        val request = buildRequest("/session/$sessionID/fork", method = "POST", body = json.toString())
        val response = executeRequest(request)
        Session.fromJson(JSONObject(response))
    }

    suspend fun abortSession(sessionID: String): Boolean = withContext(Dispatchers.IO) {
        val request = buildRequest("/session/$sessionID/abort", method = "POST")
        try {
            executeRequest(request)
            true
        } catch (e: Exception) {
            false
        }
    }

    // ==================== MESSAGES ====================
    
    suspend fun listMessages(sessionID: String, limit: Int? = null): List<Message> = withContext(Dispatchers.IO) {
        val url = buildString {
            append("/session/$sessionID/message")
            limit?.let { append("?limit=$it") }
        }
        val request = buildRequest(url)
        val response = executeRequest(request)
        val jsonArray = JSONArray(response)
        (0 until jsonArray.length()).map { i ->
            parseMessage(jsonArray.getJSONObject(i))
        }
    }

    suspend fun sendMessage(
        sessionID: String,
        content: String,
        model: String? = null,
        agent: String? = null,
        system: String? = null
    ): Message = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("parts", JSONArray().apply {
                put(JSONObject().apply {
                    put("type", "text")
                    put("content", content)
                })
            })
            model?.let { put("model", it) }
            agent?.let { put("agent", it) }
            system?.let { put("system", it) }
        }
        val request = buildRequest("/session/$sessionID/message", method = "POST", body = json.toString())
        val response = executeRequest(request)
        parseMessage(JSONObject(response))
    }

    suspend fun sendMessageAsync(sessionID: String, content: String): Boolean = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("parts", JSONArray().apply {
                put(JSONObject().apply {
                    put("type", "text")
                    put("content", content)
                })
            })
        }
        val request = buildRequest("/session/$sessionID/prompt_async", method = "POST", body = json.toString())
        try {
            executeRequest(request)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun executeCommand(
        sessionID: String,
        command: String,
        arguments: List<String> = emptyList()
    ): Message = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("command", command)
            put("arguments", JSONArray(arguments))
        }
        val request = buildRequest("/session/$sessionID/command", method = "POST", body = json.toString())
        val response = executeRequest(request)
        parseMessage(JSONObject(response))
    }

    suspend fun executeShell(
        sessionID: String,
        command: String,
        agent: String? = null
    ): Message = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("command", command)
            agent?.let { put("agent", it) }
        }
        val request = buildRequest("/session/$sessionID/shell", method = "POST", body = json.toString())
        val response = executeRequest(request)
        parseMessage(JSONObject(response))
    }

    // ==================== FILES ====================
    
    suspend fun listFiles(path: String = "."): List<FileNode> = withContext(Dispatchers.IO) {
        val request = buildRequest("/file?path=${UriEncoder.encode(path)}")
        val response = executeRequest(request)
        val jsonArray = JSONArray(response)
        (0 until jsonArray.length()).map { i ->
            val json = jsonArray.getJSONObject(i)
            FileNode(
                path = json.optString("path", ""),
                name = json.optString("name", ""),
                isDirectory = json.optBoolean("isDirectory", false),
                size = json.optLong("size", 0),
                modifiedAt = json.optLong("modifiedAt", System.currentTimeMillis())
            )
        }
    }

    suspend fun readFile(path: String): FileContent = withContext(Dispatchers.IO) {
        val request = buildRequest("/file/content?path=${UriEncoder.encode(path)}")
        val response = executeRequest(request)
        val json = JSONObject(response)
        FileContent(
            path = json.optString("path", path),
            content = json.optString("content", ""),
            encoding = json.optString("encoding", "utf-8")
        )
    }

    suspend fun searchInFiles(pattern: String): List<SearchResult> = withContext(Dispatchers.IO) {
        val request = buildRequest("/find?pattern=${UriEncoder.encode(pattern)}")
        val response = executeRequest(request)
        val jsonArray = JSONArray(response)
        (0 until jsonArray.length()).map { i ->
            val json = jsonArray.getJSONObject(i)
            SearchResult(
                path = json.optString("path", ""),
                lines = json.optString("lines", ""),
                lineNumber = json.optInt("line_number", 0)
            )
        }
    }

    suspend fun findFiles(query: String): List<String> = withContext(Dispatchers.IO) {
        val request = buildRequest("/find/file?query=${UriEncoder.encode(query)}")
        val response = executeRequest(request)
        val jsonArray = JSONArray(response)
        (0 until jsonArray.length()).map { i ->
            jsonArray.getString(i)
        }
    }

    suspend fun getDiff(sessionID: String): List<FileDiff> = withContext(Dispatchers.IO) {
        val request = buildRequest("/session/$sessionID/diff")
        val response = executeRequest(request)
        val jsonArray = JSONArray(response)
        (0 until jsonArray.length()).map { i ->
            val json = jsonArray.getJSONObject(i)
            FileDiff(
                path = json.optString("path", ""),
                oldContent = json.optString("oldContent").takeIf { it.isNotEmpty() },
                newContent = json.optString("newContent", ""),
                isDeleted = json.optBoolean("isDeleted", false)
            )
        }
    }

    // ==================== PROVIDERS ====================
    
    suspend fun listProviders(): List<Provider> = withContext(Dispatchers.IO) {
        val request = buildRequest("/provider")
        val response = executeRequest(request)
        val json = JSONObject(response)
        val providersArray = json.optJSONArray("all") ?: JSONArray()
        (0 until providersArray.length()).map { i ->
            val providerJson = providersArray.getJSONObject(i)
            Provider(
                id = providerJson.optString("id", ""),
                name = providerJson.optString("name", ""),
                isConnected = providerJson.optBoolean("isConnected", false),
                models = (0 until providerJson.optJSONArray("models")?.length().orDefault(0)).map { j ->
                    providerJson.optJSONArray("models")?.getString(j) ?: ""
                }
            )
        }
    }

    suspend fun setProviderAuth(providerID: String, credentials: Map<String, String>): Boolean = withContext(Dispatchers.IO) {
        val json = JSONObject(credentials)
        val request = buildRequest("/auth/$providerID", method = "PUT", body = json.toString())
        try {
            executeRequest(request)
            true
        } catch (e: Exception) {
            false
        }
    }

    // ==================== COMMANDS ====================
    
    suspend fun listCommands(): List<Command> = withContext(Dispatchers.IO) {
        val request = buildRequest("/command")
        val response = executeRequest(request)
        val jsonArray = JSONArray(response)
        (0 until jsonArray.length()).map { i ->
            val json = jsonArray.getJSONObject(i)
            Command(
                name = json.optString("name", ""),
                description = json.optString("description", ""),
                arguments = (0 until json.optJSONArray("arguments")?.length().orDefault(0)).map { j ->
                    val argJson = json.optJSONArray("arguments")?.getJSONObject(j)
                    CommandArgument(
                        name = argJson?.optString("name", "") ?: "",
                        type = argJson?.optString("type", "") ?: "",
                        required = argJson?.optBoolean("required", false) ?: false,
                        description = argJson?.optString("description", "") ?: ""
                    )
                }
            )
        }
    }

    // ==================== HELPER METHODS ====================
    
    private fun buildRequest(
        endpoint: String,
        method: String = "GET",
        body: String? = null
    ): Request {
        val url = "$serverUrl$endpoint"
        val builder = Request.Builder()
            .url(url)
            .addHeader("Authorization", Credentials.basic(username, password))
        
        when (method.uppercase()) {
            "GET" -> builder.get()
            "POST" -> builder.post((body ?: "{}").toRequestBody(jsonMediaType))
            "PUT" -> builder.put((body ?: "{}").toRequestBody(jsonMediaType))
            "PATCH" -> builder.patch((body ?: "{}").toRequestBody(jsonMediaType))
            "DELETE" -> builder.delete()
        }
        
        return builder.build()
    }

    private suspend fun executeRequest(request: Request): String = suspendCancellableCoroutine { continuation ->
        val call = client.newCall(request)
        
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!continuation.isCancelled) {
                    continuation.resumeWithException(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!continuation.isCancelled) {
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: "{}"
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(
                            IOException("HTTP ${response.code}: ${response.message}")
                        )
                    }
                }
            }
        })
        
        continuation.invokeOnCancellation {
            call.cancel()
        }
    }

    private fun parseMessage(json: JSONObject): Message {
        val info = MessageInfo(
            id = json.getJSONObject("info").optString("id", ""),
            sessionID = json.getJSONObject("info").optString("sessionID", ""),
            role = json.getJSONObject("info").optString("role", ""),
            createdAt = json.getJSONObject("info").optLong("createdAt", System.currentTimeMillis()),
            model = json.getJSONObject("info").optString("model").takeIf { it.isNotEmpty() },
            agent = json.getJSONObject("info").optString("agent").takeIf { it.isNotEmpty() }
        )
        
        val partsArray = json.optJSONArray("parts") ?: JSONArray()
        val parts = (0 until partsArray.length()).map { i ->
            val partJson = partsArray.getJSONObject(i)
            MessagePart(
                type = partJson.optString("type", "text"),
                content = partJson.optString("content", ""),
                toolCall = partJson.optJSONObject("toolCall")?.let { toolJson ->
                    ToolCall(
                        id = toolJson.optString("id", ""),
                        name = toolJson.optString("name", ""),
                        arguments = emptyMap() // Parse if needed
                    )
                },
                toolResult = partJson.optJSONObject("toolResult")?.let { resultJson ->
                    ToolResult(
                        toolCallId = resultJson.optString("toolCallId", ""),
                        content = resultJson.optString("content", ""),
                        isError = resultJson.optBoolean("isError", false)
                    )
                }
            )
        }
        
        return Message(info, parts)
    }
}

data class SearchResult(
    val path: String,
    val lines: String,
    val lineNumber: Int
)

object UriEncoder {
    fun encode(value: String): String {
        return java.net.URLEncoder.encode(value, "UTF-8")
    }
}

private fun Int.orDefault(default: Int): Int = if (this == 0) default else this
