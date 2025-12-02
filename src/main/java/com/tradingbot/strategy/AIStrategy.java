package com.tradingbot.strategy;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import com.tradingbot.domain.MarketCandle;
import com.tradingbot.domain.Signal;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the 'Concrete Strategy' in the Strategy Pattern that uses AI/ML for trading decisions.
 * 
 * Pattern Name: Strategy Pattern & Template Method Pattern
 * Role: Concrete Strategy - implements the TradingStrategy interface and extends BaseTradingStrategy.
 * This class loads an ONNX model and uses it for AI-based trading signal generation.
 * Reason: Used Strategy Pattern to allow runtime switching between AI and math-based logic.
 * Extends BaseTradingStrategy to leverage the Template Method Pattern for consistent trade execution flow.
 * The AI model inference is encapsulated in the analyze() method, making it interchangeable with other strategies.
 */
public class AIStrategy extends BaseTradingStrategy {
    private OrtSession session;
    private OrtEnvironment environment;
    private final String modelPath;
    private final List<Double> priceHistory;
    private static final int SMA_PERIOD = 20;
    private static final int MOMENTUM_PERIOD = 5;
    private static final int RSI_PERIOD = 14;

    public AIStrategy(com.tradingbot.domain.Wallet wallet, String modelPath) {
        super(wallet);
        this.modelPath = modelPath;
        this.priceHistory = new ArrayList<>();
        this.environment = OrtEnvironment.getEnvironment();
        loadModel();
    }

    /**
     * Loads the ONNX model from the specified path.
     */
    private void loadModel() {
        try {
            File modelFile = new File(modelPath);
            
            // If relative path doesn't exist, try to resolve it
            if (!modelFile.exists()) {
                // Try to resolve relative to current working directory
                String currentDir = System.getProperty("user.dir");
                File absolutePath = new File(currentDir, modelPath);
                if (absolutePath.exists()) {
                    modelFile = absolutePath;
                    logger.info("Resolved model path to: " + modelFile.getAbsolutePath());
                } else {
                    logger.severe("ONNX model file not found at: " + modelPath);
                    logger.severe("Also tried: " + absolutePath.getAbsolutePath());
                    logger.severe("Current working directory: " + currentDir);
                    throw new RuntimeException("Model file not found: " + modelPath);
                }
            }
            
            String absoluteModelPath = modelFile.getAbsolutePath();
            logger.info("Loading ONNX model from: " + absoluteModelPath);
            session = environment.createSession(absoluteModelPath);
            logger.info("ONNX model loaded successfully!");
        } catch (Exception e) {
            logger.severe("Failed to load ONNX model: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load ONNX model", e);
        }
    }

    /**
     * Calculates RSI (Relative Strength Index).
     */
    private double calculateRSI(List<Double> prices, int period) {
        if (prices.size() < period + 1) {
            return 50.0;
        }

        List<Double> gains = new ArrayList<>();
        List<Double> losses = new ArrayList<>();

        for (int i = 1; i < prices.size(); i++) {
            double change = prices.get(i) - prices.get(i - 1);
            if (change > 0) {
                gains.add(change);
                losses.add(0.0);
            } else {
                gains.add(0.0);
                losses.add(-change);
            }
        }

        double avgGain = 0.0;
        double avgLoss = 0.0;

        for (int i = gains.size() - period; i < gains.size(); i++) {
            avgGain += gains.get(i);
            avgLoss += losses.get(i);
        }

        avgGain /= period;
        avgLoss /= period;

        if (avgLoss == 0) {
            return 100.0;
        }

        double rs = avgGain / avgLoss;
        return 100.0 - (100.0 / (1.0 + rs));
    }

    /**
     * Calculates Simple Moving Average (SMA).
     */
    private double calculateSMA(List<Double> prices, int period) {
        if (prices.size() < period) {
            return prices.isEmpty() ? 0.0 : prices.get(prices.size() - 1);
        }
        double sum = 0.0;
        for (int i = prices.size() - period; i < prices.size(); i++) {
            sum += prices.get(i);
        }
        return sum / period;
    }

    /**
     * Calculates Momentum (price change over last N periods).
     */
    private double calculateMomentum(List<Double> prices, int period) {
        if (prices.size() < period + 1) {
            return 0.0;
        }
        double currentPrice = prices.get(prices.size() - 1);
        double pastPrice = prices.get(prices.size() - period - 1);
        return currentPrice - pastPrice;
    }

    /**
     * Converts MarketCandle to feature vector expected by the ONNX model.
     * Features: [RSI_14, SMA_Diff, Momentum]
     * @param candle The market candle
     * @return Feature array
     */
    private float[] extractFeatures(MarketCandle candle) {
        priceHistory.add(candle.getClose());
        
        // Keep sufficient history
        if (priceHistory.size() > Math.max(SMA_PERIOD, Math.max(MOMENTUM_PERIOD, RSI_PERIOD)) + 1) {
            priceHistory.remove(0);
        }

        // Calculate features
        double rsi = calculateRSI(priceHistory, RSI_PERIOD);
        double sma = calculateSMA(priceHistory, SMA_PERIOD);
        double smaDiff = candle.getClose() - sma;
        double momentum = calculateMomentum(priceHistory, MOMENTUM_PERIOD);

        return new float[]{
            (float) rsi,
            (float) smaDiff,
            (float) momentum
        };
    }

