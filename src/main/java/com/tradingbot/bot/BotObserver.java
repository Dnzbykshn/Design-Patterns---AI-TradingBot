package com.tradingbot.bot;

import com.tradingbot.domain.MarketCandle;
import com.tradingbot.domain.Signal;
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

    private Signal lastSignal = Signal.HOLD;
    private MarketCandle lastCandle = null;
    
    /**
     * Called by the Subject (MarketDataSubject) when a new market candle is available.
     * This method delegates to the strategy's template method for processing.
     * 
     * @param candle The new market candle data
     */
    @Override
    public void update(MarketCandle candle) {
        this.lastCandle = candle;
        
        // If strategy is BaseTradingStrategy, use the template method
        if (strategy instanceof com.tradingbot.strategy.BaseTradingStrategy) {
            com.tradingbot.strategy.BaseTradingStrategy baseStrategy = 
                (com.tradingbot.strategy.BaseTradingStrategy) strategy;
            baseStrategy.executeStrategyTemplate(candle);
            
            // Extract signal for GUI display
            lastSignal = baseStrategy.analyze(candle);
        } else {
            // Fallback: just analyze and log (for custom strategies)
            lastSignal = strategy.analyze(candle);
            System.out.println(String.format("[%s] Signal: %s | Price: %.2f", 
                botName, lastSignal, candle.getClose()));
        }
    }
    
    public Signal getLastSignal() {
        return lastSignal;
    }
    
    public MarketCandle getLastCandle() {
        return lastCandle;
    }

    public String getBotName() {
        return botName;
    }

    public TradingStrategy getStrategy() {
        return strategy;
    }
}

