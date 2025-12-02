package com.tradingbot;

import com.tradingbot.bot.BotObserver;
import com.tradingbot.domain.Wallet;
import com.tradingbot.observer.MarketDataSubject;
import com.tradingbot.strategy.AIStrategy;
import com.tradingbot.strategy.RSIStrategy;
import com.tradingbot.strategy.TradingStrategy;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * Main entry point for the AI Trading Bot application.
 * Demonstrates the use of Observer Pattern and Strategy Pattern.
 */
public class TradingBotMain {
    private static final double INITIAL_BALANCE = 10000.0; // Starting with 10,000 USDT

    public static void main(String[] args) {
        // Configure logging
        LogManager.getLogManager().reset();
        java.util.logging.Logger.getLogger("ai.onnxruntime").setLevel(Level.SEVERE);
        
        // Enable console logging for strategies
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);
        for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
            handler.setLevel(Level.INFO);
        }

        System.out.println("=".repeat(60));
        System.out.println("🚀 AI Trading Bot - Design Patterns Demo");
        System.out.println("=".repeat(60));
        System.out.println();

        // Create market data subject
        MarketDataSubject marketData = new MarketDataSubject();
        System.out.println("✓ Market Data Subject created");

        // Ask user to choose strategy
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nSelect Trading Strategy:");
        System.out.println("1. RSI Strategy (Math-based)");
        System.out.println("2. AI Strategy (ML-based)");
        System.out.print("Enter choice (1 or 2): ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        TradingStrategy strategy;
        BotObserver bot;

        if (choice == 1) {
            // RSI Strategy
            Wallet wallet = new Wallet(INITIAL_BALANCE);
            strategy = new RSIStrategy(wallet);
            bot = new BotObserver("RSI-Bot", strategy);
            System.out.println("✓ RSI Strategy selected");
        } else if (choice == 2) {
            // AI Strategy
            System.out.print("Enter path to ONNX model file (or press Enter for 'ai_model/trading_model.onnx'): ");
            String modelPath = scanner.nextLine().trim();
            if (modelPath.isEmpty()) {
                modelPath = "ai_model/trading_model.onnx";
            }

            Wallet wallet = new Wallet(INITIAL_BALANCE);
            try {
                strategy = new AIStrategy(wallet, modelPath);
                bot = new BotObserver("AI-Bot", strategy);
                System.out.println("✓ AI Strategy selected");
            } catch (Exception e) {
                System.err.println("❌ Failed to load AI model: " + e.getMessage());
                System.err.println("   Falling back to RSI Strategy...");
                strategy = new RSIStrategy(wallet);
                bot = new BotObserver("RSI-Bot", strategy);
            }
        } else {
            System.out.println("Invalid choice. Using RSI Strategy by default.");
            Wallet wallet = new Wallet(INITIAL_BALANCE);
            strategy = new RSIStrategy(wallet);
            bot = new BotObserver("RSI-Bot", strategy);
        }

        // Register bot as observer
        marketData.registerObserver(bot);
        System.out.println("✓ Bot registered as observer");
        System.out.println();

        // Display initial wallet state
        if (strategy instanceof com.tradingbot.strategy.BaseTradingStrategy) {
            Wallet wallet = ((com.tradingbot.strategy.BaseTradingStrategy) strategy).getWallet();
            System.out.println("Initial Wallet State:");
            System.out.println("  USDT Balance: " + wallet.getUsdtBalance());
            System.out.println("  Coin Balance: " + wallet.getCoinBalance());
            System.out.println();
        }

        // Start market data feed
        System.out.println("Starting market data feed...");
        System.out.println("Press Enter to stop the bot...");
        System.out.println("=".repeat(60));
        System.out.println();

        marketData.start();

        // Wait for user to stop
        scanner.nextLine();

        // Stop market data feed
        System.out.println("\nStopping market data feed...");
        marketData.stop();

        // Display final wallet state
        if (strategy instanceof com.tradingbot.strategy.BaseTradingStrategy) {
            Wallet wallet = ((com.tradingbot.strategy.BaseTradingStrategy) strategy).getWallet();
            System.out.println("\nFinal Wallet State:");
            System.out.println("  USDT Balance: " + String.format("%.2f", wallet.getUsdtBalance()));
            System.out.println("  Coin Balance: " + String.format("%.2f", wallet.getCoinBalance()));
            System.out.println("  Total Value: " + String.format("%.2f", wallet.getTotalValue(wallet.getCurrentPrice())));
            System.out.println("  PnL: " + String.format("%.2f%%", wallet.getPnLPercentage(wallet.getCurrentPrice())));
        }

        // Cleanup
        if (strategy instanceof AIStrategy) {
            ((AIStrategy) strategy).close();
        }

        scanner.close();
        System.out.println("\n✓ Trading bot stopped. Goodbye!");
    }
}

