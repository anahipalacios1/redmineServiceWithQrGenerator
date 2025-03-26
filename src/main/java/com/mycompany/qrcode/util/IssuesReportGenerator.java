package com.mycompany.qrcode.util;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import com.mycompany.qrcode.beans.Issue;
import com.mycompany.qrcode.response.IssuesResponse;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;

import java.io.FileNotFoundException;
import java.util.Arrays;
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

        File reportFile = ResourceUtils.getFile("classpath:employees.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(reportFile.getAbsolutePath());

        // 🔍 Verificar si Gotham está en la lista de fuentes disponibles en Java
        String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        System.out.println("🔍 Fuentes disponibles en el sistema: " + Arrays.toString(availableFonts));

        if (Arrays.asList(availableFonts).contains("Gotham")) {
            System.out.println("✅ La fuente Gotham está disponible.");
        } else {
            System.out.println("❌ La fuente Gotham NO está disponible.");
        }

        params.put("REPORT_FONT", "Gotham");

        JasperPrint report = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
        return report;
    }

}
