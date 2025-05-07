package com.mycompany.qrcode.controllers;

import com.mycompany.qrcode.beans.CustomField;
import com.mycompany.qrcode.response.IssuesResponse;
import com.mycompany.qrcode.services.RedmineService;
import com.mycompany.qrcode.beans.Issue;
import com.mycompany.qrcode.config.RedmineConfig;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class IssueController {

    private final RedmineService redmineService;
    private final RedmineConfig redmineConfig;

    @Value("${app.url}")
    private String appUrl;

    @Autowired
    public IssueController(RedmineService redmineService, RedmineConfig redmineConfig) {
        this.redmineService = redmineService;
        this.redmineConfig = redmineConfig;
    }

    @GetMapping("/issue")
    public String getIssueById(@RequestParam("id") int id, Model model) {
        try {
            IssuesResponse issuesResponse = redmineService.getIssues();
            Issue issue = issuesResponse.getIssues().stream()
                    .filter(i -> i.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found"));

            String fotoUrlFinal = null;

            if (issue.getCustomFields() != null) {
                for (CustomField field : issue.getCustomFields()) {
                    System.out.println("Campo: " + field.getName() + ", Valor: " + field.getValue());
                    if ("fotografia".equalsIgnoreCase(field.getName().trim()) && field.getValue() != null) {
                        String fotoId = field.getValue().toString().trim();
                        System.out.println("fotoId encontrado: " + fotoId);

                        // Acepta solo números, que son IDs de adjuntos en Redmine
                        if (fotoId.matches("\\d+")) {
                            String imagesFolder = "/opt/images";
                            File folder = new File(imagesFolder);

                            // Crea la carpeta si no existe
                            if (!folder.exists()) {
                                folder.mkdirs();
                            }

                            // Ruta local donde guardar la imagen
                            String localPath = imagesFolder + "/" + fotoId + ".jpg";
                            File imageFile = new File(localPath);

                            if (!imageFile.exists()) {
                                String redmineFotoUrl = redmineConfig.getUrlQr() + "/attachments/download/" + fotoId + "?key=" + redmineConfig.getKey();

                                try (InputStream in = new URL(redmineFotoUrl).openStream()) {
                                    Files.copy(in, imageFile.toPath());
                                    System.out.println("Imagen descargada exitosamente: " + localPath);
                                } catch (Exception e) {
                                    System.err.println("Error descargando imagen: " + e.getMessage());
                                }
                            } else {
                                System.out.println("Imagen ya existe en: " + localPath);
                            }

                            // Esta URL debe ser accesible (configurada en StaticResourceConfig)
                            fotoUrlFinal = "/images/" + fotoId + ".jpg";
                        } else {
                            System.out.println("Valor del campo Fotografía no es numérico: " + fotoId);
                        }
                    }
                }
            }

            System.out.println("fotoUrlFinal: " + fotoUrlFinal);

            String issueUrl = appUrl + "/issue?id=" + id;
            model.addAttribute("issueUrl", issueUrl);
            model.addAttribute("issue", issue);
            model.addAttribute("fotoUrl", fotoUrlFinal);

            return "seleccionar_id";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al obtener los datos del issue: " + e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/front/pdf/{id}")
    public ResponseEntity<byte[]> exportPdfForIssueFront(@PathVariable("id") int id) {
        try {
            IssuesResponse issuesResponse = redmineService.getIssues();

            Issue issue = issuesResponse.getIssues().stream()
                    .filter(i -> i.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found"));

            byte[] pdfBytes = redmineService.exportCombinedReport(issue);

            if (pdfBytes == null || pdfBytes.length == 0) {
                throw new RuntimeException("El PDF generado está vacío.");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("issue_" + id + "_report.pdf")
                    .build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/back/pdf/{id}")
    public ResponseEntity<byte[]> exportPdfForIssueBack(@PathVariable("id") int id) {
        try {
            IssuesResponse issuesResponse = redmineService.getIssues();
            Issue issue = issuesResponse.getIssues().stream()
                    .filter(i -> i.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found"));
            byte[] pdfBytes = redmineService.exportReportBack(issue);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("issue_" + id + "_report.pdf")
                    .build());
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
