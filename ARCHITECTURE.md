# Architecture Documentation — OpenCode Android

This document provides a comprehensive overview of the OpenCode Android application architecture, including its design principles, component structure, data flow, and technical decisions.

---

## Table of Contents

- [Overview](#overview)
- [Design Principles](#design-principles)
- [High-Level Architecture](#high-level-architecture)
- [Component Breakdown](#component-breakdown)
- [Data Flow](#data-flow)
- [API Client Layer](#api-client-layer)
- [State Management](#state-management)
- [UI Layer](#ui-layer)
- [Background Services](#background-services)
- [Security Architecture](#security-architecture)
- [Build Configuration](#build-configuration)
- [Project Structure](#project-structure)
- [Dependencies](#dependencies)
- [Related Projects](#related-projects)

---

## Overview

OpenCode Android is a native Android client for the [OpenCode](https://opencode.ai) AI coding assistant platform. The application connects to an OpenCode server instance (running on the same device via Termux or on a remote machine) and provides mobile access to powerful AI-powered coding capabilities including chat, terminal access, file management, and code editing.

The architecture follows a **client-server model** where the Android app acts as a thin client that communicates with the OpenCode backend over HTTP, using SSE (Server-Sent Events) for real-time streaming updates. The app implements 50+ API endpoints covering sessions, messages, files, providers, commands, and more.

**Key Characteristics:**
- Written in 100% Kotlin
- Targets Android 7.0 (API 24) and above
- Minimum SDK: 24 | Target SDK: 34
- Package: `ai.opencode.mobile`
- Asynchronous by design using Kotlin Coroutines and Flow

---

## Design Principles

1. **Client-Server Separation** — The Android app is strictly a client. All AI processing, file operations, and command execution happen on the OpenCode server. The app is responsible only for UI presentation and user interaction.

2. **Asynchronous First** — All network operations use Kotlin coroutines with `Dispatchers.IO` to ensure the main thread remains responsive. SSE event streams use Kotlin Flow for reactive data delivery.

3. **Type Safety** — All API responses are parsed into strongly-typed Kotlin data classes. JSON parsing uses the built-in `org.json` library with safe accessors (`optString`, `optBoolean`) to handle missing or malformed fields gracefully.

4. **Material Design 3** — The UI follows Google's Material Design 3 guidelines with a dark theme as the primary visual identity. Navigation is bottom-tab based with four primary sections.

5. **Resilience** — The app implements auto-reconnection logic, timeout handling, and user-friendly error messages to handle the inherent instability of mobile network connections.

6. **Minimal Dependencies** — The project intentionally keeps its dependency footprint small, using OkHttp for HTTP/SSE, AndroidX libraries for UI, and Kotlin coroutines for async work.

---

## High-Level Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Android Application                 │
│                                                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────┐│
│  │  Chat     │  │ Terminal  │  │  File     │  │Code  ││
│  │  Fragment │  │ Fragment  │  │  Manager  │  │Editor││
│  │           │  │           │  │  Fragment │  │Frag. ││
│  └─────┬─────┘  └─────┬─────┘  └─────┬────┘  └──┬───┘│
│        │              │              │           │     │
│  ┌─────┴──────────────┴──────────────┴───────────┴──┐  │
│  │              Session Manager                       │  │
│  │         (State Management & Coordination)          │  │
│  └─────────────────────┬────────────────────────────┘  │
│                        │                               │
│  ┌─────────────────────┴────────────────────────────┐  │
│  │              OpenCode Client                       │  │
│  │         (HTTP + SSE Communication Layer)           │  │
│  └─────────────────────┬────────────────────────────┘  │
│                        │                               │
│  ┌─────────────────────┴────────────────────────────┐  │
│  │              OkHttp + SSE                          │  │
│  │         (Network Transport Layer)                  │  │
│  └─────────────────────┬────────────────────────────┘  │
│                        │                               │
│  ┌─────────────────────┴────────────────────────────┐  │
│  │           OpenCode Service (Background)            │  │
│  └───────────────────────────────────────────────────┘  │
└────────────────────────┬──────────────────────────────┘
                         │ HTTP/SSE
                         ▼
┌────────────────────────────────────────────────────────┐
│              OpenCode Server (Backend)                  │
│         (Node.js, running on port 4096)                 │
│                                                         │
│  ┌─────────┐ ┌──────────┐ ┌─────────┐ ┌─────────────┐ │
│  │ Sessions │ │ Messages │ │  Files  │ │  LLM APIs   │ │
│  │ & Chat   │ │ & Shell  │ │ & VCS   │ │ (75+ models)│ │
│  └─────────┘ └──────────┘ └─────────┘ └─────────────┘ │
└────────────────────────────────────────────────────────┘
```

---

## Component Breakdown

### Activities

| Component | File | Description |
|-----------|------|-------------|
| **ConnectionActivity** | `ConnectionActivity.kt` | Entry point. Handles the connection wizard where users enter server URL and credentials. Performs health check before connecting. |
| **MainActivity** | `MainActivity.kt` | Main container activity. Hosts the bottom navigation and manages fragment transactions for the four primary tabs. |

### Fragments

| Component | File | Description |
|-----------|------|-------------|
| **ChatFragment** | `ChatFragment.kt` | Primary chat interface. Displays message history with user/assistant/system message bubbles. Supports SSE streaming for real-time AI responses. Session switching and creation. |
| **TerminalFragment** | `TerminalFragment.kt` | Terminal emulator interface. Sends shell commands to the OpenCode server via the shell API. Displays command output in monospace font. Supports command history navigation. |
| **FileManagerFragment** | `FileManagerFragment.kt` | File browser and manager. Navigates the project directory tree on the server. Supports file search by name and content. Displays file metadata (size, modification date). |
| **CodeEditorFragment** | `CodeEditorFragment.kt` | Code editor with syntax highlighting. Loads file content from the server, enables editing, and saves changes back. Uses WebView-based editor with HTML/JS for rendering. |

### API Layer

| Component | File | Description |
|-----------|------|-------------|
| **OpenCodeClient** | `api/OpenCodeClient.kt` | Primary HTTP client. Implements all 50+ API endpoints using OkHttp. Handles request building, authentication (HTTP Basic Auth), JSON parsing, and error handling. Uses coroutines for async operations. |
| **OpenCodeApi** | `api/OpenCodeApi.kt` | API interface definitions and endpoint constants. Provides a structured way to reference available API routes and their expected payloads. |

### Models

| Component | File | Description |
|-----------|------|-------------|
| **Models** | `model/Models.kt` | Core data classes: `ServerHealth`, `Session`, `FileNode`, `FileContent`, `FileDiff`, `Provider`, `Command`, `SearchResult`, `ServerEvent`, and more. All API response models are defined here with type-safe properties. |
| **Message** | `model/Message.kt` | Message-specific data classes: `Message`, `MessageInfo`, `MessagePart`, `ToolCall`, `ToolResult`. Supports the complex structure of OpenCode messages including text, tool calls, and tool results. |

### Managers

| Component | File | Description |
|-----------|------|-------------|
| **SessionManager** | `manager/SessionManager.kt` | Central state manager. Coordinates session lifecycle, event stream subscription, message delivery, and state synchronization across fragments. Uses StateFlow for reactive state updates. |

### Services

| Component | File | Description |
|-----------|------|-------------|
| **OpenCodeService** | `OpenCodeService.kt` | Android foreground service. Keeps the SSE connection alive when the app is in the background. Displays a persistent notification to comply with Android's background execution limits. |

### UI Adapters

| Component | File | Description |
|-----------|------|-------------|
| **MessageAdapter** | `MessageAdapter.kt` | RecyclerView adapter for chat messages. Handles different message types (user, assistant, system, error) with distinct view holders and layouts. |

---

## Data Flow

### Chat Message Flow

```
User types message in ChatFragment
        │
        ▼
ChatFragment.sendMessage()
        │
        ▼
OpenCodeClient.sendMessage(sessionID, content)
        │
        ▼
HTTP POST /session/:id/message
        │
        ▼
OpenCode Server processes message with AI
        │
        ▼
SSE Event: { type: "message", data: {...} }
        │
        ▼
SessionManager receives SSE event
        │
        ▼
StateFlow emits new message
        │
        ▼
ChatFragment collects Flow, updates RecyclerView
        │
        ▼
MessageAdapter renders message in UI
```

### Terminal Command Flow

```
User types command in TerminalFragment
        │
        ▼
TerminalFragment.executeCommand()
        │
        ▼
OpenCodeClient.executeShell(sessionID, command)
        │
        ▼
HTTP POST /session/:id/shell
        │
        ▼
OpenCode Server executes command in shell
        │
        ▼
SSE Event: { type: "command.output", data: {...} }
        │
        ▼
SessionManager receives SSE event
        │
        ▼
TerminalFragment updates output display
```

### File Browsing Flow

```
User navigates directory in FileManagerFragment
        │
        ▼
FileManagerFragment.listDirectory()
        │
        ▼
OpenCodeClient.listFiles(path)
        │
        ▼
HTTP GET /file?path=<path>
        │
        ▼
OpenCode Server reads directory
        │
        ▼
Response: FileNode[]
        │
        ▼
FileManagerFragment renders file list
```

---

## API Client Layer

The `OpenCodeClient` class is the heart of the application's communication with the OpenCode server. It is designed as a single class that encapsulates all API interactions.

### Endpoint Categories

| Category | Endpoints | Description |
|----------|-----------|-------------|
| **Global** | `/global/health`, `/global/event` | Health checks and real-time SSE event stream |
| **Project** | `/project`, `/project/current` | Project listing and current project info |
| **Sessions** | `/session`, `/session/:id`, `/session/:id/fork`, `/session/:id/abort`, `/session/:id/diff` | Full session CRUD and lifecycle management |
| **Messages** | `/session/:id/message`, `/session/:id/prompt_async`, `/session/:id/command`, `/session/:id/shell` | Message sending, command execution, and shell access |
| **Files** | `/file`, `/file/content`, `/find`, `/find/file` | File browsing, content reading, and search |
| **Providers** | `/provider`, `/provider/auth`, `/provider/:id/oauth/authorize` | LLM provider management and authentication |
| **Commands** | `/command` | Available command listing |
| **LSP** | `/lsp/status`, `/lsp/add` | Language Server Protocol integration |
| **MCP** | `/mcp/status`, `/mcp/add` | Model Context Protocol integration |
| **Auth** | `/auth/:id` | Authentication credential management |
| **TUI** | `/tui/append`, `/tui/submit`, `/tui/clear`, `/tui/execute` | Terminal UI control endpoints |

### Request Building

All HTTP requests are constructed via the `buildRequest()` helper method which:
- Prepends the server URL to the endpoint path
- Adds HTTP Basic Authentication headers
- Supports GET, POST, PUT, PATCH, and DELETE methods
- Automatically sets `application/json` content type for request bodies

### Error Handling

The `executeRequest()` method uses `suspendCancellableCoroutine` to bridge OkHttp's callback-based API with Kotlin coroutines. It:
- Converts successful responses (HTTP 2xx) to string bodies
- Throws `IOException` for non-successful responses with status code and message
- Supports coroutine cancellation (cancels the OkHttp call)
- Uses configurable timeouts (30s connect, 60s read, 30s write)

---

## State Management

### SessionManager

The `SessionManager` is the central state coordinator. It manages:

- **Current Session** — Tracks the active session ID and metadata
- **Session List** — Maintains a list of all available sessions
- **Event Stream** — Subscribes to the SSE event stream and dispatches events
- **Message State** — Collects and distributes messages for the active session
- **Connection State** — Tracks connection status (connected, disconnected, reconnecting)

State is exposed via Kotlin `StateFlow` and `SharedFlow`, allowing fragments to reactively collect and display updates without manual refresh mechanisms.

### SharedPreferences

Connection settings are persisted using Android's `SharedPreferences`:
- Server URL
- Username
- Password
- Auto-connect preference

These settings survive app restarts and are loaded when the app launches.

---

## UI Layer

### Navigation Architecture

The app uses a bottom navigation pattern with four primary tabs:

1. **Chat** (`ChatFragment`) — AI conversation interface
2. **Terminal** (`TerminalFragment`) — Shell command execution
3. **Files** (`FileManagerFragment`) — Project file browser
4. **Editor** (`CodeEditorFragment`) — Code editing with syntax highlighting

Navigation is managed by `MainActivity` using fragment transactions. The bottom navigation menu is defined in `bottom_nav_menu.xml`.

### Layout Architecture

Each fragment has a corresponding XML layout file:
- `fragment_chat.xml` — RecyclerView for messages + input bar
- `fragment_terminal.xml` — ScrollView for output + command input
- `fragment_file_manager.xml` — RecyclerView for file list + search bar
- `fragment_code_editor.xml` — WebView container for the code editor

Message items use differentiated layouts:
- `item_message_user.xml` — User message bubble (right-aligned)
- `item_message_assistant.xml` — AI response bubble (left-aligned)
- `item_message_system.xml` — System notification (centered)
- `item_message_error.xml` — Error message (red accent)

### Theming

The app uses a dark theme with Material Design 3:
- Background: `#0a0a0a` (near-black)
- Primary color: `#6366f1` (indigo/purple)
- Gradient accents for visual depth
- Custom drawable backgrounds for message bubbles and UI elements

---

## Background Services

### OpenCodeService

The `OpenCodeService` is an Android foreground service that keeps the SSE connection alive when the app goes to the background. This is necessary because:

1. **Android Background Restrictions** — Since Android 8.0, background services are limited. A foreground service with a persistent notification bypasses these restrictions.
2. **Session Keep-Alive** — Long-running AI tasks may take minutes to complete. The service ensures the connection stays open.
3. **Real-Time Updates** — SSE events need to be received even when the app is not in the foreground.

The service is declared in the manifest with `foregroundServiceType="dataSync"` and requires the `FOREGROUND_SERVICE` permission.

---

## Security Architecture

### Authentication

The app uses HTTP Basic Authentication to connect to the OpenCode server:

```kotlin
Credentials.basic(username, password)
```

Credentials are included in the `Authorization` header of every HTTP request. The server validates these credentials before processing any API call.

### Credential Storage

Connection credentials are stored in Android's `SharedPreferences`. For enhanced security on newer Android versions, the app uses `EncryptedSharedPreferences` when available to encrypt stored passwords at rest.

### Network Security

- **Cleartext Traffic** — Disabled (`android:usesCleartextTraffic="false"`) by default. A `network_security_config.xml` allows cleartext only to `localhost`/`127.0.0.1` for development. For production use with remote servers, HTTPS is enforced.
- **SSL/TLS** — OkHttp supports TLS out of the box. When connecting to HTTPS endpoints, standard certificate validation applies.
- **No Hardcoded Secrets** — The app contains no API keys or server credentials. All connection parameters are user-provided.

### Permissions

The app requests the following permissions:
- `INTERNET` — Network communication with OpenCode server
- `ACCESS_NETWORK_STATE` — Check network availability
- `WRITE_EXTERNAL_STORAGE` / `READ_EXTERNAL_STORAGE` — File access (legacy, SDK ≤ 32)
- `FOREGROUND_SERVICE` — Background service for keep-alive
- `POST_NOTIFICATIONS` — Foreground service notification (SDK ≥ 33)
- `WAKE_LOCK` — Prevent CPU sleep during long operations

---

## Build Configuration

### Gradle Setup

The project uses a standard Android Gradle build configuration:

| Property | Value |
|----------|-------|
| `compileSdk` | 34 |
| `minSdk` | 24 |
| `targetSdk` | 34 |
| `applicationId` | `ai.opencode.mobile` |
| `versionCode` | 2 |
| `versionName` | `2.0.0` |

### Build Variants

- **Debug** — No minification, debugging enabled
- **Release** — ProGuard/R8 minification enabled with `proguard-android-optimize.txt`

### Build Script

The `build.sh` script automates APK generation:
```bash
chmod +x gradlew
./gradlew assembleRelease
```

---

## Project Structure

```
opencode-android/
├── app/
│   ├── build.gradle                    # App-level Gradle configuration
│   ├── proguard-rules.pro              # ProGuard/R8 rules
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml      # App manifest
│           ├── assets/
│           │   └── index.html           # Code editor WebView HTML
│           ├── java/ai/opencode/mobile/
│           │   ├── MainActivity.kt       # Main activity
│           │   ├── ConnectionActivity.kt # Connection wizard
│           │   ├── ChatFragment.kt       # Chat interface
│           │   ├── TerminalFragment.kt   # Terminal interface
│           │   ├── FileManagerFragment.kt# File browser
│           │   ├── CodeEditorFragment.kt # Code editor
│           │   ├── MessageAdapter.kt     # Chat message adapter
│           │   ├── OpenCodeService.kt    # Background service
│           │   ├── api/
│           │   │   ├── OpenCodeApi.kt    # API definitions
│           │   │   └── OpenCodeClient.kt # HTTP client (50+ endpoints)
│           │   ├── manager/
│           │   │   └── SessionManager.kt # State management
│           │   └── model/
│           │       ├── Models.kt         # Data models
│           │       └── Message.kt        # Message models
│           └── res/
│               ├── color/                # Color state lists
│               ├── drawable/             # Icons, backgrounds, shapes
│               ├── layout/               # XML layouts (15 files)
│               ├── menu/                 # Navigation menus
│               └── values/               # Strings, colors, themes
├── build.gradle                         # Project-level Gradle config
├── settings.gradle                      # Gradle settings
├── gradle/wrapper/                      # Gradle wrapper
├── build.sh                             # Build automation script
├── setup-termux.sh                      # Termux setup script
├── start-opencode-termux.sh             # OpenCode launch script
├── README.md                            # English documentation
├── README_id.md                         # Bahasa Indonesia documentation
├── README_zh.md                         # Chinese documentation
├── CHANGELOG.md                         # Version history
├── CONTRIBUTING.md                      # Contribution guidelines
├── ARCHITECTURE.md                      # This file
└── LICENSE                              # MIT License
```

---

## Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| `androidx.appcompat` | 1.6.1 | Backward-compatible Android components |
| `com.google.android.material` | 1.11.0 | Material Design 3 components |
| `androidx.constraintlayout` | 2.1.4 | Flexible layout system |
| `androidx.swiperefreshlayout` | 1.1.0 | Pull-to-refresh gesture support |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.6.2 | Lifecycle-aware components |
| `androidx.core:core-ktx` | 1.12.0 | Kotlin extensions for Android APIs |
| `com.squareup.okhttp3:okhttp` | 4.12.0 | HTTP client and SSE support |
| `org.java-websocket` | 1.5.3 | WebSocket client (optional transport) |
| `org.json` | 20231013 | JSON parsing and serialization |

---

## Related Projects

- **[OpenCode](https://github.com/anomalyco/opencode)** — The core OpenCode AI coding agent server
- **[HermesQuantOS](https://github.com/mulkymalikuldhrs/HermesQuantOS)** — Related project by the same author
- **[Termux](https://termux.dev/)** — Android terminal emulator for running the OpenCode server locally

---

This architecture document is maintained alongside the codebase. If you notice any discrepancies or have suggestions for improvement, please open an issue or pull request at [https://github.com/mulkymalikuldhrs/opencode-android](https://github.com/mulkymalikuldhrs/opencode-android).
