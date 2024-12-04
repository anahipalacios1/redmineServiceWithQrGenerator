package com.mycompany.qrcode.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.qrcode.config.RedmineConfig;
import com.mycompany.qrcode.response.IssuesResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mycompany.qrcode.beans.CustomField;
import com.mycompany.qrcode.beans.Issue;
import com.mycompany.qrcode.util.IssuesReportGenerator;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;

@Service
public class RedmineService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RedmineConfig redmineConfig;
    private final IssuesReportGenerator issuesReportGenerator;

    public RedmineService(RestTemplate restTemplate, ObjectMapper objectMapper, RedmineConfig redmineConfig,
            IssuesReportGenerator issuesReportGenerator) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.redmineConfig = redmineConfig;
        this.issuesReportGenerator = issuesReportGenerator;

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public IssuesResponse getIssues() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Redmine-API-Key", redmineConfig.getKey());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = redmineConfig.getUrl() + "/issues.json";

        try {
            ResponseEntity<IssuesResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, IssuesResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new Exception("Error en la solicitud a Redmine: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            throw new Exception("Error en el servidor de Redmine: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        }
    }

    public byte[] exportReportFront(Issue issue) throws JRException, IOException {
        File file = ResourceUtils.getFile("classpath:employees.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);
        JRBeanCollectionDataSource issueDataSource = new JRBeanCollectionDataSource(issueList);
        List<CustomField> customFields = issue.getCustomFields();
        List<CustomField> filteredCustomFields = customFields.stream()
                .filter(cf -> cf.getName().equals("Nombre")
                || cf.getName().equals("Apellido")
                || cf.getName().equals("Cargo")
                || cf.getName().equals("Sector")
                || cf.getName().equals("Codigo de Personal"))
                .collect(Collectors.toList());

        JRBeanCollectionDataSource customFieldsDataSource = new JRBeanCollectionDataSource(filteredCustomFields);
        Object fotoId = customFields.stream()
                .filter(cf -> cf.getName().equals("Fotografía"))
                .map(CustomField::getValue)
                .findFirst()
                .orElse(null);
        byte[] fotografia = null;
        if (fotoId != null) {
            fotografia = obtenerImagenDesdeRedmine(fotoId);
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Java Developer");
        parameters.put("CUSTOM_FIELDS_DATASOURCE", customFieldsDataSource);
        parameters.put("SUBREPORT_PATH", "C:/Users/Usuario/JaspersoftWorkspace/MyReports/");
        parameters.put("PHOTO", fotografia);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, issueDataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public byte[] obtenerImagenDesdeRedmine(Object fotoId) throws IOException {
        String url = "https://redmine4.sudolabs.com.py/attachments/download/" + fotoId;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Redmine-API-Key", redmineConfig.getKey());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            // Realizar la solicitud GET
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    byte[].class
            );

            // Verificar el tipo de contenido
            String contentType = response.getHeaders().getContentType().toString();
            System.out.println("Response Headers: " + response.getHeaders());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                byte[] imageBytes = response.getBody();

                // Verificar si el tipo de contenido es una imagen
                if (!contentType.startsWith("image/")) {
                    String errorHtml = new String(response.getBody(), StandardCharsets.UTF_8);
                    throw new IOException("La respuesta no es una imagen. Content-Type: " + contentType + ". Respuesta HTML: " + errorHtml);
                }

                // Verificar si la imagen es válida
                if (isValidImage(imageBytes)) {
                    return imageBytes;
                } else {
                    throw new IOException("La imagen descargada no es válida.");
                }
            } else {
                // Capturar el cuerpo HTML si la respuesta no es válida
                String errorHtml = new String(response.getBody(), StandardCharsets.UTF_8);
                throw new IOException("No se pudo descargar la imagen desde Redmine. Código de estado: " + response.getStatusCode() + ". Respuesta HTML: " + errorHtml);
            }
        } catch (Exception e) {
            throw new IOException("Error al obtener la imagen desde Redmine: " + e.getMessage(), e);
        }
    }

// Método para verificar si los bytes corresponden a una imagen válida
    private boolean isValidImage(byte[] imageBytes) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            return bufferedImage != null; // Si no es null, es una imagen válida
        } catch (IOException e) {
            return false; // Si ocurre un error, no es una imagen válida
        }
    }

    public byte[] exportReportBack(Issue issue) throws JRException, FileNotFoundException {
        // Cargar el archivo JRXML
        File file = ResourceUtils.getFile("classpath:employeesBack.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        // Crear una lista con el issue
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);
        JRBeanCollectionDataSource issueDataSource = new JRBeanCollectionDataSource(issueList);

        // Filtrar campos personalizados relevantes
        List<CustomField> customFields = issue.getCustomFields();
        List<CustomField> filteredCustomFields = customFields.stream()
                .filter(cf -> cf.getName().equals("Nombre")
                || cf.getName().equals("Apellido")
                || cf.getName().equals("Cargo")
                || cf.getName().equals("Codigo de Personal")
                || cf.getName().equals("Correo")
                || cf.getName().equals("Telefono"))
                .collect(Collectors.toList());
        JRBeanCollectionDataSource customFieldsDataSource = new JRBeanCollectionDataSource(filteredCustomFields);

        // Generar la URL dinámica para el QR
        String qrCodeUrl = "http://localhost:8080/issue?id=" + issue.getId();

        // Configurar los parámetros del reporte
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Java Developer");
        parameters.put("CUSTOM_FIELDS_DATASOURCE", customFieldsDataSource);
        parameters.put("SUBREPORT_PATH", "C:/Users/Usuario/JaspersoftWorkspace/MyReports/");
        parameters.put("QR_CODE_DATA", qrCodeUrl); // Añadir la URL dinámica

        // Generar el reporte
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, issueDataSource);

        // Exportar a PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
