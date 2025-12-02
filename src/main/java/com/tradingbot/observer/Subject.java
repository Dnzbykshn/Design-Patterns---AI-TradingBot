package com.tradingbot.observer;

/**
 * Represents the 'Subject' interface in the Observer Pattern.
 * 
 * Pattern Name: Observer Pattern
 * Role: Subject (Observable) - maintains a list of observers and notifies them of state changes
 * Reason: Used to decouple the market data source from trading strategies, allowing multiple
 * observers to react to market updates without tight coupling. This enables event-driven architecture
 * without while(true) loops in the main logic.
 */
public interface Subject {
    /**
     * Registers an observer to receive updates.
     * @param observer The observer to register
     */
    void registerObserver(Observer observer);

    /**
     * Removes an observer from the notification list.
     * @param observer The observer to remove
     */
    void removeObserver(Observer observer);

    /**
     * Notifies all registered observers of a state change.
     * @param candle The market candle data to broadcast
     */
    void notifyObservers(com.tradingbot.domain.MarketCandle candle);
}

