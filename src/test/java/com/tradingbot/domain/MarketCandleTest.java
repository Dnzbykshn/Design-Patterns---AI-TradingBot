package com.tradingbot.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class MarketCandleTest {
    @Test
    void testMarketCandleCreation() {
        LocalDateTime timestamp = LocalDateTime.now();
        MarketCandle candle = new MarketCandle(50000, 51000, 49000, 50500, 1000, timestamp);
        
        assertEquals(50000, candle.getOpen());
        assertEquals(51000, candle.getHigh());
        assertEquals(49000, candle.getLow());
        assertEquals(50500, candle.getClose());
        assertEquals(1000, candle.getVolume());
        assertEquals(timestamp, candle.getTimestamp());
    }

    @Test
    void testToString() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 12, 0);
        MarketCandle candle = new MarketCandle(50000, 51000, 49000, 50500, 1000, timestamp);
        
        String str = candle.toString();
        assertTrue(str.contains("MarketCandle"));
        assertTrue(str.contains("50000"));
        assertTrue(str.contains("50500"));
    }
}

