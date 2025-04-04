package com.mycompany.qrcode.config;

import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
//import net.sf.jasperreports.engine.util.JRPropertiesUtil;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;

public class JasperConfig {
    public static void configureJasperReports() {
        JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
        JRPropertiesUtil jrPropertiesUtil = JRPropertiesUtil.getInstance(jasperReportsContext);
        jrPropertiesUtil.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
//        jrPropertiesUtil.setProperty("net.sf.jasperreports.default.font.name", defaultPDFFont);
        System.out.println("JasperReportsContext configurado correctamente.");
    }
}
