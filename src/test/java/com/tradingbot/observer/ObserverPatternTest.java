package com.tradingbot.observer;

import com.tradingbot.domain.MarketCandle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ObserverPatternTest {
    private MarketDataSubject subject;
    private TestObserver observer1;
    private TestObserver observer2;

    @BeforeEach
    void setUp() {
        subject = new MarketDataSubject();
        observer1 = new TestObserver("Observer1");
        observer2 = new TestObserver("Observer2");
    }

    @Test
    void testRegisterObserver() {
        subject.registerObserver(observer1);
        assertEquals(1, subject.getObserverCount());
        
        subject.registerObserver(observer2);
        assertEquals(2, subject.getObserverCount());
    }

    @Test
    void testRemoveObserver() {
        subject.registerObserver(observer1);
        subject.registerObserver(observer2);
        
        subject.removeObserver(observer1);
        assertEquals(1, subject.getObserverCount());
    }

    @Test
    void testNotifyObservers() {
        subject.registerObserver(observer1);
        subject.registerObserver(observer2);
        
        MarketCandle candle = new MarketCandle(50000, 51000, 49000, 50500, 1000, LocalDateTime.now());
        subject.notifyObservers(candle);
        
        assertEquals(1, observer1.getUpdateCount());
        assertEquals(1, observer2.getUpdateCount());
        assertEquals(candle, observer1.getLastCandle());
        assertEquals(candle, observer2.getLastCandle());
    }

    @Test
    void testMultipleNotifications() {
        subject.registerObserver(observer1);
        
        MarketCandle candle1 = new MarketCandle(50000, 51000, 49000, 50500, 1000, LocalDateTime.now());
        MarketCandle candle2 = new MarketCandle(50500, 51500, 49500, 51000, 1200, LocalDateTime.now());
        
        subject.notifyObservers(candle1);
        subject.notifyObservers(candle2);
        
        assertEquals(2, observer1.getUpdateCount());
        assertEquals(candle2, observer1.getLastCandle());
    }

    // Helper class for testing
    private static class TestObserver implements Observer {
        private final List<MarketCandle> receivedCandles;
        
        public TestObserver(String name) {
            this.receivedCandles = new ArrayList<>();
        }
        
        @Override
        public void update(MarketCandle candle) {
            receivedCandles.add(candle);
        }
        
        public int getUpdateCount() {
            return receivedCandles.size();
        }
        
        public MarketCandle getLastCandle() {
            return receivedCandles.isEmpty() ? null : receivedCandles.get(receivedCandles.size() - 1);
        }
    }
}

