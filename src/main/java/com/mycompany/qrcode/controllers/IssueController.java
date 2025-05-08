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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
            Map<String, String> tempFields = new HashMap<>();

            if (issue.getCustomFields() != null) {
                for (CustomField field : issue.getCustomFields()) {
                    String fieldName = field.getName().trim();
                    String fieldValue = field.getValue() != null ? field.getValue().toString().trim() : "";

                    System.out.println("Campo: " + fieldName + ", Valor: " + fieldValue);

                    tempFields.put(fieldName, fieldValue);

                    if ("fotografia".equalsIgnoreCase(fieldName) && !fieldValue.isEmpty()) {
                        String fotoId = fieldValue;

                        if (fotoId.matches("\\d+")) {
                            String imagesFolder = "/opt/images";
                            File folder = new File(imagesFolder);
                            if (!folder.exists()) {
                                folder.mkdirs();
                            }

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

                            fotoUrlFinal = "/images/" + fotoId + ".jpg";
                        } else {
                            System.out.println("Valor del campo Fotografía no es numérico: " + fotoId);
                        }
                    }
                }
            }

            Map<String, String> camposOrdenados = new LinkedHashMap<>();
            camposOrdenados.put("Fiscalizador activo", "Sí");
            camposOrdenados.put("Fotografía", fotoUrlFinal);
            camposOrdenados.put("Cedula", tempFields.getOrDefault("Cedula", "Sin valor"));
            camposOrdenados.put("Nombre", tempFields.getOrDefault("Nombre", "Sin valor"));
            camposOrdenados.put("Apellido", tempFields.getOrDefault("Apellido", "Sin valor"));
            camposOrdenados.put("Cargo", tempFields.getOrDefault("Cargo", "Sin valor"));
            camposOrdenados.put("Dirección Institucional", tempFields.getOrDefault("Dpto. Institucional", "Sin valor")); // <- corrección de nombre
            camposOrdenados.put("Departamento", tempFields.getOrDefault("Departamento", "Sin valor"));
            camposOrdenados.put("Unidad", tempFields.getOrDefault("Unidad", "Sin valor"));
            camposOrdenados.put("Telefono de Contacto", tempFields.getOrDefault("Telefono", "Sin valor"));

            model.addAttribute("campos", camposOrdenados);
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
