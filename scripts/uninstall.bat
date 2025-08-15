@echo off
echo Uninstalling Sequence Board Game...

REM Remove installation directory
if exist "C:\SequenceGame" rmdir /S /Q "C:\SequenceGame"

REM Remove desktop shortcut
if exist "%USERPROFILE%\Desktop\Sequence Game.bat" del "%USERPROFILE%\Desktop\Sequence Game.bat"

echo Uninstallation complete!
pause