package com.tradingbot.observer;

import com.tradingbot.domain.MarketCandle;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the 'Concrete Subject' in the Observer Pattern.
 * 
 * Pattern Name: Observer Pattern
 * Role: Concrete Subject - implements the Subject interface and maintains a list of Observer instances.
 * This class generates simulated market data and notifies all registered observers when new candles are available.
 * Reason: Used to create an event-driven market data feed that runs on a separate thread, simulating
 * real-time market updates. This decouples data generation from trading logic and allows multiple
 * strategies to react to the same market events independently.
 */
public class MarketDataSubject implements Subject, Runnable {
    private final List<Observer> observers;
    private final Random random;
    private volatile boolean running;
    private Thread dataThread;
    
    // Simulation parameters
    private double basePrice = 50000.0; // Starting price (e.g., BTC/USDT)
    private static final double VOLATILITY = 0.02; // 2% volatility per candle

    public MarketDataSubject() {
        this.observers = new ArrayList<>();
        this.random = new Random();
        this.running = false;
    }

    @Override
    public synchronized void registerObserver(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public synchronized void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public synchronized void notifyObservers(MarketCandle candle) {
        // Create a copy of the list to avoid concurrent modification issues
        List<Observer> observersCopy = new ArrayList<>(observers);
        for (Observer observer : observersCopy) {
            observer.update(candle);
        }
    }

    /**
     * Starts the market data feed in a separate thread.
     * Generates a new candle every 1 second.
     */
    public void start() {
        if (!running) {
            running = true;
            dataThread = new Thread(this, "MarketDataThread");
            dataThread.start();
        }
    }

    /**
     * Stops the market data feed.
     */
    public void stop() {
        running = false;
        if (dataThread != null) {
            try {
                dataThread.join(2000); // Wait up to 2 seconds for thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Generates a simulated OHLCV candle with random price movements.
     * @return A new MarketCandle instance
     */
    private MarketCandle generateCandle() {
        // Random price change: -VOLATILITY to +VOLATILITY
        double priceChange = (random.nextDouble() - 0.5) * 2 * VOLATILITY;
        double newPrice = basePrice * (1 + priceChange);
        
        // Generate OHLC values with some randomness
        double open = basePrice;
        double close = newPrice;
        double high = Math.max(open, close) * (1 + random.nextDouble() * 0.01);
        double low = Math.min(open, close) * (1 - random.nextDouble() * 0.01);
        double volume = 1000 + random.nextDouble() * 5000; // Random volume between 1000-6000
        
        // Update base price for next candle
        basePrice = close;
        
        return new MarketCandle(open, high, low, close, volume, LocalDateTime.now());
    }

    @Override
    public void run() {
        while (running) {
            MarketCandle candle = generateCandle();
            notifyObservers(candle);
            
            try {
                Thread.sleep(1000); // Wait 1 second before generating next candle
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
                break;
            }
        }
    }

    /**
     * Gets the number of registered observers.
     * @return The count of observers
     */
    public synchronized int getObserverCount() {
        return observers.size();
    }
}

