@echo off

cd /d "%~dp0"

if not exist out mkdir out

dir /s /b src\*.java > sources.txt

javac -d out @sources.txt
set "result=%errorlevel%"

del sources.txt

if not %result%==0 goto :end

java -cp out FitTrack

:end
pause