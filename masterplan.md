# 🚀 Project Context: AI-Powered Trading Bot (Design Patterns Term Project)

**Role:** Act as a **Lead Software Architect** and **Data Scientist**.
**Goal:** Build a complete trading system that uses **Java** for the core architecture (strictly using Design Patterns) and **Python** for training a Machine Learning model. The Java system will load the Python-trained model via **ONNX Runtime**.

---

## 🛠️ Part 1: Technology Stack

* **Core Language:** Java 17+
* **Build Tool:** Maven
* **Key Libraries:**
    * `com.microsoft.onnxruntime` (for AI inference)
    * `org.junit.jupiter` (for Testing)
    * `org.projectlombok` (Lombok - optional, for cleaner code)
* **AI/ML Language:** Python 3.9+
* **AI Libraries:** `scikit-learn`, `pandas`, `skl2onnx`, `onnxruntime` (Python)

---

## 🏛️ Part 2: Java Architecture & Design Patterns (The Skeleton)

The Java application must be built around strict adherence to the following patterns:

### 1. Domain Layer (`com.tradingbot.domain`)
* **`MarketCandle`:** A class representing OHLCV (Open, High, Low, Close, Volume) data + Timestamp.
* **`Signal`:** Enum (`BUY`, `SELL`, `HOLD`).
* **`Wallet`:** Manages virtual portfolio (USDT & Coin balance). logic for PnL calculations.

### 2. Observer Pattern (`com.tradingbot.observer`)
* **Objective:** The system must be Event-Driven. No `while(true)` loops in the main logic.
* **`Subject` (Interface):** `registerObserver`, `removeObserver`, `notifyObservers`.
* **`Observer` (Interface):** `update(MarketCandle candle)`.
* **`MarketDataFeed` (Concrete Class):**
    * Implements `Subject`.
    * Runs on a separate **Thread**.
    * Simulates market data flow (generates a new candle every 1 second) and pushes it to observers.

### 3. Strategy & Template Method Patterns (`com.tradingbot.strategy`)
* **Objective:** Define the trading lifecycle and interchangeable logic.
* **`TradingStrategy` (Interface):** The common interface.
* **`BaseTradingStrategy` (Abstract Class):**
    * **Implements Template Method Pattern.**
    * Method: `public final void onMarketUpdate(MarketCandle candle)` -> **MUST BE FINAL**.
    * **Execution Flow (The Template):**
        1.  Validate Data (Concrete)
        2.  `Signal signal = analyze(candle)` (**Abstract** - Subclasses implement this)
        3.  Risk Check & Execution (Concrete - checks Wallet funds)
        4.  Logging (Concrete)
* **`RSIStrategy` (Concrete Class):** Simple math-based logic.
* **`AIStrategy` (Concrete Class - *The Advanced Part*):**
    * Loads the `.onnx` model.
    * Converts `MarketCandle` into the specific Tensor format expected by the model.
    * Runs inference and maps output (0/1) to `Signal`.

## 📏 Part 2.5: Design Pattern Strictness & Documentation (CRITICAL)

Since this is an academic project graded on Design Patterns (30%), the implementation must be explicit and strictly adhere to standard definitions.

**1. Naming Conventions:**
* **Must** use pattern names in class names.
    * `MarketDataFeed` -> `MarketDataSubject` (or implement `Subject`).
    * `TradingBot` -> `BotObserver` (or implement `Observer`).
    * `RSIStrategy` -> Keep as is (Must end with `Strategy`).
    * `onMarketUpdate` -> Rename to `executeStrategyTemplate` to emphasize it's a Template Method.

**2. JavaDoc Requirements (Documentation in Code):**
* Every class implementing a pattern MUST have a class-level JavaDoc explaining:
    * **Pattern Name:** Which pattern is used?
    * **Role:** Is this the Context? The Strategy? The Concrete Observer?
    * **Reason:** Why was this pattern chosen? (e.g., "Used Strategy to allow runtime switching between AI and Math-based logic").
    * *Example:*
        ```java
        /**
         * Represents the 'Concrete Strategy' in the Strategy Pattern.
         * Uses the Relative Strength Index (RSI) algorithm to determine buy/sell signals.
         */
        public class RSIStrategy extends BaseTradingStrategy { ... }
        ```

**3. Pattern-Specific Constraints:**
* **Template Method:** The skeleton method in `BaseTradingStrategy` MUST be declared `final` to prevent overriding. Use `protected abstract` for steps that must be customized.
* **Observer:** The `Subject` must maintain a `List<Observer>` and use a loop to notify them. Do not tightly couple the Bot to the Data Feed.
* **Strategy:** The `TradingBot` (Context) must rely ONLY on the `TradingStrategy` interface, never on concrete classes like `RSIStrategy`.

## 🐍 Part 3: Python AI Model Generation (The Brain)

We need a Python script to create the "Brain" for the `AIStrategy` above.

**Requirements for `ai_model/train_model.py`:**
1.  **Synthetic Data:** Generate 1000 rows of dummy market data using `pandas`.
2.  **Features (Inputs):** Calculate these specific indicators:
    * `RSI_14`: Relative Strength Index.
    * `SMA_Diff`: Difference between Price and Simple Moving Average.
    * `Momentum`: Price change over last 5 periods.
3.  **Labeling:** Logic: If (Next Close > Current Close * 1.01) then `1` (BUY) else `0` (SELL).
4.  **Training:** Train a `RandomForestClassifier`.
5.  **Export:** Convert the model to **ONNX** format (`trading_model.onnx`) using `skl2onnx`.
    * *Crucial:* Ensure input type is `FloatTensorType`.
    * *Crucial:* Print the expected Input Shape at the end (so we can match it in Java).

---

 **Do not generate everything at once. Wait for my confirmation after each step.**



---