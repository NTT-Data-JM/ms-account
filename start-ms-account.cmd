@echo off
setlocal EnableDelayedExpansion

set "JAVA_HOME=c:\Programas\jdk-17"
set "MAVEN_HOME=c:\Programas\maven"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

set "PROJECT_DIR=%~dp0"
set "PID_FILE=%~dp0ms-account.pid"
set "RUNNER=%~dp0run-ms-account.cmd"

if not exist "%JAVA_HOME%\bin\java.exe" (
  echo ERROR: Java not found at "%JAVA_HOME%"
  exit /b 1
)
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  echo ERROR: Maven not found at "%MAVEN_HOME%"
  exit /b 1
)
if not exist "%PROJECT_DIR%pom.xml" (
  echo ERROR: Project not found at "%PROJECT_DIR%"
  exit /b 1
)

for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":9092" ^| findstr "LISTENING"') do (
  echo Stopping previous process on port 9092 ^(PID %%P^)...
  taskkill /PID %%P /T /F >nul 2>&1
)

echo Starting ms-account...
echo Project: %PROJECT_DIR%
echo URL:     http://localhost:9092
echo.

start "ms-account" "%RUNNER%"

echo Waiting for port 9092...
set /a _tries=0
:wait_port
timeout /t 2 /nobreak >nul
set /a _tries+=1
for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":9092" ^| findstr "LISTENING"') do (
  >"%PID_FILE%" echo %%P
  echo ms-account is UP ^(PID %%P^). PID saved to ms-account.pid
  echo Use stop-ms-account.cmd to stop it.
  exit /b 0
)
if !_tries! LSS 60 goto wait_port

echo ERROR: Timed out waiting for port 9092.
echo Check the "ms-account" window for Maven / Spring Boot errors.
exit /b 1
