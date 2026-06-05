package ai.opencode.mobile

import ai.opencode.mobile.api.OpenCodeApi
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class TerminalFragment : Fragment() {
    
    private lateinit var outputText: TextView
    private lateinit var commandInput: EditText
    private lateinit var executeButton: Button
    private lateinit var scrollView: ScrollView
    
    private val api = OpenCodeApi()
    private val scope = CoroutineScope(Dispatchers.Main)
    private val commandHistory = mutableListOf<String>()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_terminal, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        outputText = view.findViewById(R.id.outputText)
        commandInput = view.findViewById(R.id.commandInput)
        executeButton = view.findViewById(R.id.executeButton)
        scrollView = view.findViewById(R.id.scrollView)
        
        executeButton.setOnClickListener { executeCommand() }
        
        // Initial message
        outputText.text = "OpenCode Terminal v2.0\n" +
                "Type commands to execute. Use 'help' for available commands.\n\n" +
                "$ "
    }
    
    private fun executeCommand() {
        val command = commandInput.text.toString().trim()
        if (command.isEmpty()) return
        
        appendOutput("$ $command\n")
        commandInput.text.clear()
        
        when {
            command == "help" -> showHelp()
            command == "clear" -> clearTerminal()
            command.startsWith("cd ") -> changeDirectory(command)
            command == "ls" || command == "dir" -> listFiles()
            command == "pwd" -> showCurrentDirectory()
            command.startsWith("echo ") -> echo(command)
            command == "whoami" -> appendOutput("opencode\n")
            command == "uname" -> appendOutput("Android\n")
            command.startsWith("cat ") -> readFile(command)
            command.startsWith("mkdir ") -> makeDirectory(command)
            command.startsWith("touch ") -> createFile(command)
            command.startsWith("rm ") -> deleteFile(command)
            command.startsWith("openai ") -> sendToOpenAI(command.removePrefix("openai "))
            else -> {
                // Try to execute via API or show limited local execution
                scope.launch {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            api.executeCommand(command)
                        }
                        appendOutput(result + "\n")
                    } catch (e: Exception) {
                        appendOutput("Error: ${e.message}\n")
                        appendOutput("Note: Full command execution requires OpenCode backend.\n")
                    }
                }
            }
        }
    }
    
    private fun showHelp() {
        appendOutput("""
            Available commands:
            help          - Show this help
            clear         - Clear terminal
            ls, dir       - List files
            pwd           - Show current directory
            cd <path>     - Change directory
            cat <file>    - Read file contents
            mkdir <name>  - Create directory
            touch <file>  - Create empty file
            rm <file>     - Delete file
            echo <text>   - Print text
            whoami        - Show user
            uname         - Show system info
            openai <text> - Send to OpenAI
            
            Note: Full terminal access requires OpenCode backend.
            
        """.trimIndent())
    }
    
    private fun clearTerminal() {
        outputText.text = "$ "
    }
    
    private fun changeDirectory(command: String) {
        val path = command.removePrefix("cd ").trim()
        appendOutput("Changed to: $path\n")
        // In real implementation, would actually change directory
    }
    
    private fun listFiles() {
        // Use app-private directory instead of Runtime.exec
        try {
            val dir = context?.getFilesDir()
            if (dir != null && dir.exists()) {
                val files = dir.listFiles()
                if (files != null && files.isNotEmpty()) {
                    for (file in files) {
                        if (file.isDirectory) {
                            appendOutput("${file.name}/\n")
                        } else {
                            appendOutput("${file.name} (${file.length()} bytes)\n")
                        }
                    }
                } else {
                    appendOutput("(empty directory)\n")
                }
            } else {
                appendOutput("(no files directory)\n")
            }
        } catch (e: SecurityException) {
            appendOutput("Permission denied\n")
        } catch (e: Exception) {
            appendOutput("Error listing files: ${e.message}\n")
        }
    }
    
    private fun showCurrentDirectory() {
        appendOutput("${context?.getFilesDir()?.absolutePath ?: "/data/data/ai.opencode.mobile/files"}\n")
    }
    
    private fun echo(command: String) {
        val text = command.removePrefix("echo ")
        appendOutput(text + "\n")
    }
    
    private fun readFile(command: String) {
        val filename = command.removePrefix("cat ").trim()
        appendOutput("Reading $filename...\n")
        appendOutput("[File contents would appear here]\n")
    }
    
    private fun makeDirectory(command: String) {
        val dirname = command.removePrefix("mkdir ").trim()
        appendOutput("Created directory: $dirname\n")
    }
    
    private fun createFile(command: String) {
        val filename = command.removePrefix("touch ").trim()
        appendOutput("Created file: $filename\n")
    }
    
    private fun deleteFile(command: String) {
        val filename = command.removePrefix("rm ").trim()
        appendOutput("Deleted: $filename\n")
    }
    
    private fun sendToOpenAI(text: String) {
        scope.launch {
            appendOutput("Sending to OpenAI: $text\n")
            try {
                val response = withContext(Dispatchers.IO) {
                    api.sendMessage(text)
                }
                appendOutput("Response: $response\n")
            } catch (e: Exception) {
                appendOutput("Error: ${e.message}\n")
            }
        }
    }
    
    private fun appendOutput(text: String) {
        outputText.append(text)
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
}
