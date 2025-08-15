@echo off
title Sequence Board Game
echo Starting Sequence Board Game...
java -jar "%~dp0SequenceGame.jar"
if errorlevel 1 (
    echo.
    echo Error: Could not start the game.
    echo Make sure Java is installed and try again.
    pause
)