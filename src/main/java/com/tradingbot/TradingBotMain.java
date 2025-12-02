package com.tradingbot;

import com.tradingbot.bot.BotObserver;
import com.tradingbot.domain.Wallet;
import com.tradingbot.factory.StrategyFactory;
import com.tradingbot.observer.MarketDataSubject;
import com.tradingbot.strategy.AIStrategy;
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

        String choice = scanner.nextLine().trim();
        
        Wallet wallet = new Wallet(INITIAL_BALANCE);
        TradingStrategy strategy = null;
        BotObserver bot = null;
        String botName = "RSI-Bot";

        try {
            // Use Factory Method Pattern to create strategy
            StrategyFactory factory = StrategyFactory.getFactory(choice);
            
            // Get additional config if needed (e.g., model path for AI)
            String[] config = null;
            if (choice.equals("2") || choice.equalsIgnoreCase("AI")) {
                System.out.print("Enter path to ONNX model file (or press Enter for 'ai_model/trading_model.onnx'): ");
                String modelPath = scanner.nextLine().trim();
                config = new String[]{modelPath};
                botName = "AI-Bot";
            }
            
            // Factory creates the strategy
            strategy = factory.createAndConfigureStrategy(wallet, config);
            bot = new BotObserver(botName, strategy);
            
            String strategyName = choice.equals("2") || choice.equalsIgnoreCase("AI") ? "AI" : "RSI";
            System.out.println("✓ " + strategyName + " Strategy created using Factory Method Pattern");
            
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Invalid choice: " + e.getMessage());
            System.err.println("   Falling back to RSI Strategy...");
            StrategyFactory factory = StrategyFactory.getFactory("RSI");
            strategy = factory.createStrategy(wallet);
            bot = new BotObserver("RSI-Bot", strategy);
        } catch (Exception e) {
            System.err.println("❌ Failed to create strategy: " + e.getMessage());
            e.printStackTrace();
            System.err.println("   Falling back to RSI Strategy...");
            StrategyFactory factory = StrategyFactory.getFactory("RSI");
            strategy = factory.createStrategy(wallet);
            bot = new BotObserver("RSI-Bot", strategy);
        }

        // Register bot as observer
        marketData.registerObserver(bot);
        System.out.println("✓ Bot registered as observer");
        System.out.println();

        // Display initial wallet state
        if (strategy instanceof com.tradingbot.strategy.BaseTradingStrategy) {
            Wallet strategyWallet = ((com.tradingbot.strategy.BaseTradingStrategy) strategy).getWallet();
            System.out.println("Initial Wallet State:");
            System.out.println("  USDT Balance: " + strategyWallet.getUsdtBalance());
            System.out.println("  Coin Balance: " + strategyWallet.getCoinBalance());
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
            Wallet strategyWallet = ((com.tradingbot.strategy.BaseTradingStrategy) strategy).getWallet();
            System.out.println("\nFinal Wallet State:");
            System.out.println("  USDT Balance: " + String.format("%.2f", strategyWallet.getUsdtBalance()));
            System.out.println("  Coin Balance: " + String.format("%.2f", strategyWallet.getCoinBalance()));
            System.out.println("  Total Value: " + String.format("%.2f", strategyWallet.getTotalValue(strategyWallet.getCurrentPrice())));
            System.out.println("  PnL: " + String.format("%.2f%%", strategyWallet.getPnLPercentage(strategyWallet.getCurrentPrice())));
        }

        // Cleanup
        if (strategy instanceof AIStrategy) {
            ((AIStrategy) strategy).close();
        }

        scanner.close();
        System.out.println("\n✓ Trading bot stopped. Goodbye!");
    }
}

