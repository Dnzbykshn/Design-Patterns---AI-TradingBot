# 🎤 AI Trading Bot - Sunum Scripti

**Süre:** 12-15 dakika  
**Hedef:** Design Patterns dersi final projesi sunumu

---

## 📋 SUNUM AKIŞI

### [0:00-0:30] GİRİŞ - Title Slide

**Ekran:** Title slide (PowerPoint/Canva)

**Söylenecekler:**
```
"Merhaba, ben [İsminiz]. Bugün Design Patterns dersi için geliştirdiğim 
'AI-Powered Trading Bot' projesini sunacağım.

Bu proje, Java Design Patterns ve Python Machine Learning'i birleştiren,
gerçek zamanlı trading kararları alabilen bir sistem. Projede 4 farklı 
design pattern kullanarak, genişletilebilir ve bakımı kolay bir mimari 
oluşturdum."
```

**Vurgu:** 4 pattern, Java + Python entegrasyonu

---

### [0:30-1:30] PROBLEM STATEMENT & SOLUTION OVERVIEW

**Ekran:** Mimari diyagram (draw.io veya PowerPoint)

**Söylenecekler:**
```
"Önce problemi tanımlayalım: Bir trading bot yapmak istiyoruz ama:
- Farklı trading stratejileri olmalı (RSI, AI, gelecekte MACD, Bollinger)
- Market data gerçek zamanlı gelmeli
- Stratejiler runtime'da değiştirilebilmeli
- Kod genişletilebilir olmalı

Çözümümüz: 4 Design Pattern kullanarak modüler bir mimari.

[Ekranda mimari diyagram göster]

Observer Pattern: Market data'yı event-driven şekilde dağıtıyor
Strategy Pattern: Farklı trading algoritmalarını değiştirilebilir yapıyor
Template Method: Tüm stratejilerin aynı lifecycle'ı takip etmesini sağlıyor
Factory Method: Strategy oluşturmayı merkezileştiriyor, Open/Closed Principle'a uyuyor"
```

**Gösterilecek:**
- Mimari diyagram
- Pattern'lerin birbiriyle ilişkisi

---

### [1:30-3:30] OBSERVER PATTERN (2 dakika)

**Ekran:** IDE'de Observer Pattern kodları

**Söylenecekler:**
```
"İlk pattern'imiz: Observer Pattern. Bu pattern, market data kaynağını 
trading stratejilerinden ayırıyor.

[IDE'de Subject.java aç]

Subject interface'imiz var. registerObserver, removeObserver, notifyObservers 
metodlarıyla observer'ları yönetiyor.

[MarketDataSubject.java aç]

MarketDataSubject, Subject'i implement ediyor. Ayrı bir thread'de çalışıyor 
ve her 1 saniyede yeni market candle üretiyor. notifyObservers metoduyla 
tüm observer'lara bildirim gönderiyor.

[BotObserver.java aç]

BotObserver, Observer interface'ini implement ediyor. update metoduyla 
market data'yı alıyor ve strategy'ye yönlendiriyor.

Bu sayede:
- Tight coupling yok
- Event-driven mimari
- while(true) loop yok
- Yeni observer eklemek kolay"
```

**Gösterilecek Kodlar:**
1. `Subject.java` - Interface
2. `MarketDataSubject.java` - notifyObservers metodu
3. `BotObserver.java` - update metodu

**Vurgu:** Event-driven, decoupling, thread safety

---

### [3:30-5:00] STRATEGY PATTERN (1.5 dakika)

**Ekran:** IDE'de Strategy Pattern kodları

**Söylenecekler:**
```
"İkinci pattern: Strategy Pattern. Bu pattern, farklı trading algoritmalarını 
runtime'da değiştirilebilir yapıyor.

[TradingStrategy.java aç]

TradingStrategy interface'imiz var. Sadece analyze metodu tanımlı.

[RSIStrategy.java aç]

RSIStrategy, TradingStrategy'yi implement ediyor. Matematiksel RSI algoritması 
kullanıyor.

[AIStrategy.java aç]

AIStrategy de aynı interface'i implement ediyor ama ONNX model kullanarak 
makine öğrenmesi ile karar veriyor.

[BotObserver.java'da strategy kullanımı göster]

BotObserver, concrete class'lara bağımlı değil. Sadece TradingStrategy 
interface'ini kullanıyor.

Bu sayede:
- Runtime'da strategy değiştirilebilir
- Yeni strategy eklemek kolay
- Client code değişmez"
```

**Gösterilecek Kodlar:**
1. `TradingStrategy.java` - Interface
2. `RSIStrategy.java` - analyze metodu
3. `AIStrategy.java` - analyze metodu
4. `BotObserver.java` - strategy kullanımı

**Vurgu:** Runtime switching, polymorphism, extensibility

---

### [5:00-6:30] TEMPLATE METHOD PATTERN (1.5 dakika)

