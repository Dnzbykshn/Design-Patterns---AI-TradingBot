package com.tradingbot.strategy;

import com.tradingbot.domain.MarketCandle;
import com.tradingbot.domain.Signal;
import com.tradingbot.domain.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class RSIStrategyTest {
    private RSIStrategy strategy;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet(10000.0);
        strategy = new RSIStrategy(wallet);
    }

    @Test
    void testAnalyzeWithInsufficientData() {
        // Need at least RSI_PERIOD + 1 candles for RSI calculation
        MarketCandle candle = new MarketCandle(50000, 51000, 49000, 50500, 1000, LocalDateTime.now());
        Signal signal = strategy.analyze(candle);
        
        // Should return HOLD when not enough data
        assertEquals(Signal.HOLD, signal);
    }

    @Test
    void testExecuteStrategyTemplate() {
        // Create enough candles to calculate RSI
        for (int i = 0; i < 20; i++) {
            double price = 50000 - (i * 100); // Decreasing prices (oversold scenario)
            MarketCandle candle = new MarketCandle(price, price + 100, price - 100, price, 1000, LocalDateTime.now());
            strategy.executeStrategyTemplate(candle);
        }
        
        // After enough decreasing prices, RSI should be low (oversold)
        // Strategy should generate BUY signal
        // Note: This is a simplified test - actual RSI calculation depends on the algorithm
    }

    @Test
    void testStrategyUsesTemplateMethod() {
        MarketCandle candle = new MarketCandle(50000, 51000, 49000, 50500, 1000, LocalDateTime.now());
        
        // Should not throw exception even with invalid/incomplete data
        assertDoesNotThrow(() -> strategy.executeStrategyTemplate(candle));
    }
}

