# 🎯 Sequence Board Game App

A **fully functional Java implementation** of the classic Sequence board game featuring **human vs human** and **human vs AI** gameplay modes.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=for-the-badge)

---

## 🎮 Features

- 🖥 **Complete GUI Interface** — intuitive click-to-play mechanics  
- 🤖 **Three AI Difficulty Levels**:  
  - **Suppandi** *(Easy)* — Basic strategy with random moves  
  - **Chacha Chaudhary** *(Medium)* — Smart tactical play  
  - **Chanakya** *(Hard)* — Master strategist with advanced algorithms  
- ⏱ **Turn Timer** — 15 seconds per move to keep games flowing  
- ✨ **Visual Sequence Highlighting** — Completed sequences clearly marked  
- 🚫 **Dead Card Detection** — Auto-detects & handles unplayable cards  
- 💻 **Console Version** — Text-based gameplay option  
- 📜 **Full Sequence Rules** — Authentic gameplay experience  

---

## 🚀 Quick Start

### 📋 Prerequisites
- Java 8 or higher installed  
- Command line access  

---

### ⚡ Option 1: Automated Installation (Recommended)

#### **Windows**
```bash
# Download or clone this repository
# Navigate to the project folder
scripts\install.bat
```
Run the game:  
- Double-click **"Sequence Game.bat"** on your desktop  
- OR:  
```bash
java -jar C:\SequenceGame\SequenceGame.jar
```

#### **Mac / Linux**
```bash
chmod +x scripts/install.sh
./scripts/install.sh
```
Run the game:  
- `sequence-game` from terminal (after restart)  
- OR: `~/SequenceGame/run-sequence.sh`  
- OR: Find "Sequence Board Game" in applications menu  

---

### 🛠 Option 2: Manual Installation
```bash
git clone https://github.com/developertable/Sequence_Board_Game_App.git
cd Sequence_Board_Game_App
cd src
javac *.java
java SequenceGameGUI   # GUI version
java GameController    # Console version
```

---

### 📦 Option 3: Portable JAR
```bash
# Windows
scripts\create-jar.bat

# Mac/Linux
chmod +x scripts/create-jar.sh
./scripts/create-jar.sh
```
Run anywhere:  
```bash
java -jar SequenceGame.jar
```

---

## 🎮 How to Play

### 🎯 Objective
Be the first to create **two sequences** of five chips in a row (horizontal, vertical, or diagonal).

### 🃏 Card Types
- **Regular Cards** — Match the board position exactly  
- **Two-Eyed Jacks (♥♦)** — Wild cards (place anywhere)  
- **One-Eyed Jacks (♠♣)** — Remove opponent’s chip (except protected ones)  

### 📝 Special Rules
- Completed sequences are protected  
- Dead cards can be discarded  
- 15-second turn timer  

---

## ⚙️ Troubleshooting

| Issue | Solution |
|-------|----------|
| `"Java is not recognized"` (Windows) | Install Java, add to PATH |
| `"Permission denied"` (Mac/Linux) | `chmod +x scripts/*.sh` |
| Game doesn’t start | Check Java version, compile manually |
| Graphics issues | Update drivers, try `-Djava.awt.headless=false` |

---

## 🗑️ Uninstallation

**Windows:**
```bash
scripts\uninstall.bat
```

**Mac/Linux:**
```bash
./scripts/uninstall.sh
```
Or delete:  
- `C:\SequenceGame` (Windows)  
- `~/SequenceGame` (Mac/Linux)  

---

## 📱 System Requirements
- OS: Windows 7+, macOS 10.10+, Linux  
- Java: 8+  
- RAM: 256 MB (512 MB recommended)  
- Storage: 50 MB free  
- Display: 1024×768 min  

---

## 📞 Contact & Support
**Developer:** Rahul Kurra  
📧 **Email:** rahul.kurra1986@gmail.com  
💻 **GitHub:** [@developertable](https://github.com/developertable)

For bugs, features, or questions — please open an issue on GitHub.

---

**🎯 Enjoy playing Sequence! May the best strategist win! 🎮**
