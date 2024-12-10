package com.mycompany.qrcode.util;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import com.mycompany.qrcode.beans.Issue;
import com.mycompany.qrcode.response.IssuesResponse;

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
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class IssuesReportGenerator {

    public byte[] exportToPdf(IssuesResponse response) throws JRException, FileNotFoundException {
        return JasperExportManager.exportReportToPdf(getReport(response));
    }

    private JasperPrint getReport(IssuesResponse response) throws FileNotFoundException, JRException {
        List<Issue> issuesList = response.getIssues();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("fiscalizadoresData", new JRBeanCollectionDataSource(issuesList));
        JasperPrint report = JasperFillManager.fillReport(JasperCompileManager.compileReport(
                ResourceUtils.getFile("classpath:employees.jrxml")
                        .getAbsolutePath()), params, new JREmptyDataSource());
        return report;
    }
}