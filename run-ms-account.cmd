@echo off
setlocal

set "JAVA_HOME=c:\Programas\jdk-17"
set "MAVEN_HOME=c:\Programas\maven"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

cd /d "%~dp0"

echo JAVA_HOME=%JAVA_HOME%
java -version
echo.
echo Running: mvn spring-boot:run
echo.

mvn spring-boot:run
set "EXITCODE=%ERRORLEVEL%"

echo.
if not "%EXITCODE%"=="0" (
  echo ms-account exited with code %EXITCODE%.
  pause
)
exit /b %EXITCODE%
