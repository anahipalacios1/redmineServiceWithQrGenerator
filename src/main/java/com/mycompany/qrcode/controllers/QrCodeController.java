package com.mycompany.qrcode.controllers;

import com.google.zxing.WriterException;
import com.mycompany.qrcode.beans.CustomField;
import com.mycompany.qrcode.beans.Issue;
import com.mycompany.qrcode.response.IssuesResponse;
import com.mycompany.qrcode.services.QrCodeService;
import com.mycompany.qrcode.services.RedmineService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import javax.imageio.ImageIO;
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
    public void getQrCode(@PathVariable("id") int id, HttpServletResponse response) throws IOException, WriterException, Exception {
        try {
            // Obtener la respuesta de issues desde el servicio de Redmine
            IssuesResponse issuesResponse = redmineService.getIssues();

            if (issuesResponse == null || issuesResponse.getIssues() == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudieron obtener los issues de Redmine");
            }

            // Filtrar el issue por el ID solicitado
            Issue issue = issuesResponse.getIssues().stream()
                    .filter(i -> i.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found"));

            // Generar la imagen del QR con los datos
            BufferedImage qrImage = qrCodeService.generateQrCode(buildQrData(issue));

            // Configurar el tipo de contenido y enviar la imagen en la respuesta
            response.setContentType("image/png");
            ImageIO.write(qrImage, "PNG", response.getOutputStream());
            response.getOutputStream().flush();

        } catch (WriterException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el código QR", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al enviar la imagen del QR", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado", e);
        }
    }

    private String buildQrData(Issue issue) {
        StringBuilder qrData = new StringBuilder();
        qrData.append("Id: ").append(issue.getId()).append("\n");
        qrData.append("Asunto: ").append(issue.getSubject()).append("\n");
        qrData.append("Descripción: ").append(issue.getDescription() != null ? issue.getDescription() : "Sin descripción").append("\n");
        qrData.append("Creado el: ").append(issue.getStartDate()).append("\n");
        qrData.append("Autor: ").append(issue.getAuthor() != null ? issue.getAuthor().getName() : "Sin autor").append("\n");
        qrData.append("Proyecto: ").append(issue.getProject() != null ? issue.getProject().getName() : "Sin proyecto").append("\n");
        qrData.append("Tracker: ").append(issue.getTracker() != null ? issue.getTracker().getName() : "Sin tracker").append("\n");
        for (CustomField customField : issue.getCustomFields()) {
            qrData.append(customField.getName()).append(": ").append(customField.getValue()).append("\n");
        }
        return qrData.toString();
    }
}
