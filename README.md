# Sequence Board Game App 🎯

A fully functional Java implementation of the classic Sequence board game featuring both human vs human and human vs AI gameplay modes.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=for-the-badge)

## 🎮 Features

- **Complete GUI Interface** with intuitive click-to-play mechanics
- **Three AI Difficulty Levels**:
  - 🤖 **Suppandi** (Easy) - Basic strategy with random moves
  - 🧠 **Chacha Chaudhary** (Medium) - Smart tactical player  
  - 👑 **Chanakya** (Hard) - Master strategist with advanced algorithms
- **Turn Timer** - 15 seconds per turn to keep games moving
- **Visual Sequence Highlighting** - Completed sequences are clearly marked
- **Dead Card Detection** - Automatic detection and handling of unplayable cards
- **Console Version** - Alternative text-based gameplay
- **Full Sequence Rules** - Authentic gameplay experience

## 🚀 Quick Start

### Prerequisites
- Java 8 or higher installed on your system
- Command line access

### Option 1: Automated Installation (Recommended)

**Windows:**
```batch
# Download and run the installer
scripts/install.bat

🚀 Installation Instructions
Choose one of the following installation methods:
Method 1: Automated Installation (Recommended)
Windows Users:

Download or clone this repository
Navigate to the project folder
Run the installer:
batchscripts\install.bat

Follow the on-screen instructions
Run the game:

Double-click "Sequence Game.bat" on your desktop, OR
Run java -jar C:\SequenceGame\SequenceGame.jar from anywhere



Mac/Linux Users:

Download or clone this repository
Open Terminal and navigate to the project folder
Make the script executable and run:
bashchmod +x scripts/install.sh
./scripts/install.sh

Follow the on-screen instructions
Run the game:

Run sequence-game from terminal (after restart), OR
Run ~/SequenceGame/run-sequence.sh, OR
Find "Sequence Board Game" in your applications menu (Linux)



Method 2: Manual Installation

Clone or download this repository:
git clone https://github.com/developertable/Sequence_Board_Game_App.git
cd Sequence_Board_Game_App

Compile the Java files:
cd src
javac *.java

Run the game:
# GUI Version (Recommended)
java SequenceGameGUI

# Console Version
java GameController


Method 3: Portable JAR File

Create a portable JAR file:
bash# Windows
scripts\create-jar.bat

**Mac/Linux:**
```bash
chmod +x scripts/install.sh
./scripts/install.sh

Run the JAR file:
bashjava -jar SequenceGame.jar

Move the JAR file anywhere and run it:

The JAR file is completely portable
Can be run on any computer with Java installed
No additional installation required



🎮 How to Start Playing
GUI Version (Recommended):

Run the game using any of the methods above
Choose game mode:

Human vs Human
Human vs Suppandi (Easy AI)
Human vs Chacha Chaudhary (Medium AI)
Human vs Chanakya (Hard AI)


Enter player names when prompted
Start playing!

Console Version:

Run: java GameController from the src directory
Follow text-based prompts
Use coordinates to make moves

🎯 How to Play
Objective
Be the first player to create TWO sequences of five chips in a row (horizontal, vertical, or diagonal).
Game Setup

Each player starts with 7 cards
Players take turns playing cards and placing chips
Corners are "FREE" spaces that count toward sequences

Card Types

Regular Cards: Must match the board position exactly
Two-Eyed Jacks (Hearts/Diamonds): Wild cards - place anywhere
One-Eyed Jacks (Spades/Clubs): Remove opponent's chip (except sequence chips)

Special Rules

Completed sequence chips are protected and cannot be removed
Dead cards (both board positions blocked) can be discarded
15-second turn timer keeps games moving
First player to complete 2 sequences wins!


⚙️ Installation Verification
To verify your installation works correctly:

Test compilation:
bashcd src
javac *.java

Test game launch:
bashjava SequenceGameGUI

Expected result: Game window opens with menu options

🔧 Troubleshooting
Common Issues:
"Java is not recognized" (Windows):

Install Java from Oracle's website
Add Java to your system PATH
Restart Command Prompt after installation

"Permission denied" (Mac/Linux):

Make scripts executable: chmod +x scripts/*.sh
Run with: ./script-name.sh
Check file permissions in the directory

Game doesn't start:

Verify Java version: java -version (must be 8+)
Ensure all .java files are in the src/ directory
Try manual compilation first
Check for error messages in terminal

Installation script fails:

Try Manual Installation (Method 2)
Check if you have write permissions to the installation directory
Run terminal/command prompt as administrator (if needed)

Graphics issues:

Update your graphics drivers
Try running with: java -Djava.awt.headless=false SequenceGameGUI

🗑️ Uninstallation
To remove the game:
Windows:
batchscripts\uninstall.bat
Mac/Linux:
bash./scripts/uninstall.sh
Or manually delete:

Installation directory: C:\SequenceGame (Windows) or ~/SequenceGame (Mac/Linux)
Desktop shortcuts
Application menu entries (Linux)

📱 System Requirements

Operating System: Windows 7+, macOS 10.10+, Linux (any modern distribution)
Java: Version 8 or higher
RAM: 256 MB minimum (512 MB recommended)
Storage: 50 MB free space
Display: 1024x768 minimum resolution
Input: Mouse and keyboard


🎯 Quick Start Commands
bash# Clone repository
git clone https://github.com/developertable/Sequence_Board_Game_App.git

# Navigate to project
cd Sequence_Board_Game_App

# Install (Windows)
scripts\install.bat

# Install (Mac/Linux)
chmod +x scripts/install.sh && ./scripts/install.sh

# Manual run
cd src && javac *.java && java SequenceGameGUI

# Create portable JAR
scripts\create-jar.bat  # Windows
./scripts/create-jar.sh # Mac/Linux

📞 Contact & Support
Developer: Rahul Kurra
Email: rahul.kurra1986@gmail.com
GitHub: @developertable
Repository: Sequence_Board_Game_App
For bug reports, feature requests, or questions about the implementation, please open an issue on GitHub.

🎯 Enjoy playing Sequence! May the best strategist win! 🎮

Last updated: August 2025