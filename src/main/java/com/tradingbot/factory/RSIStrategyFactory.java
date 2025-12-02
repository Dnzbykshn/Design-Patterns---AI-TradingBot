package com.tradingbot.factory;

import com.tradingbot.domain.Wallet;
import com.tradingbot.strategy.RSIStrategy;
import com.tradingbot.strategy.TradingStrategy;

/**
 * Represents the 'Concrete Creator' in the Factory Method Pattern.
 * 
 * Pattern Name: Factory Method Pattern
 * Role: Concrete Creator - implements the factory method to create RSIStrategy instances
 * Reason: Encapsulates the creation logic for RSIStrategy. If RSIStrategy constructor changes,
 * only this factory needs to be updated, not the client code.
 */
public class RSIStrategyFactory extends StrategyFactory {
    
    @Override
    public TradingStrategy createStrategy(Wallet wallet, String... config) {
        // RSIStrategy doesn't need additional configuration
        return new RSIStrategy(wallet);
    }
}



