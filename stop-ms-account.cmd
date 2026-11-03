@echo off
setlocal EnableDelayedExpansion

set "PID_FILE=%~dp0ms-account.pid"
set "STOPPED=0"

echo Stopping ms-account...

if exist "%PID_FILE%" (
  set /p APP_PID=<"%PID_FILE%"
  if defined APP_PID (
    echo Found PID file: !APP_PID!
    taskkill /PID !APP_PID! /T /F >nul 2>&1 && set STOPPED=1
  )
  del "%PID_FILE%" >nul 2>&1
)

for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":9092" ^| findstr "LISTENING"') do (
  echo Killing process on port 9092 ^(PID %%P^)...
  taskkill /PID %%P /T /F >nul 2>&1 && set STOPPED=1
)

taskkill /FI "WINDOWTITLE eq ms-account*" /T /F >nul 2>&1 && set STOPPED=1

if "!STOPPED!"=="1" (
  echo ms-account stopped.
) else (
  echo No running ms-account instance found on port 9092.
)

exit /b 0