**Ekran:** IDE'de Template Method kodları

**Söylenecekler:**
```
"Üçüncü pattern: Template Method Pattern. Bu pattern, tüm stratejilerin 
aynı execution flow'unu takip etmesini sağlıyor.

[BaseTradingStrategy.java aç]

BaseTradingStrategy abstract class'ımız var. executeStrategyTemplate metodu 
FINAL olarak tanımlı - bu template method.

[Template method'u göster]

Template method şu adımları takip ediyor:
1. Validate Data - Concrete implementation
2. Analyze - Abstract, subclasses implement ediyor
3. Risk Check & Execute - Concrete
4. Logging - Concrete

[RSIStrategy.java'da analyze override göster]

RSIStrategy, sadece analyze metodunu override ediyor. Diğer adımlar 
otomatik çalışıyor.

Bu sayede:
- Tüm stratejiler aynı lifecycle'ı takip ediyor
- Kod tekrarı yok
- Template method FINAL - override edilemez
- Sadece analyze adımı customize ediliyor"
```

**Gösterilecek Kodlar:**
1. `BaseTradingStrategy.java` - executeStrategyTemplate (FINAL)
2. Template method adımları
3. `RSIStrategy.java` - analyze override

**Vurgu:** FINAL method, consistent lifecycle, code reuse

---

### [6:30-8:30] FACTORY METHOD PATTERN (2 dakika)

**Ekran:** IDE'de Factory Method kodları

**Söylenecekler:**
```
"Dördüncü ve son pattern: Factory Method Pattern. Bu pattern, strategy 
oluşturmayı merkezileştiriyor ve Open/Closed Principle'a uyuyor.

[StrategyFactory.java aç]

StrategyFactory abstract class'ımız var. createStrategy abstract method'u 
factory method. createAndConfigureStrategy template method.

[RSIStrategyFactory.java aç]

RSIStrategyFactory, StrategyFactory'yi extend ediyor. createStrategy'de 
RSIStrategy oluşturuyor.

[AIStrategyFactory.java aç]

AIStrategyFactory de aynı şekilde AIStrategy oluşturuyor. Model path 
handling'i burada yapılıyor.

[TradingBotMain.java'da factory kullanımı göster]

Main class'ımızda artık if-else yok. Sadece Factory.getFactory() çağırıyoruz.

Bu sayede:
- Yeni strategy eklemek için sadece yeni Factory class ekliyoruz
- Main code değişmiyor - Open/Closed Principle
- Strategy creation logic merkezileşti
- Single Responsibility Principle"
```

**Gösterilecek Kodlar:**
1. `StrategyFactory.java` - Abstract factory
2. `RSIStrategyFactory.java` - Concrete factory
3. `AIStrategyFactory.java` - Concrete factory
4. `TradingBotMain.java` - Factory kullanımı (if-else yok)

**Vurgu:** Open/Closed Principle, centralized creation, extensibility

---

### [8:30-12:00] CANLI DEMO (3.5 dakika)

**Ekran:** Terminal + IDE

**Söylenecekler:**
```
"Şimdi botu canlı çalıştıralım ve pattern'lerin nasıl birlikte çalıştığını görelim.

[Terminal'de mvn exec:java çalıştır]

Bot başladı. Factory Method Pattern çalışıyor - strategy seçimi yapıyoruz.

[Strategy seçimi: 1 - RSI]

RSI Strategy seçildi. Factory RSIStrategy oluşturdu.

[Bot çalışıyor, loglar geliyor]

Observer Pattern çalışıyor - her saniye yeni market data geliyor ve 
BotObserver'a notify ediliyor.

Template Method Pattern çalışıyor - her candle için:
1. Validate
2. Analyze (RSI hesaplama)
3. Execute trade
4. Log

[Trading yapılıyor - BUY/SELL sinyalleri]

Strategy Pattern çalışıyor - RSI algoritması sinyal üretiyor.

[Wallet durumu değişiyor]

Trading başarılı! Wallet durumu güncelleniyor.

[AI Strategy'ye geç]

Şimdi AI Strategy'yi deneyelim. Factory Method yine çalışıyor.

[AI Strategy çalışıyor]

ONNX model yüklendi, AI inference yapıyor. Aynı Template Method kullanılıyor 
ama analyze adımı AI model kullanıyor.

[Sonuçları göster]

Her iki strategy de aynı interface'i kullanıyor, aynı lifecycle'ı takip ediyor 
ama farklı algoritmalar kullanıyor."
```

**Gösterilecek:**
1. Bot başlatma
2. Strategy seçimi (Factory Method)
3. Trading yapılıyor (Observer + Template Method)
4. Strategy değiştirme (Runtime switching)
5. Sonuçlar

**Vurgu:** Pattern'lerin birlikte çalışması, runtime flexibility

---

