#!/bin/bash

echo "Starting Sequence Board Game..."
cd "$(dirname "$0")"

if [ ! -f "SequenceGame.jar" ]; then
    echo "Error: SequenceGame.jar not found!"
    echo "Please run install.sh first."
    exit 1
fi

java -jar SequenceGame.jar

if [ $? -ne 0 ]; then
    echo
    echo "Error: Could not start the game."
    echo "Make sure Java is installed and try again."
    read -p "Press Enter to continue..."
fi