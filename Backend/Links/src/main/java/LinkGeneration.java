import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Instant;

public class LinkGeneration {
    // Твой будущий домен на Render
    private static final String BASE_URL = "https://your-app.onrender.com/access";

    public static void main(String[] args) {
        try {
            // 1. Устанавливаем время жизни: текущее время + 5 минут (300 секунд)
            long expireTimestamp = Instant.now().getEpochSecond() + 300;

            // 2. Формируем ссылку с параметром exp (expiration)
            String finalUrl = BASE_URL + "?target=youtube&exp=" + expireTimestamp;

            System.out.println("Сгенерирована ссылка: " + finalUrl);

            // 3. Генерируем QR-код из этой ссылки
            generateQRCodeImage(finalUrl, 350, 350, "./access_qr.png");

            System.out.println("QR-код сохранен в файл: access_qr.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateQRCodeImage(String text, int width, int height, String filePath) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
}
