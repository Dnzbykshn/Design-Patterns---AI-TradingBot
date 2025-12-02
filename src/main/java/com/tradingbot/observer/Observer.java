package com.tradingbot.observer;

import com.tradingbot.domain.MarketCandle;

/**
 * Represents the 'Observer' interface in the Observer Pattern.
 * 
 * Pattern Name: Observer Pattern
 * Role: Observer - receives updates from the Subject when state changes occur
 * Reason: Allows trading strategies and bots to react to market data updates in a decoupled manner.
 * Observers are notified automatically when new market candles are available, enabling event-driven
 * trading logic without polling or tight coupling to the data source.
 */
public interface Observer {
    /**
     * Called by the Subject when a state change occurs.
     * @param candle The new market candle data
     */
    void update(MarketCandle candle);
}

