package com.tradingbot.factory;

import com.tradingbot.domain.Wallet;
import com.tradingbot.strategy.TradingStrategy;
import com.tradingbot.strategy.RSIStrategy;
import com.tradingbot.strategy.AIStrategy;

/**
 * Represents the 'Creator' in the Factory Method Pattern.
 * 
 * Pattern Name: Factory Method Pattern
 * Role: Creator - defines the factory method for creating strategy objects
 * Reason: Used to encapsulate strategy object creation logic. This allows adding new strategies
 * without modifying the client code (TradingBotMain). Follows Open/Closed Principle - open for
 * extension (new strategies), closed for modification (main code doesn't change).
 */
public abstract class StrategyFactory {
    
    /**
     * Factory Method - creates a TradingStrategy instance.
     * This is the abstract factory method that subclasses must implement.
     * 
     * @param wallet The wallet to associate with the strategy
     * @param config Optional configuration parameters (e.g., model path for AI strategy)
     * @return A TradingStrategy instance
     */
    public abstract TradingStrategy createStrategy(Wallet wallet, String... config);
    
    /**
     * Template method that can perform additional setup after strategy creation.
     * 
     * @param wallet The wallet
     * @param config Optional configuration
     * @return Configured TradingStrategy instance
     */
    public TradingStrategy createAndConfigureStrategy(Wallet wallet, String... config) {
        TradingStrategy strategy = createStrategy(wallet, config);
        // Additional configuration can be added here
        return strategy;
    }
    
    /**
     * Static factory method to get the appropriate factory based on strategy type.
     * This is a convenience method for the client.
     * 
     * @param strategyType The type of strategy ("RSI" or "AI")
     * @return The appropriate StrategyFactory instance
     */
    public static StrategyFactory getFactory(String strategyType) {
        if (strategyType == null) {
            throw new IllegalArgumentException("Strategy type cannot be null");
        }
        
        switch (strategyType.toUpperCase()) {
            case "RSI":
            case "1":
                return new RSIStrategyFactory();
            case "AI":
            case "2":
                return new AIStrategyFactory();
            default:
                throw new IllegalArgumentException("Unknown strategy type: " + strategyType);
        }
    }
}


