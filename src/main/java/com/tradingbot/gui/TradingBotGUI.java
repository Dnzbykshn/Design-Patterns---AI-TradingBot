package com.tradingbot.gui;

import com.tradingbot.bot.BotObserver;
import com.tradingbot.domain.MarketCandle;
import com.tradingbot.domain.Signal;
import com.tradingbot.domain.Wallet;
import com.tradingbot.factory.StrategyFactory;
import com.tradingbot.observer.MarketDataSubject;
import com.tradingbot.strategy.BaseTradingStrategy;
import com.tradingbot.strategy.TradingStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 * GUI for the AI Trading Bot using Swing (Java's built-in GUI framework).
 * Provides a user-friendly interface to control and monitor the trading bot.
 */
public class TradingBotGUI extends JFrame {
    private static final double INITIAL_BALANCE = 10000.0;
    
    private MarketDataSubject marketData;
    private BotObserver bot;
    private TradingStrategy strategy;
    private Wallet wallet;
    
    // GUI Components
    private JComboBox<String> strategyComboBox;
    private JTextField modelPathField;
    private JButton startButton;
    private JButton stopButton;
    
    private JLabel usdtBalanceLabel;
    private JLabel coinBalanceLabel;
    private JLabel totalValueLabel;
    private JLabel pnlLabel;
    
    private JTextArea logArea;
    private JLabel currentPriceLabel;
    private JLabel lastSignalLabel;
    
    private javax.swing.Timer updateTimer;
    private boolean isRunning = false;
    
    private DecimalFormat df = new DecimalFormat("#,##0.00");
    
    public TradingBotGUI() {
        initializeGUI();
        // Ensure window is visible and on top
        setVisible(true);
        toFront();
        requestFocus();
    }
    
    private void initializeGUI() {
        setTitle("AI Trading Bot - Design Patterns Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Top Panel - Strategy Selection
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - Wallet Status and Logs
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel - Control Buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Initialize market data
        marketData = new MarketDataSubject();
        
        // Setup update timer (update GUI every 500ms)
        updateTimer = new javax.swing.Timer(500, e -> updateGUI());
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Strategy Configuration"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Strategy Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Strategy:"), gbc);
        
        gbc.gridx = 1;
        strategyComboBox = new JComboBox<>(new String[]{"RSI Strategy", "AI Strategy"});
        strategyComboBox.addActionListener(e -> {
            boolean isAI = strategyComboBox.getSelectedIndex() == 1;
            modelPathField.setEnabled(isAI);
        });
        panel.add(strategyComboBox, gbc);
        
        // Model Path (for AI Strategy)
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Model Path:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        modelPathField = new JTextField("ai_model/trading_model.onnx", 30);
        modelPathField.setEnabled(false);
        panel.add(modelPathField, gbc);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Left Panel - Wallet Status
        JPanel walletPanel = createWalletPanel();
        panel.add(walletPanel, BorderLayout.WEST);
        
        // Right Panel - Market Info
        JPanel marketPanel = createMarketPanel();
        panel.add(marketPanel, BorderLayout.CENTER);
        
        // Bottom - Logs
        JPanel logPanel = createLogPanel();
        panel.add(logPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createWalletPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Wallet Status"));
        panel.setPreferredSize(new Dimension(300, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("USDT Balance:"), gbc);
        gbc.gridx = 1;
        usdtBalanceLabel = new JLabel(df.format(INITIAL_BALANCE));
        usdtBalanceLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(usdtBalanceLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Coin Balance:"), gbc);
        gbc.gridx = 1;
        coinBalanceLabel = new JLabel("0.00");
        coinBalanceLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(coinBalanceLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Total Value:"), gbc);
        gbc.gridx = 1;
        totalValueLabel = new JLabel(df.format(INITIAL_BALANCE));
        totalValueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(totalValueLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("PnL:"), gbc);
        gbc.gridx = 1;
        pnlLabel = new JLabel("0.00%");
        pnlLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        pnlLabel.setForeground(Color.GRAY);
        panel.add(pnlLabel, gbc);
        
        return panel;
    }
    
    private JPanel createMarketPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Market Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Current Price:"), gbc);
        gbc.gridx = 1;
        currentPriceLabel = new JLabel("Waiting...");
        currentPriceLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        panel.add(currentPriceLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Last Signal:"), gbc);
        gbc.gridx = 1;
        lastSignalLabel = new JLabel("HOLD");
        lastSignalLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        lastSignalLabel.setForeground(Color.GRAY);
        panel.add(lastSignalLabel, gbc);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Trading Logs"));
        
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        startButton = new JButton("Start Bot");
        startButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        startButton.setPreferredSize(new Dimension(150, 40));
        startButton.addActionListener(e -> startBot());
        panel.add(startButton);
        
        stopButton = new JButton("Stop Bot");
        stopButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        stopButton.setPreferredSize(new Dimension(150, 40));
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopBot());
        panel.add(stopButton);
        
        return panel;
    }
    
    private void startBot() {
        if (isRunning) {
            return;
        }
        
        try {
            // Create wallet
            wallet = new Wallet(INITIAL_BALANCE);
            
            // Get strategy selection
            String strategyType = strategyComboBox.getSelectedIndex() == 0 ? "RSI" : "AI";
            String[] config = null;
            
            if (strategyType.equals("AI")) {
                String modelPath = modelPathField.getText().trim();
                if (modelPath.isEmpty()) {
                    modelPath = "ai_model/trading_model.onnx";
                }
                config = new String[]{modelPath};
            }
            
            // Use Factory Method Pattern to create strategy
            StrategyFactory factory = StrategyFactory.getFactory(strategyType);
            strategy = factory.createAndConfigureStrategy(wallet, config);
            
            // Create bot observer
            String botName = strategyType + "-Bot";
            bot = new BotObserver(botName, strategy);
            
            // Register observer
            marketData.registerObserver(bot);
            
            // Start market data feed
            marketData.start();
            
            // Start GUI updates
            updateTimer.start();
            isRunning = true;
            
            // Update UI
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            strategyComboBox.setEnabled(false);
            modelPathField.setEnabled(false);
            
            log("✓ " + strategyType + " Strategy started using Factory Method Pattern");
            log("✓ Bot registered as observer");
            log("✓ Market data feed started");
            log("=".repeat(60));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error starting bot: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            log("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void stopBot() {
        if (!isRunning) {
            return;
        }
        
        // Stop market data feed
        marketData.stop();
        
        // Stop GUI updates
        updateTimer.stop();
        isRunning = false;
        
        // Update UI
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        strategyComboBox.setEnabled(true);
        modelPathField.setEnabled(strategyComboBox.getSelectedIndex() == 1);
        
        // Display final stats
        if (strategy instanceof BaseTradingStrategy) {
            Wallet strategyWallet = ((BaseTradingStrategy) strategy).getWallet();
            log("=".repeat(60));
            log("Final Wallet State:");
            log("  USDT Balance: " + df.format(strategyWallet.getUsdtBalance()));
            log("  Coin Balance: " + df.format(strategyWallet.getCoinBalance()));
            log("  Total Value: " + df.format(strategyWallet.getTotalValue(strategyWallet.getCurrentPrice())));
            log("  PnL: " + df.format(strategyWallet.getPnLPercentage(strategyWallet.getCurrentPrice())) + "%");
        }
        
        log("✓ Trading bot stopped");
    }
    
    private void updateGUI() {
        if (!isRunning || strategy == null || bot == null) {
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            if (strategy instanceof BaseTradingStrategy) {
                Wallet strategyWallet = ((BaseTradingStrategy) strategy).getWallet();
                
                // Update wallet labels
                usdtBalanceLabel.setText(df.format(strategyWallet.getUsdtBalance()));
                coinBalanceLabel.setText(df.format(strategyWallet.getCoinBalance()));
                
                // Get last candle from bot
                MarketCandle lastCandle = bot.getLastCandle();
                if (lastCandle != null) {
                    double currentPrice = lastCandle.getClose();
                    double totalValue = strategyWallet.getTotalValue(currentPrice);
                    totalValueLabel.setText(df.format(totalValue));
                    
                    double pnl = strategyWallet.getPnLPercentage(currentPrice);
                    pnlLabel.setText(df.format(pnl) + "%");
                    pnlLabel.setForeground(pnl >= 0 ? Color.GREEN : Color.RED);
                    
                    currentPriceLabel.setText(df.format(currentPrice));
                    
                    // Update signal label
                    Signal signal = bot.getLastSignal();
                    lastSignalLabel.setText(signal.toString());
                    switch (signal) {
                        case BUY:
                            lastSignalLabel.setForeground(Color.GREEN);
                            break;
                        case SELL:
                            lastSignalLabel.setForeground(Color.RED);
                            break;
                        default:
                            lastSignalLabel.setForeground(Color.GRAY);
                    }
                }
            }
        });
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            TradingBotGUI gui = new TradingBotGUI();
            gui.setVisible(true);
        });
    }
}

