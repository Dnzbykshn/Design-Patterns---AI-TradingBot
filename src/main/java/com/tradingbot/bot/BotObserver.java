package com.tradingbot.bot;

import com.tradingbot.domain.MarketCandle;
import com.tradingbot.observer.Observer;
import com.tradingbot.strategy.TradingStrategy;

/**
 * Represents the 'Concrete Observer' in the Observer Pattern and the 'Context' in the Strategy Pattern.
 * 
 * Pattern Name: Observer Pattern & Strategy Pattern
 * Role: 
 *   - Concrete Observer: Implements the Observer interface to receive market data updates
 *   - Context: Uses the TradingStrategy interface to execute trading logic without knowing concrete strategy types
 * Reason: 
 *   - Observer Pattern: Allows the bot to react to market data events in a decoupled, event-driven manner
 *   - Strategy Pattern: Enables runtime switching between different trading strategies (RSI, AI, etc.)
 *     without modifying the bot's code. The bot depends only on the TradingStrategy interface, not concrete classes.
 */
public class BotObserver implements Observer {
    private final TradingStrategy strategy;
    private final String botName;

    public BotObserver(String botName, TradingStrategy strategy) {
        this.botName = botName;
        this.strategy = strategy;
    }

    /**
     * Called by the Subject (MarketDataSubject) when a new market candle is available.
     * This method delegates to the strategy's template method for processing.
     * 
     * @param candle The new market candle data
     */
    @Override
    public void update(MarketCandle candle) {
        // If strategy is BaseTradingStrategy, use the template method
        if (strategy instanceof com.tradingbot.strategy.BaseTradingStrategy) {
            com.tradingbot.strategy.BaseTradingStrategy baseStrategy = 
                (com.tradingbot.strategy.BaseTradingStrategy) strategy;
            baseStrategy.executeStrategyTemplate(candle);
        } else {
            // Fallback: just analyze and log (for custom strategies)
            com.tradingbot.domain.Signal signal = strategy.analyze(candle);
            System.out.println(String.format("[%s] Signal: %s | Price: %.2f", 
                botName, signal, candle.getClose()));
        }
    }

    public String getBotName() {
        return botName;
    }

    public TradingStrategy getStrategy() {
        return strategy;
    }
}

