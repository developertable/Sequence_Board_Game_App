#!/bin/bash

echo "Installing Sequence Board Game..."
echo

# Create installation directory
INSTALL_DIR="$HOME/SequenceGame"
mkdir -p "$INSTALL_DIR"

# Copy files
cp *.java "$INSTALL_DIR/"
cp *.class "$INSTALL_DIR/" 2>/dev/null || true

# Compile Java files
cd "$INSTALL_DIR"
echo "Compiling Java files..."
javac *.java

# Create JAR file
echo "Creating JAR file..."
jar cfe SequenceGame.jar SequenceGameGUI *.class

# Create runner script
cat > "$INSTALL_DIR/run-sequence.sh" << 'EOF'
#!/bin/bash
cd "$(dirname "$0")"
java -jar SequenceGame.jar
EOF

chmod +x "$INSTALL_DIR/run-sequence.sh"

# Create global command (optional)
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    echo "#!/bin/bash" > /usr/local/bin/sequence-game
    echo "cd '$INSTALL_DIR' && java -jar SequenceGame.jar" >> /usr/local/bin/sequence-game
    chmod +x /usr/local/bin/sequence-game
else
    # Linux
    echo "#!/bin/bash" > ~/.local/bin/sequence-game
    echo "cd '$INSTALL_DIR' && java -jar SequenceGame.jar" >> ~/.local/bin/sequence-game
    chmod +x ~/.local/bin/sequence-game
    
    # Add to PATH if not already there
    if [[ ":$PATH:" != *":$HOME/.local/bin:"* ]]; then
        echo 'export PATH="$HOME/.local/bin:$PATH"' >> ~/.bashrc
    fi
fi

# Create desktop entry for Linux
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    DESKTOP_DIR="$HOME/.local/share/applications"
    mkdir -p "$DESKTOP_DIR"
    
    cat > "$DESKTOP_DIR/sequence-game.desktop" << EOF
[Desktop Entry]
Version=1.0
Type=Application
Name=Sequence Board Game
Comment=Play the classic Sequence board game
Exec=$INSTALL_DIR/run-sequence.sh
Icon=applications-games
Terminal=false
Categories=Game;BoardGame;
EOF
fi

echo
echo "================================"
echo "Installation Complete!"
echo "================================"
echo
echo "You can now run the game by:"
echo "1. Running: $INSTALL_DIR/run-sequence.sh"
echo "2. Running: sequence-game (from terminal, after restart)"
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    echo "3. Finding 'Sequence Board Game' in your applications menu"
fi
echo