# 🚀 Cursor'da Çalıştırma Rehberi

## Yöntem 1: Cursor'un Run Butonu (ÖNERİLEN) ⭐

1. **Dosyayı Aç:**
   - `src/main/java/com/tradingbot/TradingBotMain.java` dosyasını aç

2. **Run Butonuna Tıkla:**
   - Dosyanın üstünde veya `main` metodunun yanında ▶️ **Run** butonuna tıkla
   - Veya `F5` tuşuna bas
   - Veya sağ tık → **Run Java**

3. **Cursor Otomatik Yapar:**
   - Bağımlılıkları indirir (ilk seferde)
   - Projeyi derler
   - Çalıştırır

## Yöntem 2: Terminalden Maven Wrapper ile

Cursor'un terminalini aç (`Ctrl + `` veya View → Terminal`):

```powershell
# İlk seferde wrapper'ı indirir
.\mvnw.cmd clean compile

# Uygulamayı çalıştır
.\mvnw.cmd exec:java -Dexec.mainClass="com.tradingbot.TradingBotMain"
```

## Yöntem 3: Cursor'un Terminalinden (Maven Yüklüyse)

```powershell
mvn clean compile
mvn exec:java -Dexec.mainClass="com.tradingbot.TradingBotMain"
```

## İlk Çalıştırmada

Cursor otomatik olarak:
- ✅ Maven bağımlılıklarını indirir (ONNX Runtime, JUnit, vb.)
- ✅ Projeyi derler
- ✅ Çalıştırır

**Not:** İlk seferde bağımlılıklar indirilirken biraz zaman alabilir (1-2 dakika).

## Sorun Giderme

### "Java Extension Pack" Hatası:
- Cursor'da Extensions'a git (`Ctrl+Shift+X`)
- "Java Extension Pack" ara ve yükle

### "Maven not found" Hatası:
- Maven Wrapper kullan (Yöntem 2)
- Veya Maven yükle (MAVEN_KURULUM.md'ye bak)

### "ONNX model not found" Hatası:
- Önce Python model'i eğit: `python ai_model/train_model.py`

## Hızlı Test

```powershell
.\mvnw.cmd test
```

