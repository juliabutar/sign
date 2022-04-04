package go.pajak.pbb.app.registrasi.service;

import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.qrcode.EncodeHintType;
import com.itextpdf.text.pdf.qrcode.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrCodeService {

    public Image generateQRCodeImage(String text) throws Exception {
//QRcode generator logic
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 250, 250);
//        return MatrixToImageWriter.toBufferedImage(bitMatrix);

        Map<EncodeHintType, Object> qrParam = new HashMap<>();
        qrParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        qrParam.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BarcodeQRCode qrcode = new BarcodeQRCode(text, 33, 33, qrParam);
        return qrcode.createAwtImage(Color.BLACK, Color.WHITE);

    }
}
