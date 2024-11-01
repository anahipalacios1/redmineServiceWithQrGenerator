package com.mycompany.qrcode.controllers;
import com.google.zxing.WriterException;
import com.mycompany.qrcode.beans.CustomField;
import com.mycompany.qrcode.beans.Issue;
import com.mycompany.qrcode.response.IssuesResponse;
import com.mycompany.qrcode.services.QrCodeService;
import com.mycompany.qrcode.services.RedmineService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QrCodeController {

    @Autowired
    private RedmineService redmineService;

    @Autowired
    private QrCodeService qrCodeService;

    @GetMapping("/qr/{id}")
    public void getQrCode(@PathVariable int id, HttpServletResponse response) throws IOException, WriterException, Exception {
        // Obtener la respuesta de issues desde el servicio de Redmine
        IssuesResponse issuesResponse = redmineService.getIssues();
        
        // Filtrar el issue por el ID solicitado
        Issue issue = issuesResponse.getIssues().stream()
            .filter(i -> i.getId() == id)
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found"));

        // Construir el contenido del QR con los campos personalizados del issue
        StringBuilder qrData = new StringBuilder();
        for (CustomField field : issue.getCustom_fields()) {
            qrData.append(field.getName()).append(": ").append(field.getValue()).append("\n");
        }

        // Generar la imagen del QR con los datos
        BufferedImage qrImage = qrCodeService.generateQrCode(qrData.toString());

        // Configurar el tipo de contenido y enviar la imagen en la respuesta
        response.setContentType("image/png");
        ImageIO.write(qrImage, "PNG", response.getOutputStream());
    }
}
