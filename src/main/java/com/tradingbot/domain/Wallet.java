package com.tradingbot.domain;

/**
 * Manages virtual portfolio with USDT and Coin balances.
 * Handles PnL (Profit and Loss) calculations and trade execution logic.
 */
public class Wallet {
    private double usdtBalance;
    private double coinBalance;
    private double initialUsdtBalance;
    private double currentPrice;

    public Wallet(double initialUsdtBalance) {
        this.usdtBalance = initialUsdtBalance;
        this.initialUsdtBalance = initialUsdtBalance;
        this.coinBalance = 0.0;
        this.currentPrice = 0.0;
    }

    /**
     * Executes a BUY order if sufficient USDT balance is available.
     * @param price The price at which to buy
     * @param amount The amount of coin to buy
     * @return true if order was executed, false if insufficient funds
     */
    public boolean buy(double price, double amount) {
        double cost = price * amount;
        if (cost > usdtBalance) {
            return false; // Insufficient funds
        }
        this.usdtBalance -= cost;
        this.coinBalance += amount;
        this.currentPrice = price;
        return true;
    }

    /**
     * Executes a SELL order if sufficient coin balance is available.
     * @param price The price at which to sell
     * @param amount The amount of coin to sell
     * @return true if order was executed, false if insufficient coins
     */
    public boolean sell(double price, double amount) {
        if (amount > coinBalance) {
            return false; // Insufficient coins
        }
        this.coinBalance -= amount;
        this.usdtBalance += price * amount;
        this.currentPrice = price;
        return true;
    }

    /**
     * Calculates the total portfolio value in USDT.
     * @param currentPrice The current market price of the coin
     * @return Total value (USDT balance + coin value in USDT)
     */
    public double getTotalValue(double currentPrice) {
        return usdtBalance + (coinBalance * currentPrice);
    }

    /**
     * Calculates Profit and Loss (PnL) as a percentage.
     * @param currentPrice The current market price of the coin
     * @return PnL percentage
     */
    public double getPnLPercentage(double currentPrice) {
        double currentValue = getTotalValue(currentPrice);
        return ((currentValue - initialUsdtBalance) / initialUsdtBalance) * 100.0;
    }

    public double getUsdtBalance() {
        return usdtBalance;
    }

    public double getCoinBalance() {
        return coinBalance;
    }

    public double getInitialUsdtBalance() {
        return initialUsdtBalance;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    @Override
    public String toString() {
        return String.format("Wallet{USDT=%.2f, Coin=%.2f, Total=%.2f, PnL=%.2f%%}",
                usdtBalance, coinBalance, getTotalValue(currentPrice), getPnLPercentage(currentPrice));
    }
}

