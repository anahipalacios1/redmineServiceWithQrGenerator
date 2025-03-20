package com.mycompany.qrcode.util;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import com.mycompany.qrcode.beans.Issue;
import com.mycompany.qrcode.response.IssuesResponse;
import java.awt.Font;
import java.io.File;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class IssuesReportGenerator {

    public byte[] exportToPdf(IssuesResponse response) throws JRException, FileNotFoundException {
        return JasperExportManager.exportReportToPdf(getReport(response));
    }

    private JasperPrint getReport(IssuesResponse response) throws FileNotFoundException, JRException {
        List<Issue> issuesList = response.getIssues();
        Map<String, Object> params = new HashMap<>();
        params.put("fiscalizadoresData", new JRBeanCollectionDataSource(issuesList));

        // Cargar el archivo .jrxml y compilarlo
        File reportFile = ResourceUtils.getFile("classpath:employees.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(reportFile.getAbsolutePath());

        // Registrar la fuente manualmente
        Font font = new Font("Arial", Font.PLAIN, 12);  // Reemplaza con la fuente que deseas usar
        params.put("REPORT_FONT", font);

        JasperPrint report = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
        return report;
    }

}
