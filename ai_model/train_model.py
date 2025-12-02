"""
AI Trading Model Training Script
Generates synthetic market data, trains a RandomForestClassifier, and exports to ONNX format.
"""

import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report
import skl2onnx
from skl2onnx.common.data_types import FloatTensorType
from skl2onnx import convert_sklearn
import os

def generate_synthetic_market_data(n_rows=1000):
    """
    Generates synthetic OHLCV market data.
    
    Args:
        n_rows: Number of rows to generate
        
    Returns:
        DataFrame with columns: open, high, low, close, volume
    """
    np.random.seed(42)
    
    # Start with a base price
    base_price = 50000.0
    prices = [base_price]
    
    # Generate price series with random walk
    for i in range(n_rows - 1):
        # Random price change: -2% to +2%
        change = np.random.uniform(-0.02, 0.02)
        new_price = prices[-1] * (1 + change)
        prices.append(new_price)
    
    # Generate OHLCV data
    data = []
    for i in range(n_rows):
        close = prices[i]
        open_price = prices[i-1] if i > 0 else close
        
        # Generate high and low with some randomness
        volatility = np.random.uniform(0.005, 0.015)
        high = max(open_price, close) * (1 + volatility)
        low = min(open_price, close) * (1 - volatility)
        
        # Generate volume
        volume = np.random.uniform(1000, 10000)
        
        data.append({
            'open': open_price,
            'high': high,
            'low': low,
            'close': close,
            'volume': volume
        })
    
    return pd.DataFrame(data)

def calculate_rsi(prices, period=14):
    """
    Calculates Relative Strength Index (RSI).
    
    Args:
        prices: Series of closing prices
        period: RSI period (default 14)
        
    Returns:
        Series of RSI values
    """
    delta = prices.diff()
    gain = (delta.where(delta > 0, 0)).rolling(window=period).mean()
    loss = (-delta.where(delta < 0, 0)).rolling(window=period).mean()
    
    rs = gain / loss
    rsi = 100 - (100 / (1 + rs))
    
    # Fill NaN values with 50 (neutral RSI)
    rsi = rsi.fillna(50.0)
    
    return rsi

def calculate_sma(prices, period=20):
    """
    Calculates Simple Moving Average (SMA).
    
    Args:
        prices: Series of closing prices
        period: SMA period (default 20)
        
    Returns:
        Series of SMA values
    """
    return prices.rolling(window=period).mean().bfill().fillna(prices.iloc[0])

def calculate_momentum(prices, period=5):
    """
    Calculates Momentum (price change over last N periods).
    
    Args:
        prices: Series of closing prices
        period: Momentum period (default 5)
        
    Returns:
        Series of momentum values
    """
    return prices.diff(period).fillna(0.0)

def create_features(df):
    """
    Creates feature columns: RSI_14, SMA_Diff, Momentum.
    
    Args:
        df: DataFrame with OHLCV data
        
    Returns:
        DataFrame with feature columns added
    """
    df = df.copy()
    
    # Calculate RSI_14
    df['RSI_14'] = calculate_rsi(df['close'], period=14)
    
    # Calculate SMA and SMA_Diff
    df['SMA_20'] = calculate_sma(df['close'], period=20)
    df['SMA_Diff'] = df['close'] - df['SMA_20']
    
    # Calculate Momentum (5 periods)
    df['Momentum'] = calculate_momentum(df['close'], period=5)
    
    return df

def create_labels(df):
    """
    Creates labels based on future price movement.
    Logic: If (Next Close > Current Close * 1.005) then 1 (BUY) else 0 (SELL)
    Changed threshold from 1.01 to 1.005 for more balanced labels
    
    Args:
        df: DataFrame with close prices
        
    Returns:
        Series of labels (0 or 1)
    """
    labels = []
    
    for i in range(len(df)):
        if i == len(df) - 1:
            # Last row: use previous label or default to 0
            labels.append(0)
        else:
            current_close = df.iloc[i]['close']
            next_close = df.iloc[i + 1]['close']
            
            # Lower threshold (0.5% instead of 1%) for more BUY signals
            if next_close > current_close * 1.005:
                labels.append(1)  # BUY
            else:
                labels.append(0)   # SELL
    
    return pd.Series(labels, name='label')

