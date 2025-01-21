package com.mycompany.qrcode.controllers;

import com.google.zxing.WriterException;
import com.mycompany.qrcode.services.QrCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QrCodeController {

    @Autowired
    private QrCodeService qrCodeService;

    @Value("app.url")
    private String appUrl;

    @GetMapping("/qr/{id}")
    public void getQrCode(@PathVariable("id") int id, HttpServletResponse response) throws IOException, WriterException, Exception {
        try {
            String qrUrl = appUrl + "issue?id=" + id;
            BufferedImage qrImage = qrCodeService.generateQrCode(qrUrl);
            response.setContentType("image/png");
            ImageIO.write(qrImage, "PNG", response.getOutputStream());

        } catch (WriterException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el código QR", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al enviar la imagen del QR", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado", e);
        }
    }
}
