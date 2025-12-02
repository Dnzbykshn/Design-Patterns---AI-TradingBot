package com.tradingbot.strategy;

import com.tradingbot.domain.MarketCandle;
import com.tradingbot.domain.Signal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the 'Concrete Strategy' in the Strategy Pattern.
 * 
 * Pattern Name: Strategy Pattern & Template Method Pattern
 * Role: Concrete Strategy - implements the TradingStrategy interface and extends BaseTradingStrategy.
 * This class provides a math-based trading algorithm using the Relative Strength Index (RSI) indicator.
 * Reason: Used Strategy Pattern to allow runtime switching between AI and math-based logic.
 * Extends BaseTradingStrategy to leverage the Template Method Pattern for consistent trade execution flow.
 */
public class RSIStrategy extends BaseTradingStrategy {
    private static final int RSI_PERIOD = 14;
    private static final double RSI_OVERSOLD = 30.0; // Buy signal when RSI < 30
    private static final double RSI_OVERBOUGHT = 70.0; // Sell signal when RSI > 70
    
    private final List<Double> priceHistory;

    public RSIStrategy(com.tradingbot.domain.Wallet wallet) {
        super(wallet);
        this.priceHistory = new ArrayList<>();
    }

    /**
     * Calculates the Relative Strength Index (RSI) indicator.
     * @param prices List of closing prices
     * @param period RSI period (typically 14)
     * @return RSI value (0-100)
     */
    private double calculateRSI(List<Double> prices, int period) {
        if (prices.size() < period + 1) {
            return 50.0; // Neutral RSI if not enough data
        }

        List<Double> gains = new ArrayList<>();
        List<Double> losses = new ArrayList<>();

        for (int i = 1; i < prices.size(); i++) {
            double change = prices.get(i) - prices.get(i - 1);
            if (change > 0) {
                gains.add(change);
                losses.add(0.0);
            } else {
                gains.add(0.0);
                losses.add(-change);
            }
        }

        // Calculate average gain and loss over the period
        double avgGain = 0.0;
        double avgLoss = 0.0;

        for (int i = gains.size() - period; i < gains.size(); i++) {
            avgGain += gains.get(i);
            avgLoss += losses.get(i);
        }

        avgGain /= period;
        avgLoss /= period;

        if (avgLoss == 0) {
            return 100.0; // Avoid division by zero
        }

        double rs = avgGain / avgLoss;
        double rsi = 100.0 - (100.0 / (1.0 + rs));
        return rsi;
    }

    @Override
    public Signal analyze(MarketCandle candle) {
        // Add current price to history
        priceHistory.add(candle.getClose());
        
        // Keep only recent prices (need RSI_PERIOD + 1 for calculation)
        if (priceHistory.size() > RSI_PERIOD + 1) {
            priceHistory.remove(0);
        }

        // Need at least RSI_PERIOD + 1 prices to calculate RSI
        if (priceHistory.size() < RSI_PERIOD + 1) {
            logger.info(String.format("RSI Strategy: Waiting for more data (%d/%d candles)", 
                    priceHistory.size(), RSI_PERIOD + 1));
            return Signal.HOLD;
        }

        double rsi = calculateRSI(priceHistory, RSI_PERIOD);
        logger.info(String.format("RSI calculated: %.2f (Oversold: <%.1f, Overbought: >%.1f)", 
                rsi, RSI_OVERSOLD, RSI_OVERBOUGHT));

        // Generate signal based on RSI
        if (rsi < RSI_OVERSOLD) {
            logger.info("RSI Strategy: BUY signal (oversold)");
            return Signal.BUY;
        } else if (rsi > RSI_OVERBOUGHT) {
            logger.info("RSI Strategy: SELL signal (overbought)");
            return Signal.SELL;
        } else {
            return Signal.HOLD;
        }
    }
}

