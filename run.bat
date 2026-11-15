@echo off
setlocal
title FREEALLA - Smart Bus Reservation System

echo ====================================================================
echo        FREEALLA - SMART BUS RESERVATION SYSTEM (OOPS MINI PROJECT)
echo ====================================================================
echo.

:: Detect or set JAVA_HOME if not present
if not defined JAVA_HOME (
    if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot" (
        set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
    )
)

if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    echo [INFO] Using Java from: %JAVA_HOME%
)

where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java is not detected on your system. Please install JDK 17+.
    pause
    exit /b 1
)

echo [INFO] Java Version:
java -version
echo.

where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    echo [INFO] Detected system Maven. Starting Spring Boot application...
    mvn spring-boot:run
) else (
    echo [INFO] System Maven not found in PATH.
    echo [INFO] You can run: 'mvn spring-boot:run' after installing Maven,
    echo        or open this folder in IntelliJ IDEA / VS Code / Eclipse as a Maven project.
    echo.
    pause
)
