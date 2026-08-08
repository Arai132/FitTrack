@echo off

cd /d "%~dp0"
set "PROJECT_ROOT=%cd%"

if not exist out mkdir out

dir /s /b src\*.java test\*.java > sources.txt

javac -d out @sources.txt
set "result=%errorlevel%"

del sources.txt

if not %result%==0 goto :end

rem Run from a scratch directory so FitTrackFacadeTest's file persistence
rem never touches a real fittrack_data.ser in the project folder.
set "RUN_DIR=%TEMP%\fittrack-test-run"
if exist "%RUN_DIR%" rmdir /s /q "%RUN_DIR%"
mkdir "%RUN_DIR%"
cd /d "%RUN_DIR%"

java -cp "%PROJECT_ROOT%\out" AllTests

cd /d "%PROJECT_ROOT%"
rmdir /s /q "%RUN_DIR%"

:end
pause
