# 🚀 AI-Powered Trading Bot

A complete trading system built with **Java Design Patterns** and **Python Machine Learning**. The Java system uses ONNX Runtime to load and execute Python-trained ML models.

## 📋 Project Overview

This project demonstrates:
- **Observer Pattern**: Event-driven market data distribution
- **Strategy Pattern**: Interchangeable trading algorithms
- **Template Method Pattern**: Consistent trading lifecycle
- **ONNX Runtime**: Cross-platform ML model inference

## 🛠️ Technology Stack

### Java (Core Architecture)
- **Java 17+**
- **Maven** (Build Tool)
- **ONNX Runtime** (AI Model Inference)
- **JUnit 5** (Testing)
- **Lombok** (Optional, for cleaner code)

### Python (AI/ML)
- **Python 3.9+**
- **scikit-learn** (Machine Learning)
- **pandas** (Data Processing)
- **skl2onnx** (Model Conversion)
- **onnxruntime** (Model Validation)

## 📁 Project Structure

```
AI-Trading/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── tradingbot/
│   │               ├── domain/          # Domain entities
│   │               ├── observer/        # Observer Pattern
│   │               ├── strategy/        # Strategy & Template Method Patterns
│   │               ├── bot/             # Bot implementation
│   │               └── TradingBotMain.java
│   └── test/
│       └── java/
│           └── com/
│               └── tradingbot/
├── ai_model/
│   ├── train_model.py       # Python training script
│   ├── requirements.txt    # Python dependencies
│   └── trading_model.onnx   # Generated model (after training)
├── pom.xml                  # Maven configuration
└── README.md
```

## 🚀 Getting Started

### Prerequisites

1. **Java 17+** installed
2. **Maven** installed
3. **Python 3.9+** installed
4. **pip** (Python package manager)

### Step 1: Install Python Dependencies

```bash
cd ai_model
pip install -r requirements.txt
```

### Step 2: Train the AI Model

```bash
python train_model.py
```

This will:
- Generate 1000 rows of synthetic market data
- Calculate features (RSI_14, SMA_Diff, Momentum)
- Train a RandomForestClassifier
- Export the model to `trading_model.onnx`
- Print the expected input shape for Java

**Expected Output:**
```
EXPECTED INPUT SHAPE FOR JAVA:
Input Shape: [batch_size, 3]
  - batch_size: 1 (for single prediction)
  - features: 3 (RSI_14, SMA_Diff, Momentum)
```

### Step 3: Build the Java Project

```bash
mvn clean compile
```

### Step 4: Run the Trading Bot

```bash
mvn exec:java "-Dexec.mainClass=com.tradingbot.TradingBotMain"
```

Or if you prefer to run the JAR:

```bash
mvn package
java -cp target/ai-trading-bot-1.0.0.jar com.tradingbot.TradingBotMain
```

## 🎮 Usage

When you run the application:

1. **Select Strategy:**
   - `1` for RSI Strategy (Math-based)
   - `2` for AI Strategy (ML-based)

2. **If AI Strategy:**
   - Enter path to ONNX model (or press Enter for default: `ai_model/trading_model.onnx`)

3. **Watch the Trading:**
   - Market data is generated every 1 second
   - The bot analyzes each candle and executes trades
   - Wallet state is logged after each trade

4. **Stop the Bot:**
   - Press Enter to stop and see final wallet statistics

## 🏗️ Design Patterns Explained

### 1. Observer Pattern (`com.tradingbot.observer`)

**Purpose:** Decouple market data source from trading strategies.

- **Subject**: `MarketDataSubject` - generates and broadcasts market candles
- **Observer**: `BotObserver` - receives market updates and processes them
- **Benefit:** Event-driven architecture, no polling loops

### 2. Strategy Pattern (`com.tradingbot.strategy`)

**Purpose:** Allow runtime switching between trading algorithms.

- **Strategy Interface**: `TradingStrategy` - defines `analyze()` method
- **Concrete Strategies**: `RSIStrategy`, `AIStrategy`
- **Context**: `BotObserver` - uses strategy without knowing concrete types
- **Benefit:** Easy to add new strategies without modifying existing code

### 3. Template Method Pattern (`com.tradingbot.strategy.BaseTradingStrategy`)

**Purpose:** Define consistent trading lifecycle while allowing customization.

- **Template Method**: `executeStrategyTemplate()` - **FINAL** method
- **Steps:**
  1. Validate Data (Concrete)
  2. Analyze (Abstract - subclasses implement)
  3. Risk Check & Execute (Concrete)
  4. Logging (Concrete)
- **Benefit:** Ensures all strategies follow the same execution flow

## 📊 Domain Model

### MarketCandle
Represents OHLCV (Open, High, Low, Close, Volume) data with timestamp.

### Signal
Enum: `BUY`, `SELL`, `HOLD`

### Wallet
Manages virtual portfolio:
- USDT balance
- Coin balance
- PnL calculations

## 🧪 Testing

Run tests with:

```bash
mvn test
```

## 📝 Notes

- The market data is **simulated** (not real-time)
- Trading uses **virtual funds** (no real money)
- The AI model expects features in this order: `[RSI_14, SMA_Diff, Momentum]`
- Model input shape: `[1, 3]` (batch_size=1, features=3)

## 🎓 Academic Context

This project was built for a **Design Patterns** course, demonstrating:
- Strict adherence to pattern definitions
- Comprehensive JavaDoc documentation
- Pattern naming conventions
- Separation of concerns

## 📄 License

This is an academic project for educational purposes.

---

**Built with ❤️ using Design Patterns**