def main():
    print("=" * 60)
    print("AI Trading Model Training")
    print("=" * 60)
    
    # Step 1: Generate synthetic data
    print("\n[1/5] Generating synthetic market data...")
    df = generate_synthetic_market_data(n_rows=1000)
    print(f"Generated {len(df)} rows of market data")
    
    # Step 2: Create features
    print("\n[2/5] Creating features (RSI_14, SMA_Diff, Momentum)...")
    df = create_features(df)
    print("Features created:")
    print(f"  - RSI_14: {df['RSI_14'].notna().sum()} valid values")
    print(f"  - SMA_Diff: {df['SMA_Diff'].notna().sum()} valid values")
    print(f"  - Momentum: {df['Momentum'].notna().sum()} valid values")
    
    # Step 3: Create labels
    print("\n[3/5] Creating labels...")
    labels = create_labels(df)
    print(f"Label distribution:")
    print(f"  - BUY (1): {labels.sum()} samples")
    print(f"  - SELL (0): {(labels == 0).sum()} samples")
    
    # Step 4: Prepare data for training
    print("\n[4/5] Preparing data for training...")
    feature_cols = ['RSI_14', 'SMA_Diff', 'Momentum']
    X = df[feature_cols].values
    y = labels.values
    
    # Remove rows with NaN values
    valid_mask = ~np.isnan(X).any(axis=1)
    X = X[valid_mask]
    y = y[valid_mask]
    
    print(f"Final dataset: {len(X)} samples, {X.shape[1]} features")
    
    # Split data
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )
    print(f"Training set: {len(X_train)} samples")
    print(f"Test set: {len(X_test)} samples")
    
    # Step 5: Train RandomForestClassifier
    print("\n[5/5] Training RandomForestClassifier...")
    model = RandomForestClassifier(
        n_estimators=100,
        max_depth=10,
        random_state=42,
        n_jobs=-1
    )
    model.fit(X_train, y_train)
    
    # Evaluate model
    y_pred = model.predict(X_test)
    accuracy = accuracy_score(y_test, y_pred)
    print(f"\nModel Accuracy: {accuracy:.4f}")
    print("\nClassification Report:")
    print(classification_report(y_test, y_pred, target_names=['SELL', 'BUY']))
    
    # Step 6: Export to ONNX
    print("\n" + "=" * 60)
    print("Exporting model to ONNX format...")
    print("=" * 60)
    
    # Define input type: FloatTensorType with shape [batch_size, 3]
    # batch_size is None (variable), 3 features: RSI_14, SMA_Diff, Momentum
    initial_type = [('float_input', FloatTensorType([None, 3]))]
    
    # Convert to ONNX
    onnx_model = convert_sklearn(model, initial_types=initial_type)
    
    # Save model
    output_path = 'trading_model.onnx'
    with open(output_path, 'wb') as f:
        f.write(onnx_model.SerializeToString())
    
    print(f"\n✓ Model exported to: {output_path}")
    print(f"  File size: {os.path.getsize(output_path) / 1024:.2f} KB")
    
    # Print expected input shape
    print("\n" + "=" * 60)
    print("EXPECTED INPUT SHAPE FOR JAVA:")
    print("=" * 60)
    print("Input Shape: [batch_size, 3]")
    print("  - batch_size: 1 (for single prediction)")
    print("  - features: 3 (RSI_14, SMA_Diff, Momentum)")
    print("\nFeature Order:")
    print("  1. RSI_14 (float)")
    print("  2. SMA_Diff (float)")
    print("  3. Momentum (float)")
    print("\nOutput Shape: [batch_size, 1]")
    print("  - 0 = SELL signal")
    print("  - 1 = BUY signal")
    print("=" * 60)
    
    print("\n✓ Training complete!")

if __name__ == '__main__':
    main()

