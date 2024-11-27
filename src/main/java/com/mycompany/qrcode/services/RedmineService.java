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
import com.mycompany.qrcode.beans.Issue;
import com.mycompany.qrcode.util.IssuesReportGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public byte[] exportReport(Issue issue) throws JRException, FileNotFoundException {
        // Crear una lista con el único objeto Issue recibido
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        // Cargar el archivo JRXML y compilarlo
        File file = ResourceUtils.getFile("classpath:employees.jrxml"); // Cambia al nombre correcto de tu JRXML
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        // Crear un DataSource a partir de la lista
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(issueList);

        // Configurar parámetros adicionales para el reporte
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Java Developer"); // Puedes agregar más parámetros si es necesario

        // Llenar el reporte con los datos
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Exportar el reporte a PDF en memoria
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

}
