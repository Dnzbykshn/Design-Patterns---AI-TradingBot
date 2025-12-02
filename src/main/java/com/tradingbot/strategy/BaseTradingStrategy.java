package com.tradingbot.strategy;

import com.tradingbot.domain.MarketCandle;
import com.tradingbot.domain.Signal;
import com.tradingbot.domain.Wallet;
import java.util.logging.Logger;

/**
 * Represents the 'Abstract Class' implementing the Template Method Pattern.
 * 
 * Pattern Name: Template Method Pattern
 * Role: Abstract Class - defines the skeleton of the algorithm with a template method that is final,
 * and abstract methods that subclasses must implement for specific steps.
 * Reason: Used to define the common trading lifecycle (validate -> analyze -> risk check -> execute -> log)
 * while allowing subclasses to customize the analysis step. The template method ensures consistent
 * execution flow across all strategies while maintaining flexibility in the analysis logic.
 */
public abstract class BaseTradingStrategy implements TradingStrategy {
    protected final Wallet wallet;
    protected final Logger logger;
    private static final double TRADE_AMOUNT = 0.1; // Amount of coin to trade per signal

    public BaseTradingStrategy(Wallet wallet) {
        this.wallet = wallet;
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Template Method - defines the algorithm skeleton.
     * This method is FINAL to prevent subclasses from overriding the algorithm structure.
     * 
     * @param candle The market candle to process
     */
    public final void executeStrategyTemplate(MarketCandle candle) {
        // Step 1: Validate Data (Concrete implementation)
        if (!validateData(candle)) {
            logger.warning("Invalid candle data received: " + candle);
            return;
        }

        // Step 2: Analyze (Abstract - must be implemented by subclasses)
        Signal signal = analyze(candle);

        // Step 3: Risk Check & Execution (Concrete implementation)
        if (signal != Signal.HOLD) {
            executeTrade(signal, candle);
        }

        // Step 4: Logging (Concrete implementation)
        logExecution(candle, signal);
    }

    /**
     * Validates the market candle data.
     * @param candle The candle to validate
     * @return true if valid, false otherwise
     */
    protected boolean validateData(MarketCandle candle) {
        if (candle == null) {
            return false;
        }
        if (candle.getClose() <= 0 || candle.getVolume() <= 0) {
            return false;
        }
        if (candle.getHigh() < candle.getLow()) {
            return false;
        }
        return true;
    }

    /**
     * Executes a trade based on the signal.
     * @param signal The trading signal
     * @param candle The market candle with current price
     */
    protected void executeTrade(Signal signal, MarketCandle candle) {
        double price = candle.getClose();
        boolean success = false;

        if (signal == Signal.BUY) {
            success = wallet.buy(price, TRADE_AMOUNT);
            if (success) {
                String msg = String.format("✓ BUY executed: %.2f coins at price %.2f", TRADE_AMOUNT, price);
                logger.info(msg);
                System.out.println(msg);
            } else {
                String msg = "✗ BUY failed: Insufficient USDT balance";
                logger.warning(msg);
                System.out.println(msg);
            }
        } else if (signal == Signal.SELL) {
            success = wallet.sell(price, TRADE_AMOUNT);
            if (success) {
                String msg = String.format("✓ SELL executed: %.2f coins at price %.2f", TRADE_AMOUNT, price);
                logger.info(msg);
                System.out.println(msg);
            } else {
                String msg = "✗ SELL failed: Insufficient coin balance (need coins to sell)";
                logger.warning(msg);
                System.out.println(msg);
            }
        }
    }

    /**
     * Logs the execution details.
     * @param candle The market candle
     * @param signal The generated signal
     */
    protected void logExecution(MarketCandle candle, Signal signal) {
        String logMsg = String.format("[%s] Signal: %s | Price: %.2f | USDT: %.2f | Coin: %.2f",
                this.getClass().getSimpleName(), signal, candle.getClose(), 
                wallet.getUsdtBalance(), wallet.getCoinBalance());
        logger.info(logMsg);
        // Also print to console for visibility
        System.out.println(logMsg);
    }

    /**
     * Gets the wallet associated with this strategy.
     * @return The wallet instance
     */
    public Wallet getWallet() {
        return wallet;
    }

    /**
     * Abstract method that subclasses must implement.
     * This is the customizable step in the template method pattern.
     * 
     * @param candle The market candle to analyze
     * @return A trading signal
     */
    @Override
    public abstract Signal analyze(MarketCandle candle);
}

