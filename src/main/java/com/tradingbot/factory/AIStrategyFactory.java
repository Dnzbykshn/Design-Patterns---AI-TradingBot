package com.tradingbot.factory;

import com.tradingbot.domain.Wallet;
import com.tradingbot.strategy.AIStrategy;
import com.tradingbot.strategy.TradingStrategy;

/**
 * Represents the 'Concrete Creator' in the Factory Method Pattern.
 * 
 * Pattern Name: Factory Method Pattern
 * Role: Concrete Creator - implements the factory method to create AIStrategy instances
 * Reason: Encapsulates the creation logic for AIStrategy, including model path handling.
 * If AIStrategy creation logic changes (e.g., different model loading), only this factory
 * needs to be updated.
 */
public class AIStrategyFactory extends StrategyFactory {
    
    private static final String DEFAULT_MODEL_PATH = "ai_model/trading_model.onnx";
    
    @Override
    public TradingStrategy createStrategy(Wallet wallet, String... config) {
        // Extract model path from config, use default if not provided
        String modelPath = (config != null && config.length > 0 && !config[0].trim().isEmpty())
                ? config[0].trim()
                : DEFAULT_MODEL_PATH;
        
        return new AIStrategy(wallet, modelPath);
    }
}

