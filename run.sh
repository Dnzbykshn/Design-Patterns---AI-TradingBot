#!/bin/bash

echo "========================================"
echo "AI Trading Bot - Quick Start"
echo "========================================"
echo ""

echo "[1/3] Building project..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo ""
echo "[2/3] Checking for ONNX model..."
if [ ! -f "ai_model/trading_model.onnx" ]; then
    echo "WARNING: ONNX model not found!"
    echo "Please run: python ai_model/train_model.py"
    echo ""
    read -p "Continue anyway? (y/n): " continue
    if [ "$continue" != "y" ]; then
        exit 1
    fi
fi

echo ""
echo "[3/3] Running Trading Bot..."
echo ""
mvn exec:java -Dexec.mainClass="com.tradingbot.TradingBotMain"

