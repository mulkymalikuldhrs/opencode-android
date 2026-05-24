# 🤖 Opencode Android Client

<div align="center">

[![Opencode Android Logo](https://img.shields.io/badge/Opencode-Android-v1.0.0-purple)](https://github.com/mulkymalikuldhrs/opencode-android/releases)
[![Lisensi: MIT](https://img.shields.io/badge/Lisensi-MIT-green.svg)](LICENSE)
[![Platform: Android](https://img.shields.io/badge/Platform-Android-blue.svg)]()
[![GitHub Stars](https://img.shields.io/github/stars/mulkymalikuldhrs/opencode-android?style=social)](https://github.com/mulkymalikuldhrs/opencode-android/stargazers)

**⚠️ Klien Android OpenCode Independen - Build Komunitas**

</div>

---

## ✨ Fitur

### 🚀 Kemampuan Inti
- **Integrasi API OpenCode Lengkap** - Terhubung ke server OpenCode (50+ endpoint)
- **Chat Real-Time** - Streaming SSE (Server-Sent Events) untuk respons AI instan
- **Manajemen Sesi** - Buat, fork, dan berpindah antar sesi
- **Akses Terminal** - Eksekusi perintah shell di server
- **Browser File** - Baca, cari, dan kelola file proyek
- **Editor Kode** - Pengeditan kode lengkap dengan penyorotan sintaks
- **Dukungan Multi-Provider** - OpenAI, Anthropic Claude, 75+ penyedia LLM

### 📱 Native Android
- **Material Design 3** - Tema gelap yang indah dengan aksen gradien
- **Navigasi Bawah** - Tata letak 4 tab yang intuitif
- **Coroutines** - Operasi asinkron dan non-blocking
- **Koneksi Ulang Otomatis** - Menjaga stabilitas koneksi
- **Layanan Latar Belakang** - Menjaga sesi tetap aktif

### 🎯 Alat Pengembang
- **Respons AI Nyata** - Didukung oleh server OpenCode
- **Pelacakan Diff File** - Lihat perubahan kode antar sesi
- **Eksekusi Perintah** - Jalankan perintah melalui API shell OpenCode
- **Integrasi Git** - Operasi VCS melalui OpenCode
- **Dukungan LSP** - Language Server Protocol untuk kecerdasan kode
- **Protokol MCP** - Model Context Protocol untuk integrasi lanjutan

---

## 📸 Tangkapan Layar

| Antarmuka Chat | Terminal | Manajer File | Editor Kode |
|:-----------:|:----------:|:------------:|:------------:|
| ![Chat](docs/screenshots/chat.png) | ![Terminal](docs/screenshots/terminal.png) | ![Files](docs/screenshots/files.png) | ![Editor](docs/screenshots/editor.png) |

*Implementasi UI lengkap dengan konektivitas backend OpenCode nyata*

---

## 🚀 Memulai

### Prasyarat

**Perangkat Android:**
- Android 7.0 (API 24) atau lebih tinggi
- RAM 2GB+ direkomendasikan
- Ruang penyimpanan 500MB+

**Backend (Diperlukan):**
- **Opsi 1: Termux** (Direkomendasikan untuk Android)
  ```bash
  pkg update -y
  pkg install nodejs-lts -y
  npm i -g opencode-ai
  opencode serve --port 4096
  ```

- **Opsi 2: PC/Mac/Linux**
  ```bash
  npm install -g opencode-ai
  opencode serve --port 4096
  ```

### Instalasi

#### Metode 1: Instal APK
```bash
# Build APK
./build.sh

# Instal melalui ADB
adb install app/build/outputs/apk/release/app-release-unsigned.apk

# Atau sideload APK secara langsung
```

#### Metode 2: Android Studio
```bash
# Clone repositori
git clone https://github.com/mulkymalikuldhrs/opencode-android.git
cd opencode-android

# Buka di Android Studio
# Sync Gradle
# Build & Jalankan
```

### Pertama Kali Menjalankan

1. **Luncurkan Aplikasi**
2. **Hubungkan ke Server OpenCode**
   - Masukkan URL server: `http://<ip-anda>:4096`
   - Masukkan kata sandi (jika dikonfigurasi)
3. **Mulai Coding!**
   - Buat sesi
   - Chat dengan AI
   - Eksekusi perintah
   - Jelajahi file
   - Edit kode

---

## 📚 Dokumentasi API

### Endpoint Inti

#### Global
```
GET  /global/health
Respons: { healthy: true, version: "1.0.193" }

GET  /global/event
Mengembalikan: Aliran event SSE (pembaruan real-time)
```

#### Sesi
```
GET  /session
Daftar semua sesi

POST  /session
Body: { title?: string, parentID?: string }
Respons: { id, title, createdAt, updatedAt, status, ... }

GET  /session/:id
Detail sesi

POST  /session/:id/message
Body: { messageID?, model?, agent?, system?, parts: [...] }
Respons: { info: Message, parts: [...] }

POST  /session/:id/shell
Body: { agent?, model?, command: string }
Respons: { info: Message, parts: [...] }

DELETE  /session/:id
Hapus sesi dan semua data

POST  /session/:id/fork
Body: { messageID? }
Respons: Sesi baru
```

#### File
```
GET  /file?path=<path>
Daftar file dan direktori

GET  /file/content?path=<path>
Baca konten file

GET  /find?pattern=<pattern>
Cari teks dalam file

GET  /find/file?query=<q>
Cari file berdasarkan nama
```

#### Provider
```
GET  /provider
Daftar semua provider

GET  /provider/auth
Metode autentikasi provider

POST  /provider/:id/oauth/authorize
Alur otorisasi OAuth
```

### Autentikasi

OpenCode menggunakan Autentikasi Dasar HTTP:

```kotlin
val client = OpenCodeClient(
    serverUrl = "http://192.168.1.100:4096",
    username = "opencode",
    password = "kata-sandi-anda"
)
```

### Tipe Event (SSE)

```typescript
type ServerEvent = 
  | { type: "server.connected", data: {} }
  | { type: "session.status", data: { sessionID: string, status: string } }
  | { type: "message", data: { sessionID: string, message: Message } }
  | { type: "command.output", data: { sessionID: string, output: string } }
  | { type: "error", data: { error: string } }
```

---

## 🔧 Konfigurasi

### Variabel Lingkungan
```bash
# URL Server
OPENCODE_SERVER_URL=http://192.168.1.100:4096

# Kata Sandi Server (opsional)
OPENCODE_SERVER_PASSWORD=kata-sandi-anda
```

### Konfigurasi Klien
```kotlin
// Disimpan di SharedPreferences
class Config {
    var serverUrl: String = "http://localhost:4096"
    var username: String = "opencode"
    var password: String = ""
    var autoConnect: Boolean = false
}
```

---

## 🤝 Kredit

### 👤 Penulis
**Mulky Malikul Dhaher**

### 📧 Kontak
- **Email:** mulkymalikuldhrs@email.com
- **GitHub:** [@mulkymalikuldhrs](https://github.com/mulkymalikuldhrs)
- **Sosial:** [@mulkymalikuldhr](https://instagram.com/mulkymalikuldhr) (FB/IG)

### 🙏 Ucapan Terima Kasih

Proyek ini didasarkan dan terinspirasi oleh:
- [OpenCode](https://opencode.ai) - Agen coding AI sumber terbuka
- [Anomaly](https://anomaly.co) - Pencipta OpenCode
- Material Design - Sistem desain Google
- OkHttp - Klien HTTP dari Square
- Bahasa Pemrograman Kotlin - JetBrains

---

## 🌐 Tautan

- **Repositori GitHub:** https://github.com/mulkymalikuldhrs/opencode-android
- **Dokumentasi OpenCode:** https://opencode.ai/docs
- **OpenCode GitHub:** https://github.com/anomalyco/opencode
- **Pelacak Bug:** https://github.com/mulkymalikuldhrs/opencode-android/issues
- **Permintaan Fitur:** https://github.com/mulkymalikuldhrs/opencode-android/discussions
- **Rilis:** https://github.com/mulkymalikuldhrs/opencode-android/releases

---

## 📄 Lisensi

Proyek ini dilisensikan di bawah Lisensi MIT - lihat file [LICENSE](LICENSE) untuk detailnya.

---

## 📱 Kode Sumber
```bash
git clone https://github.com/mulkymalikuldhrs/opencode-android.git
```

---

<div align="center">

## ⭐ Berikan Bintang pada Proyek Ini!

Jika Anda merasa OpenCode Android bermanfaat, silakan berikan bintang di GitHub!

Dibuat dengan ❤️ oleh [Mulky Malikul Dhaher](https://github.com/mulkymalikuldhrs)

---

[![Dibuat dengan Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple)](https://kotlinlang.org/)
[![Android API](https://img.shields.io/badge/API-24%2B-blue)](https://developer.android.com/)
[![Target SDK](https://img.shields.io/badge/Target-34-green)](https://developer.android.com/)

</div>