### [12:00-13:00] SONUÇ VE ÖĞRENİLENLER (1 dakika)

**Ekran:** Summary slide

**Söylenecekler:**
```
"Özetleyecek olursak:

✅ 4 Design Pattern başarıyla uygulandı:
   - Observer Pattern: Event-driven mimari
   - Strategy Pattern: Runtime strategy switching
   - Template Method Pattern: Consistent lifecycle
   - Factory Method Pattern: Open/Closed Principle

✅ Kod kalitesi:
   - Comprehensive JavaDoc documentation
   - Unit tests yazıldı
   - Clean code principles

✅ Genişletilebilirlik:
   - Yeni strategy eklemek kolay (sadece Factory ekle)
   - Yeni observer eklemek kolay
   - Pattern'ler birbiriyle uyumlu çalışıyor

✅ Teknoloji entegrasyonu:
   - Java + Python (ONNX Runtime)
   - Maven build system
   - Cross-platform

Proje GitHub'da: github.com/Dnzbykshn/Design-Patterns---AI-TradingBot

Teşekkürler, sorularınızı bekliyorum."
```

**Gösterilecek:**
- Summary slide
- GitHub repo linki
- Pattern listesi

---

## 🎬 EKRAN KAYDI İPUÇLARI

### Hazırlık:
1. **IDE Hazırlığı:**
   - Tüm dosyalar açık olsun
   - Syntax highlighting açık
   - Font size büyük (18-20pt)

2. **Terminal Hazırlığı:**
   - Terminal temiz olsun
   - Font size büyük
   - Dark theme (göz yormaz)

3. **Demo Hazırlığı:**
   - Bot önceden test edilmiş olsun
   - Model dosyası hazır olsun
   - Hızlı demo senaryosu hazırla

### Kayıt Sırası:
1. **Kod Gösterimi:** Yavaş scroll, önemli satırları işaretle
2. **Demo:** Akıcı olsun, hata olursa devam et
3. **Transition:** Slide'lar arası geçişler yumuşak olsun

---

## 📊 SUNUM SLIDE'LARI (PowerPoint/Canva)

### Slide 1: Title
- Proje Adı: AI-Powered Trading Bot
- İsim, Tarih
- Design Patterns Course

### Slide 2: Problem Statement
- Trading bot gereksinimleri
- Farklı stratejiler
- Genişletilebilirlik

### Slide 3: Solution Overview
- 4 Design Pattern
- Mimari diyagram
- Teknoloji stack

### Slide 4: Observer Pattern
- Pattern açıklaması
- Kod örneği (screenshot)
- Avantajlar

### Slide 5: Strategy Pattern
- Pattern açıklaması
- Kod örneği (screenshot)
- Avantajlar

### Slide 6: Template Method Pattern
- Pattern açıklaması
- Template method flow
- FINAL method vurgusu

### Slide 7: Factory Method Pattern
- Pattern açıklaması
- Factory hierarchy
- Open/Closed Principle

### Slide 8: Live Demo
- Demo video embed veya canlı

### Slide 9: Results & Conclusion
- Başarılar
- Öğrenilenler
- GitHub link

---

## ⏱️ ZAMAN YÖNETİMİ

| Bölüm | Süre | Kritik Noktalar |
|-------|------|----------------|
| Giriş | 0:30 | Kısa ve öz |
| Problem & Solution | 1:00 | Mimari net göster |
| Observer Pattern | 2:00 | Event-driven vurgula |
| Strategy Pattern | 1:30 | Runtime switching |
| Template Method | 1:30 | FINAL method |
| Factory Method | 2:00 | Open/Closed Principle |
| Demo | 3:30 | Akıcı olsun |
| Sonuç | 1:00 | Özet ve link |

**Toplam:** ~13 dakika

---

## 🎯 VURGU NOKTALARI

1. **Pattern'lerin Birbiriyle Uyumu:**
   - Observer → Strategy → Template Method → Factory Method
   - Her pattern bir öncekini destekliyor

2. **SOLID Principles:**
   - Open/Closed (Factory Method)
   - Single Responsibility (Her pattern ayrı sorumluluk)
   - Dependency Inversion (Interface'lere bağımlılık)

3. **Kod Kalitesi:**
   - JavaDoc documentation
   - Test coverage
   - Clean code

4. **Gerçek Dünya Uygulaması:**
   - Çalışan sistem
   - ML entegrasyonu
   - Production-ready kod

---

## 💡 SON ANLARDAKİ İPUÇLARI

1. **Ses:** Net ve yavaş konuş
2. **Pace:** Acele etme, her pattern'i tam anlat
3. **Demo:** Hata olursa "Bu normal bir durum, production'da error handling var" de
4. **Sorular:** Her pattern'in neden seçildiğini bil
5. **Confidence:** Kodunu iyi biliyorsun, güvenle sun

---

**Başarılar! 🚀**

