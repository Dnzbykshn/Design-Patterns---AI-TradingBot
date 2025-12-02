package com.tradingbot.strategy;

import com.tradingbot.domain.MarketCandle;
import com.tradingbot.domain.Signal;

/**
 * Represents the 'Strategy' interface in the Strategy Pattern.
 * 
 * Pattern Name: Strategy Pattern
 * Role: Strategy - defines the common interface for all trading strategies
 * Reason: Used to allow runtime switching between different trading algorithms (AI-based, RSI-based, etc.)
 * without modifying the client code. This enables flexibility and extensibility in trading logic.
 */
public interface TradingStrategy {
    /**
     * Analyzes a market candle and generates a trading signal.
     * @param candle The market candle to analyze
     * @return A trading signal (BUY, SELL, or HOLD)
     */
    Signal analyze(MarketCandle candle);
}

