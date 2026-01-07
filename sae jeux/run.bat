@echo off
setlocal enabledelayedexpansion

set "ROOT_DIR=%~dp0"
set "SRC_DIR=%ROOT_DIR%src"
set "CLASSES_DIR=%ROOT_DIR%classes"

if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"

echo [IJAVA] Compilation en cours...
javac -d "%CLASSES_DIR%" -cp "%ROOT_DIR%lib\ijava.jar" -sourcepath "%SRC_DIR%" "%SRC_DIR%\*.java"
if errorlevel 1 goto :error

echo [IJAVA] Lancement de Main...
cd "%CLASSES_DIR%"
java -cp "%ROOT_DIR%lib\ijava.jar;." ijava2.clitools.MainCLI execute Main
if errorlevel 1 goto :error

endlocal
goto :eof

:error
endlocal
exit /b 1
