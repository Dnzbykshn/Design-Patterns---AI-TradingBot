package com.tradingbot.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WalletTest {
    private Wallet wallet;
    private static final double INITIAL_BALANCE = 10000.0;

    @BeforeEach
    void setUp() {
        wallet = new Wallet(INITIAL_BALANCE);
    }

    @Test
    void testInitialState() {
        assertEquals(INITIAL_BALANCE, wallet.getUsdtBalance());
        assertEquals(0.0, wallet.getCoinBalance());
        assertEquals(INITIAL_BALANCE, wallet.getInitialUsdtBalance());
    }

    @Test
    void testBuySuccess() {
        double price = 50000.0;
        double amount = 0.1;
        double cost = price * amount;

        assertTrue(wallet.buy(price, amount));
        assertEquals(INITIAL_BALANCE - cost, wallet.getUsdtBalance(), 0.01);
        assertEquals(amount, wallet.getCoinBalance(), 0.01);
    }

    @Test
    void testBuyInsufficientFunds() {
        double price = 50000.0;
        double amount = 1.0; // Too expensive

        assertFalse(wallet.buy(price, amount));
        assertEquals(INITIAL_BALANCE, wallet.getUsdtBalance());
        assertEquals(0.0, wallet.getCoinBalance());
    }

    @Test
    void testSellSuccess() {
        // First buy some coins
        wallet.buy(50000.0, 0.1);
        
        // Then sell
        double price = 51000.0;
        double amount = 0.05;
        
        assertTrue(wallet.sell(price, amount));
        assertEquals(0.05, wallet.getCoinBalance(), 0.01);
    }

    @Test
    void testSellInsufficientCoins() {
        assertFalse(wallet.sell(50000.0, 0.1));
    }

    @Test
    void testGetTotalValue() {
        wallet.buy(50000.0, 0.1);
        double currentPrice = 51000.0;
        double expectedValue = wallet.getUsdtBalance() + (0.1 * currentPrice);
        
        assertEquals(expectedValue, wallet.getTotalValue(currentPrice), 0.01);
    }

    @Test
    void testGetPnLPercentage() {
        wallet.buy(50000.0, 0.1);
        double currentPrice = 55000.0; // 10% price increase
        double pnl = wallet.getPnLPercentage(currentPrice);
        
        // Should be positive since price increased
        assertTrue(pnl > 0);
    }
}

