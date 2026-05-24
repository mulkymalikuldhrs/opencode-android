# OPENCODE SERVER DI TERMUX - GUIDE LENGKAP

## 🎯 Kenapa Termux?

✅ **Keuntungan:**
- Termux = Linux environment di Android
- Bisa install Node.js + OpenCode CLI
- Android jadi server + client sekaligus!
- Tidak perlu PC/server terpisah

## 📋 Requirements

- Android 7.0+ (API 24+)
- Termux (F-Droid version - lebih update)
- Storage: 500MB+ free
- RAM: 2GB+ recommended

## 🚀 Setup Step-by-Step

### **1. Install Termux**

Download dari F-Droid (BUKAN Play Store - versi Play Store sudah outdated):
```
https://f-droid.org/packages/com.termux/
```

### **2. Setup OpenCode di Termux**

Jalankan script setup:
```bash
# Copy script ke Termux (via file sharing atau download)
cd ~
# Jalankan:
bash setup-termux.sh
```

Atau manual:
```bash
# Update packages
pkg update -y
pkg upgrade -y

# Install Node.js
pkg install nodejs-lts -y

# Install OpenCode
npm i -g opencode-ai

# Cek versi
opencode --version
```

### **3. Jalankan OpenCode Server**

```bash
# Start server
opencode serve --port 4096 --hostname 0.0.0.0

# Dengan password (lebih aman)
OPENCODE_SERVER_PASSWORD=secret123 opencode serve --port 4096 --hostname 0.0.0.0
```

### **4. Cek IP Address**

```bash
# Cek IP
ifconfig
# atau
ip addr

# Cari interface wlan0 atau eth0
# Contoh: 192.168.1.100
```

### **5. Connect dari OpenCode Android App**

1. Buka OpenCode Android app
2. Masukkan URL: `http://<IP_TERMUX>:4096`
3. Masukkan password (jika ada)
4. Tap Connect
5. ✅ **Connected!**

## ⚠️ Keterbatasan Termux

### **1. Background Execution**
Termux process akan mati kalau:
- Screen off + battery optimization aktif
- Memory pressure (RAM penuh)
- Termux app di-swipe away dari recent apps

**Solusi:**
```bash
# Install termux-services
pkg install termux-services -y

# Atau pakai tmux untuk keep session alive
pkg install tmux -y
tmux new -s opencode
# Jalankan opencode serve di dalam tmux
# Detach: Ctrl+B, D
# Attach lagi: tmux attach -t opencode
```

### **2. Network**
- Harus pakai WiFi (tidak bisa pakai mobile data untuk host)
- Port 4096 harus terbuka (tidak diblokir firewall)

### **3. Storage**
- Project files tersimpan di `/data/data/com.termux/files/home/`
- Tidak bisa akses external SD card langsung
- Bisa pakai `termux-setup-storage` untuk akses shared storage

### **4. Performance**
- Node.js di Termux lebih lambat dari PC
- Large context mungkin slow
- Recommendation: pakai model yang lebih kecil/cepat

## 🔧 Troubleshooting

### **"Cannot install opencode"**
```bash
# Clear npm cache
npm cache clean --force

# Install dengan verbose
npm install -g opencode --verbose

# Atau pakai yarn
pkg install yarn -y
yarn global add opencode
```

### **"Permission denied"**
```bash
# Fix permissions
termux-fix-permissions
# atau
chmod +x $PREFIX/bin/opencode
```

### **"Port already in use"**
```bash
# Kill process di port 4096
kill $(lsof -t -i:4096)

# Atau ganti port
opencode serve --port 8080 --hostname 0.0.0.0
```

### **"Connection refused dari Android app"**
1. Cek IP address benar: `ifconfig`
2. Pastikan port benar: 4096
3. Cek firewall: `iptables -L`
4. Pastikan Termux dan Android app di network yang sama (WiFi)
5. Coba ping: `ping <IP_TERMUX>`

### **"Out of memory"**
```bash
# Limit Node.js memory
node --max-old-space-size=512 $(which opencode) serve --port 4096
```

## 💡 Tips & Tricks

### **Auto-start saat boot**
```bash
# Install termux-boot (dari F-Droid)
# Buat script di ~/.termux/boot/
mkdir -p ~/.termux/boot
cat > ~/.termux/boot/start-opencode << 'EOF'
#!/data/data/com.termux/files/usr/bin/sh
termux-wake-lock
cd $HOME
export OPENCODE_SERVER_PASSWORD=yourpass
opencode serve --port 4096 --hostname 0.0.0.0 &
EOF
chmod +x ~/.termux/boot/start-opencode
```

### **Keep screen on saat coding**
```bash
termux-wake-lock
# Keep screen on
# Unlock: termux-wake-unlock
```

### **Share files antara Android dan Termux**
```bash
# Setup storage access
termux-setup-storage

# Sekarang bisa akses:
# ~/storage/shared/ = Internal storage
# ~/storage/downloads/ = Downloads
# ~/storage/dcim/ = Photos
```

### **Backup project**
```bash
# Backup ke external storage
tar -czvf ~/storage/shared/opencode-backup.tar.gz ~/projects/
```

## 🌐 Access dari luar network (opsional)

Kalau mau akses dari luar WiFi (internet):

**Opsi 1: Ngrok**
```bash
pkg install ngrok
ngrok http 4096
# Dapatkan public URL
```

**Opsi 2: Cloudflare Tunnel**
```bash
# Install cloudflared
pkg install cloudflared
cloudflared tunnel --url http://localhost:4096
```

⚠️ **Peringatan:** Jangan expose ke internet tanpa password kuat!

## 🎯 Performance Optimization

### **1. Gunakan model yang lebih kecil**
```bash
# Di OpenCode, pilih model yang lebih cepat
# Contoh: gpt-3.5-turbo instead of gpt-4
```

### **2. Limit concurrent sessions**
```bash
# Jangan buka terlalu banyak session
# Close session yang tidak digunakan
```

### **3. Clear cache periodically**
```bash
# Clear npm cache
npm cache clean --force

# Clear OpenCode cache (if any)
rm -rf ~/.cache/opencode
```

## 📱 Contoh Workflow

1. **Buka Termux**
2. **Jalankan:** `tmux new -s opencode`
3. **Start server:** `opencode serve --port 4096 --hostname 0.0.0.0`
4. **Detach tmux:** Ctrl+B, lalu D
5. **Buka OpenCode Android app**
6. **Connect ke:** `http://localhost:4096` (kalau di device yang sama)
   
   Atau `http://<IP_WIFI>:4096` (kalau dari device lain di network yang sama)
7. **Coding!** 💻

## 🎉 Kesimpulan

**Termux + OpenCode = Perfect combo untuk Android!**

✅ Tidak perlu PC/server terpisah
✅ Bisa coding di mana saja
✅ Portable development environment
✅ Real OpenCode experience di Android

⚠️ Hanya perlu diperhatikan:
- Background execution (pakai tmux/termux-services)
- Battery consumption
- Storage management

**Siap untuk coding di Android! 🚀**

---

> **Contact:** Mulky Malikul Dhaher — [mulkymalikuldhaher@email.com](mailto:mulkymalikuldhaher@email.com)
>
> **Disclaimer:** This project is for Education Purpose only. Risiko apapun tidak kita tanggung. (We are not responsible for any risks or damages.)
