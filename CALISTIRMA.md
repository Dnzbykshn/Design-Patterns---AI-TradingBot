# 🚀 Terminal'de Çalıştırma Rehberi

## Windows PowerShell / CMD

### Adım 1: Projeyi Derle
```powershell
mvn clean compile
```

### Adım 2: Uygulamayı Çalıştır
```powershell
mvn exec:java -Dexec.mainClass="com.tradingbot.TradingBotMain"
```

## Alternatif: JAR Oluşturup Çalıştır

### Adım 1: JAR Dosyası Oluştur
```powershell
mvn clean package
```

### Adım 2: JAR'ı Çalıştır
```powershell
java -cp target/ai-trading-bot-1.0.0.jar com.tradingbot.TradingBotMain
```

## Hızlı Başlatma (run.bat)

Windows'ta çift tıkla veya terminalden:
```powershell
.\run.bat
```

## Kullanım

1. **Strategy Seç:**
   - `1` → RSI Strategy (Matematik tabanlı)
   - `2` → AI Strategy (Makine öğrenmesi)

2. **AI Strategy için:**
   - Model dosyası yolu gir (veya Enter'a bas, varsayılan: `ai_model/trading_model.onnx`)

3. **Trading Başlar:**
   - Her 1 saniyede yeni market data gelir
   - Bot analiz yapar ve trade yapar
   - Wallet durumu loglanır

4. **Durdur:**
   - Enter'a bas

## Test Çalıştırma

```powershell
mvn test
```

