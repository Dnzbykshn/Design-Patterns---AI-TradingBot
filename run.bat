@echo off
echo ========================================
echo AI Trading Bot - Quick Start
echo ========================================
echo.

echo [1/3] Building project...
if exist "mvnw.cmd" (
    call mvnw.cmd clean compile
) else (
    call mvn clean compile
)
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Checking for ONNX model...
if not exist "ai_model\trading_model.onnx" (
    echo WARNING: ONNX model not found!
    echo Please run: python ai_model\train_model.py
    echo.
    set /p continue="Continue anyway? (y/n): "
    if /i not "%continue%"=="y" (
        exit /b 1
    )
)

echo.
echo [3/3] Running Trading Bot...
echo.
if exist "mvnw.cmd" (
    call mvnw.cmd exec:java -Dexec.mainClass="com.tradingbot.TradingBotMain"
) else (
    mvn exec:java -Dexec.mainClass="com.tradingbot.TradingBotMain"
)

pause

