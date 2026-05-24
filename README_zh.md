# 🤖 Opencode Android 客户端

<div align="center">

[![Opencode Android Logo](https://img.shields.io/badge/Opencode-Android-v1.0.0-purple)](https://github.com/mulkymalikuldhrs/opencode-android/releases)
[![许可证: MIT](https://img.shields.io/badge/许可证-MIT-green.svg)](LICENSE)
[![平台: Android](https://img.shields.io/badge/平台-Android-blue.svg)]()
[![GitHub Stars](https://img.shields.io/github/stars/mulkymalikuldhrs/opencode-android?style=social)](https://github.com/mulkymalikuldhrs/opencode-android/stargazers)

**⚠️ 独立 OpenCode Android 客户端 - 社区构建版本**

</div>

---

## ✨ 功能特性

### 🚀 核心能力
- **完整 OpenCode API 集成** - 连接 OpenCode 服务器（50+ 接口端点）
- **实时聊天** - SSE（服务器推送事件）流式传输，即时获取 AI 响应
- **会话管理** - 创建、分叉、切换会话
- **终端访问** - 在服务器上执行 Shell 命令
- **文件浏览器** - 读取、搜索和管理项目文件
- **代码编辑器** - 全功能代码编辑，支持语法高亮
- **多模型支持** - 支持 OpenAI、Anthropic Claude 等 75+ LLM 提供商

### 📱 Android 原生体验
- **Material Design 3** - 精美的暗色主题与渐变色彩
- **底部导航** - 直观的四标签页布局
- **协程** - 异步非阻塞操作
- **自动重连** - 保持连接稳定性
- **后台服务** - 保持会话活跃

### 🎯 开发者工具
- **真实 AI 响应** - 由 OpenCode 服务器驱动
- **文件差异追踪** - 查看跨会话的代码变更
- **命令执行** - 通过 OpenCode Shell API 执行命令
- **Git 集成** - 通过 OpenCode 进行版本控制操作
- **LSP 支持** - 语言服务器协议，提供代码智能
- **MCP 协议** - 模型上下文协议，用于高级集成

---

## 📸 应用截图

| 聊天界面 | 终端 | 文件管理器 | 代码编辑器 |
|:-----------:|:----------:|:------------:|:------------:|
| ![Chat](docs/screenshots/chat.png) | ![Terminal](docs/screenshots/terminal.png) | ![Files](docs/screenshots/files.png) | ![Editor](docs/screenshots/editor.png) |

*完整的 UI 实现，具有真实的 OpenCode 后端连接*

---

## 🚀 快速开始

### 前提条件

**Android 设备：**
- Android 7.0（API 24）或更高版本
- 推荐 2GB+ 内存
- 500MB+ 存储空间

**后端服务（必需）：**
- **方式一：Termux**（推荐 Android 用户使用）
  ```bash
  pkg update -y
  pkg install nodejs-lts -y
  npm i -g opencode-ai
  opencode serve --port 4096
  ```

- **方式二：PC/Mac/Linux**
  ```bash
  npm install -g opencode-ai
  opencode serve --port 4096
  ```

### 安装

#### 方法一：APK 安装
```bash
# 构建 APK
./build.sh

# 通过 ADB 安装
adb install app/build/outputs/apk/release/app-release-unsigned.apk

# 或直接侧载 APK
```

#### 方法二：Android Studio
```bash
# 克隆仓库
git clone https://github.com/mulkymalikuldhrs/opencode-android.git
cd opencode-android

# 在 Android Studio 中打开
# 同步 Gradle
# 构建并运行
```

### 首次运行

1. **启动应用**
2. **连接 OpenCode 服务器**
   - 输入服务器地址：`http://<你的IP>:4096`
   - 输入密码（如已配置）
3. **开始编程！**
   - 创建会话
   - 与 AI 对话
   - 执行命令
   - 浏览文件
   - 编辑代码

---

## 📚 API 文档

### 核心接口

#### 全局
```
GET  /global/health
响应: { healthy: true, version: "1.0.193" }

GET  /global/event
返回: SSE 事件流（实时更新）
```

#### 会话
```
GET  /session
列出所有会话

POST  /session
请求体: { title?: string, parentID?: string }
响应: { id, title, createdAt, updatedAt, status, ... }

GET  /session/:id
获取会话详情

POST  /session/:id/message
请求体: { messageID?, model?, agent?, system?, parts: [...] }
响应: { info: Message, parts: [...] }

POST  /session/:id/shell
请求体: { agent?, model?, command: string }
响应: { info: Message, parts: [...] }

DELETE  /session/:id
删除会话及所有数据

POST  /session/:id/fork
请求体: { messageID? }
响应: 新会话
```

#### 文件
```
GET  /file?path=<path>
列出文件和目录

GET  /file/content?path=<path>
读取文件内容

GET  /find?pattern=<pattern>
在文件中搜索文本

GET  /find/file?query=<q>
按名称查找文件
```

#### 模型提供商
```
GET  /provider
列出所有提供商

GET  /provider/auth
获取提供商认证方法

POST  /provider/:id/oauth/authorize
OAuth 授权流程
```

### 身份认证

OpenCode 使用 HTTP 基本认证：

```kotlin
val client = OpenCodeClient(
    serverUrl = "http://192.168.1.100:4096",
    username = "opencode",
    password = "你的密码"
)
```

### 事件类型（SSE）

```typescript
type ServerEvent = 
  | { type: "server.connected", data: {} }
  | { type: "session.status", data: { sessionID: string, status: string } }
  | { type: "message", data: { sessionID: string, message: Message } }
  | { type: "command.output", data: { sessionID: string, output: string } }
  | { type: "error", data: { error: string } }
```

---

## 🔧 配置

### 环境变量
```bash
# 服务器地址
OPENCODE_SERVER_URL=http://192.168.1.100:4096

# 服务器密码（可选）
OPENCODE_SERVER_PASSWORD=你的密码
```

### 客户端配置
```kotlin
// 存储在 SharedPreferences 中
class Config {
    var serverUrl: String = "http://localhost:4096"
    var username: String = "opencode"
    var password: String = ""
    var autoConnect: Boolean = false
}
```

---

## 🤝 致谢

### 👤 作者
**Mulky Malikul Dhaher**

### 📧 联系方式
- **邮箱:** mulkymalikuldhrs@email.com
- **GitHub:** [@mulkymalikuldhrs](https://github.com/mulkymalikuldhrs)
- **社交媒体:** [@mulkymalikuldhr](https://instagram.com/mulkymalikuldhr) (FB/IG)

### 🙏 特别鸣谢

本项目基于以下项目并受其启发：
- [OpenCode](https://opencode.ai) - 开源 AI 编程助手
- [Anomaly](https://anomaly.co) - OpenCode 创作者
- Material Design - Google 设计系统
- OkHttp - Square 出品的 HTTP 客户端
- Kotlin 编程语言 - JetBrains

---

## 🌐 相关链接

- **GitHub 仓库:** https://github.com/mulkymalikuldhrs/opencode-android
- **OpenCode 文档:** https://opencode.ai/docs
- **OpenCode GitHub:** https://github.com/anomalyco/opencode
- **问题追踪:** https://github.com/mulkymalikuldhrs/opencode-android/issues
- **功能请求:** https://github.com/mulkymalikuldhrs/opencode-android/discussions
- **版本发布:** https://github.com/mulkymalikuldhrs/opencode-android/releases

---

## 📄 许可证

本项目基于 MIT 许可证授权 - 详见 [LICENSE](LICENSE) 文件。

---

## 📱 源代码
```bash
git clone https://github.com/mulkymalikuldhrs/opencode-android.git
```

---

<div align="center">

## ⭐ 给本项目点个 Star！

如果您觉得 OpenCode Android 有用，请在 GitHub 上给我们一个 Star！

由 [Mulky Malikul Dhaher](https://github.com/mulkymalikuldhrs) 用 ❤️ 制作

---

[![使用 Kotlin 构建](https://img.shields.io/badge/Kotlin-1.9.0-purple)](https://kotlinlang.org/)
[![Android API](https://img.shields.io/badge/API-24%2B-blue)](https://developer.android.com/)
[![目标 SDK](https://img.shields.io/badge/目标-34-green)](https://developer.android.com/)

</div>
