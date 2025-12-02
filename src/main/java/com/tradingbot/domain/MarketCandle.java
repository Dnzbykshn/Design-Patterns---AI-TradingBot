package com.tradingbot.domain;

import java.time.LocalDateTime;

/**
 * Represents OHLCV (Open, High, Low, Close, Volume) market data with timestamp.
 * This is a domain entity that encapsulates a single candlestick/bar of market data.
 */
public class MarketCandle {
    private final double open;
    private final double high;
    private final double low;
    private final double close;
    private final double volume;
    private final LocalDateTime timestamp;

    public MarketCandle(double open, double high, double low, double close, double volume, LocalDateTime timestamp) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.timestamp = timestamp;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public double getVolume() {
        return volume;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("MarketCandle{timestamp=%s, O=%.2f, H=%.2f, L=%.2f, C=%.2f, V=%.2f}",
                timestamp, open, high, low, close, volume);
    }
}

