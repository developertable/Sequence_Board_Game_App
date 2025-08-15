#!/bin/bash
echo "Creating portable JAR file..."
javac *.java
jar cfe SequenceGame.jar SequenceGameGUI *.class
echo "JAR file created: SequenceGame.jar"
echo "You can now run: java -jar SequenceGame.jar"