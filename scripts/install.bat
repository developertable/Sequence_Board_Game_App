@echo off
echo Installing Sequence Board Game...
echo.

REM Create installation directory
if not exist "C:\SequenceGame" mkdir "C:\SequenceGame"

REM Copy Java files to installation directory
copy "*.java" "C:\SequenceGame\"
copy "*.class" "C:\SequenceGame\" 2>nul

REM Compile Java files if .class files don't exist
cd "C:\SequenceGame"
echo Compiling Java files...
javac *.java

REM Create JAR file
echo Creating JAR file...
jar cfe SequenceGame.jar SequenceGameGUI *.class

REM Create desktop shortcut batch file
echo @echo off > "C:\SequenceGame\run-sequence.bat"
echo cd "C:\SequenceGame" >> "C:\SequenceGame\run-sequence.bat"
echo java -jar SequenceGame.jar >> "C:\SequenceGame\run-sequence.bat"

REM Copy shortcut to desktop
copy "C:\SequenceGame\run-sequence.bat" "%USERPROFILE%\Desktop\Sequence Game.bat"

REM Add to PATH (optional)
setx PATH "%PATH%;C:\SequenceGame"

echo.
echo ================================
echo Installation Complete!
echo ================================
echo.
echo You can now run the game by:
echo 1. Double-clicking "Sequence Game.bat" on your desktop
echo 2. Running "java -jar C:\SequenceGame\SequenceGame.jar" from anywhere
echo 3. Running "run-sequence" from command prompt (after restart)
echo.
pause