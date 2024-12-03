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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
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

    public byte[] exportReportFront(Issue issue) throws JRException, FileNotFoundException {
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
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Java Developer");
        parameters.put("CUSTOM_FIELDS_DATASOURCE", customFieldsDataSource);
        parameters.put("SUBREPORT_PATH", "C:/Users/Usuario/JaspersoftWorkspace/MyReports/");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, issueDataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
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
