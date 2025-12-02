# Maven Kurulumu (Windows)

## Yöntem 1: Chocolatey ile (En Kolay)

Eğer Chocolatey yüklüyse:
```powershell
choco install maven
```

## Yöntem 2: Manuel Kurulum

1. **Maven İndir:**
   - https://maven.apache.org/download.cgi adresinden `apache-maven-3.9.x-bin.zip` indir

2. **Çıkart:**
   - `C:\Program Files\Apache\maven` klasörüne çıkart

3. **Environment Variables Ekle:**
   - `MAVEN_HOME` = `C:\Program Files\Apache\maven`
   - `PATH`'e ekle: `%MAVEN_HOME%\bin`

4. **Kontrol Et:**
   ```powershell
   mvn -version
   ```

## Yöntem 3: IDE Kullan (Önerilen)

### IntelliJ IDEA:
- Projeyi aç
- `pom.xml`'e sağ tık → "Add as Maven Project"
- `TradingBotMain.java`'ya sağ tık → "Run 'TradingBotMain.main()'"

### VS Code:
- Java Extension Pack yükle
- `TradingBotMain.java`'yı aç
- Üstteki "Run" butonuna tıkla

### Eclipse:
- File → Import → Maven → Existing Maven Projects
- `TradingBotMain.java`'ya sağ tık → Run As → Java Application

