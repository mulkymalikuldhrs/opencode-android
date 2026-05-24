<div align="center">

<a href="https://github.com/mulkymalikuldhrs/opencode-android">
<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=600&size=28&duration=3000&pause=1000&color=8B5CF6&center=true&vCenter=true&multiline=false&repeat=true&width=500&height=50&lines=Opencode+Android;AI+Coding+Agent+Client;Code+%E2%80%A2+Chat+%E2%80%A2+Terminal" alt="Typing SVG" />
</a>

<br/>

[![Version](https://img.shields.io/badge/version-1.0.0-8B5CF6?style=for-the-badge&logo=semver)](https://github.com/mulkymalikuldhrs/opencode-android)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-7.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)
[![Stars](https://img.shields.io/github/stars/mulkymalikuldhrs/opencode-android?style=for-the-badge&logo=github&color=yellow)](https://github.com/mulkymalikuldhrs/opencode-android/stargazers)
[![GitHub](https://img.shields.io/badge/GitHub-mulkymalikuldhrs-181717?style=for-the-badge&logo=github)](https://github.com/mulkymalikuldhrs/opencode-android)

<br/>

**Independent Android client for OpenCode AI coding agent — chat, terminal, file manager, and code editor in one native app.**

[🐛 Report Bug](https://github.com/mulkymalikuldhrs/opencode-android/issues) &bull; [✨ Request Feature](https://github.com/mulkymalikuldhrs/opencode-android/issues)

</div>

---

## 🇬🇧 English

### ✨ Overview

Opencode Android is a native Android client for the OpenCode AI coding agent. It connects to an OpenCode server and provides real-time chat with AI, terminal access, file browsing, and a code editor — all in a beautiful Material Design 3 dark theme. With SSE streaming for instant AI responses and support for 75+ LLM providers, it's the most powerful mobile coding companion.

### 🏗️ Architecture

```
┌────────────────────────────────────────────────────────────┐
│                Opencode Android Architecture                │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ┌──────────┐    ┌──────────────┐    ┌──────────────────┐ │
│  │  Kotlin  │    │   OkHttp     │    │   OpenCode       │ │
│  │  App     │───▶│   + SSE      │───▶│   Server API     │ │
│  │  (MD3)   │    │   Client     │    │   (50+ Endpoints)│ │
│  └──────────┘    └──────────────┘    └──────────────────┘ │
│       │                                       │            │
│       ▼                                       ▼            │
│  ┌──────────┐    ┌──────────────┐    ┌──────────────────┐ │
│  │  Chat +  │    │  Terminal +  │    │  Code Editor +   │ │
│  │  Session │    │  Commands    │    │  File Manager    │ │
│  └──────────┘    └──────────────┘    └──────────────────┘ │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

### 🎯 Features

| Feature | Description |
|---------|-------------|
| 💬 **Real-Time Chat** | SSE streaming for instant AI responses |
| 🖥️ **Terminal Access** | Execute shell commands on server |
| 📁 **File Manager** | Browse, read, search, and manage project files |
| ✏️ **Code Editor** | Full-featured code editing with syntax highlighting |
| 🤖 **Multi-Provider** | OpenAI, Anthropic Claude, 75+ LLM providers |
| 🔄 **Session Management** | Create, fork, switch between sessions |
| 📡 **Auto-Reconnection** | Maintains connection stability |
| 🎨 **Material Design 3** | Beautiful dark theme with gradient accents |
| 📱 **4-Tab Navigation** | Chat, Terminal, Files, Editor |

### 🚀 Quick Start

```bash
# Clone the repository
git clone https://github.com/mulkymalikuldhrs/opencode-android.git

# Open in Android Studio
# Sync Gradle → Build → Run

# Or build APK
./build.sh
```

**Backend Required:** Install OpenCode server first:
```bash
npm install -g opencode-ai
opencode serve --port 4096
```

---

## 🇮🇩 Bahasa Indonesia

### ✨ Gambaran Umum

Opencode Android adalah klien Android native untuk agen coding AI OpenCode. Terhubung ke server OpenCode dan menyediakan chat real-time dengan AI, akses terminal, penelusuran file, dan editor kode — semuanya dalam tema gelap Material Design 3 yang indah. Dengan streaming SSE untuk respons AI instan dan dukungan 75+ penyedia LLM, ini adalah pendamping coding mobile paling powerful.

### 🎯 Fitur Utama

| Fitur | Deskripsi |
|-------|-----------|
| 💬 **Chat Real-Time** | Streaming SSE untuk respons AI instan |
| 🖥️ **Akses Terminal** | Eksekusi perintah shell di server |
| 📁 **Manajer File** | Telusuri, baca, cari, dan kelola file proyek |
| ✏️ **Editor Kode** | Pengeditan kode lengkap dengan penyorotan sintaks |
| 🤖 **Multi-Provider** | OpenAI, Anthropic Claude, 75+ penyedia LLM |
| 🔄 **Manajemen Sesi** | Buat, fork, dan alihkan antar sesi |

### 🚀 Mulai Cepat

```bash
git clone https://github.com/mulkymalikuldhrs/opencode-android.git
# Buka di Android Studio → Sync Gradle → Build → Run
```

---

## 🇨🇳 中文

### ✨ 概述

Opencode Android 是 OpenCode AI 编程代理的原生 Android 客户端。它连接到 OpenCode 服务器，提供与 AI 的实时聊天、终端访问、文件浏览和代码编辑器——全部采用精美的 Material Design 3 暗色主题。通过 SSE 流式传输实现即时 AI 响应，支持 75+ LLM 提供商。

### 🎯 主要功能

| 功能 | 描述 |
|------|------|
| 💬 **实时聊天** | SSE 流式传输实现即时 AI 响应 |
| 🖥️ **终端访问** | 在服务器上执行 Shell 命令 |
| 📁 **文件管理器** | 浏览、读取、搜索和管理项目文件 |
| ✏️ **代码编辑器** | 具有语法高亮的完整代码编辑 |
| 🤖 **多提供商** | OpenAI、Anthropic Claude、75+ LLM 提供商 |
| 🔄 **会话管理** | 创建、分支、切换会话 |

### 🚀 快速开始

```bash
git clone https://github.com/mulkymalikuldhrs/opencode-android.git
# 在 Android Studio 中打开 → 同步 Gradle → 构建 → 运行
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|-----------|---------|
| ![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?logo=kotlin&logoColor=white) | Language |
| ![Android](https://img.shields.io/badge/Android-7.0+-3DDC84?logo=android&logoColor=white) | Platform |
| ![OkHttp](https://img.shields.io/badge/OkHttp-4-000000?logo=square) | HTTP/SSE Client |
| ![Material Design](https://img.shields.io/badge/Material_Design-3-757575?logo=material-design) | UI Design |
| ![Coroutines](https://img.shields.io/badge/Coroutines-Flow-7F52FF) | Async |

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) for details.

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

## 👤 Author

**Mulky Malikul Dhaher**

[![GitHub](https://img.shields.io/badge/GitHub-mulkymalikuldhrs-181717?style=flat&logo=github)](https://github.com/mulkymalikuldhrs)
[![Email](https://img.shields.io/badge/Email-mulkymalikuldhaher@email.com-EA4335?style=flat&logo=gmail&logoColor=white)](mailto:mulkymalikuldhaher@email.com)

---

## ⚠️ Disclaimer

### 🇬🇧 English

> **⚠️ For Education Purpose Only**
> This project is provided strictly for educational and research purposes. The authors and contributors assume **no responsibility or liability** for any damages, losses, or risks arising from the use of this software. **We do not bear any responsibility or risk** for how this software is used.
> **Contact:** Mulky Malikul Dhaher | mulkymalikuldhaher@email.com

### 🇮🇩 Bahasa Indonesia

> **⚠️ Hanya untuk Tujuan Pendidikan**
> Proyek ini disediakan secara ketat untuk tujuan pendidikan dan penelitian. Penulis dan kontributor **tidak bertanggung jawab atau berkewajiban** atas kerusakan, kerugian, atau risiko yang timbul dari penggunaan perangkat lunak ini. **Kami tidak menanggung tanggung jawab atau risiko** apa pun untuk penggunaan perangkat lunak ini.
> **Kontak:** Mulky Malikul Dhaher | mulkymalikuldhaher@email.com

### 🇨🇳 中文

> **⚠️ 仅供教育目的**
> 本项目严格仅供教育和研究目的提供。作者和贡献者对因使用本软件而产生的任何损害、损失或风险**不承担任何责任或义务**。**我们不承担任何责任或风险**对于本软件的使用方式。
> **联系方式:** Mulky Malikul Dhaher | mulkymalikuldhaher@email.com

---

<div align="center">

Made with ❤️ by Mulky Malikul Dhaher

**For Education Purpose Only**

</div>