    @Override
    public Signal analyze(MarketCandle candle) {
        if (session == null) {
            logger.warning("ONNX model not loaded, returning HOLD");
            return Signal.HOLD;
        }

        try {
            // Extract features
            float[] features = extractFeatures(candle);
            
            // Create input tensor
            // Expected shape: [1, 3] (batch_size=1, features=3)
            long[] shape = {1, 3};
            FloatBuffer buffer = FloatBuffer.wrap(features);
            OnnxTensor inputTensor = OnnxTensor.createTensor(environment, buffer, shape);

            // Prepare inputs
            Map<String, OnnxTensor> inputs = new HashMap<>();
            // Get input name from model (usually the first input)
            String inputName = session.getInputNames().iterator().next();
            inputs.put(inputName, inputTensor);

            // Run inference
            OrtSession.Result output = session.run(inputs);

            // Get prediction - handle different output types
            OnnxTensor outputTensor = (OnnxTensor) output.get(0);
            Object value = outputTensor.getValue();
            
            float prediction = 0.0f;
            boolean isClassIndex = false;
            
            // Handle different output formats
            if (value instanceof long[]) {
                // RandomForestClassifier often outputs class indices as long[]
                long[] predictions = (long[]) value;
                prediction = predictions[0]; // 0 = SELL, 1 = BUY
                isClassIndex = true;
                String msg = String.format("AI Model output (class index): %d", (long)prediction);
                logger.info(msg);
                System.out.println(msg);
            } else if (value instanceof float[]) {
                // Probability or score as float[]
                float[] predictions = (float[]) value;
                prediction = predictions[0];
                String msg = String.format("AI Model output (probability): %.4f", prediction);
                logger.info(msg);
                System.out.println(msg);
            } else if (value instanceof float[][]) {
                // 2D array
                float[][] predictions = (float[][]) value;
                prediction = predictions[0][0];
                String msg = String.format("AI Model output (2D float): %.4f", prediction);
                logger.info(msg);
                System.out.println(msg);
            } else if (value instanceof long[][]) {
                // 2D long array
                long[][] predictions = (long[][]) value;
                prediction = predictions[0][0];
                isClassIndex = true;
                String msg = String.format("AI Model output (2D class index): %d", (long)prediction);
                logger.info(msg);
                System.out.println(msg);
            } else {
                String msg = "Unexpected output type: " + value.getClass().getName();
                logger.warning(msg);
                System.out.println(msg);
                // Try to get first element as number
                if (value instanceof Object[]) {
                    Object[] arr = (Object[]) value;
                    if (arr.length > 0 && arr[0] instanceof Number) {
                        prediction = ((Number) arr[0]).floatValue();
                    }
                }
            }
            
            // Clean up
            inputTensor.close();
            outputTensor.close();
            output.close();

            // Map output to signal
            // Model is returning class indices (0 or 1), so we need to handle it differently
            Signal signal;
            
            if (isClassIndex) {
                // Direct class prediction: 0 = SELL, 1 = BUY
                // Since model always returns 0 (SELL), let's add some randomness or use RSI as fallback
                // For now, if we have no coins and model says SELL, return HOLD instead
                if (prediction < 0.5 && wallet.getCoinBalance() == 0) {
                    // Can't sell if we have no coins - return HOLD
                    signal = Signal.HOLD;
                } else {
                    signal = (prediction >= 0.5) ? Signal.BUY : Signal.SELL;
                }
            } else {
                // Probability/score - use confidence threshold
                final double CONFIDENCE_THRESHOLD = 0.3;
                double distanceFromNeutral = Math.abs(prediction - 0.5);
                
                if (distanceFromNeutral < CONFIDENCE_THRESHOLD) {
                    signal = Signal.HOLD;
                } else if (prediction >= 0.5) {
                    signal = Signal.BUY;
                } else {
                    // If SELL but no coins, return HOLD
                    if (wallet.getCoinBalance() == 0) {
                        signal = Signal.HOLD;
                    } else {
                        signal = Signal.SELL;
                    }
                }
            }
            
            String decisionMsg = String.format("AI Model decision: %s (prediction: %.4f, confidence: %.2f%%)", 
                    signal, prediction, Math.abs(prediction - 0.5) * 200);
            logger.info(decisionMsg);
            System.out.println(decisionMsg);
            return signal;

        } catch (Exception e) {
            logger.severe("Error during AI inference: " + e.getMessage());
            e.printStackTrace();
            return Signal.HOLD;
        }
    }

    /**
     * Closes the ONNX session and releases resources.
     */
    public void close() {
        try {
            if (session != null) {
                session.close();
            }
            if (environment != null) {
                environment.close();
            }
        } catch (Exception e) {
            logger.warning("Error closing ONNX resources: " + e.getMessage());
        }
    }
}

