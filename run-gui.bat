@echo off
echo ========================================
echo AI Trading Bot - GUI Mode
echo ========================================
echo.

echo Building project...
call mvn clean compile
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Starting GUI...
echo.
mvn exec:java -Dexec.mainClass="com.tradingbot.gui.TradingBotGUI"

pause


