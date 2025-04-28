<%@page import="java.util.List"%>
<%@page import="com.mycompany.qrcode.beans.Issue" %>
<%@page import="com.mycompany.qrcode.beans.CustomField" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Detalles del Fiscal - Municipalidad de Asunción</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
        <style>
            body {
                background-color: #f8f9fa;
                font-family: Arial, sans-serif;
            }
            .header {
                background-color: #004085;
                color: white;
                padding: 10px 0;
                text-align: center;
                margin-bottom: 20px;
            }
            .issue-details {
                border: 1px solid #ced4da;
                border-radius: 8px;
                padding: 20px;
                background-color: white;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            }
            .custom-fields {
                margin-top: 15px;
            }
            .foto-fiscal {
                max-width: 200px;
                height: auto;
                border-radius: 8px;
                margin-top: 10px;
            }
        </style>
    </head>
    <body>

        <div class="header">
            <h1>Detalles del Fiscal - Municipalidad de Asunción</h1>
        </div>

        <div class="container">
            <%
                Issue issue = (Issue) request.getAttribute("issue");
                String error = (String) request.getAttribute("error");
                String urlQr = (String) request.getAttribute("urlQr");
                String key = (String) request.getAttribute("key");
                String fotoUrl = null;

                if (issue != null && issue.getCustomFields() != null) {
                    for (CustomField field : issue.getCustomFields()) {
                        if ("Fotografia".equals(field.getName()) && field.getValue() != null) {
                            String fotoId = field.getValue().toString().trim();
                            fotoUrl = fotoId.matches("\\d+") ? urlQr + "/attachments/download/" + fotoId + "?key=" + key : fotoId;
                        }
                    }
                }
            %>

            <% if (error != null) { %>
                <div class="alert alert-danger"><%= error %></div>
            <% } else if (issue != null) { %>
                <div class="issue-details">
                    <h4>Información del Fiscal</h4>
                    <% if (fotoUrl != null && !fotoUrl.isEmpty()) { %>
                        <div class="text-center">
                            <img src="<%= fotoUrl %>" alt="Fotografía del Fiscal" class="foto-fiscal">
                        </div>
                    <% } else { %>
                        <div class="text-center">
                            <img src="https://via.placeholder.com/150?text=No+Foto" alt="No disponible" class="foto-fiscal">
                        </div>
                    <% } %>

                    <div class="custom-fields mt-3">
                        <% if (issue.getCustomFields() != null) {
                            for (CustomField field : issue.getCustomFields()) {
                                if (!"Logo de Dpto.".equals(field.getName())) { // Salteamos "Logo de Dpto."
                        %>
                            <p><strong><%= field.getName() %>:</strong> <%= field.getValue() != null ? field.getValue() : "Sin valor" %></p>
                        <%      }
                               }
                           } else { %>
                            <p>No hay campos personalizados disponibles.</p>
                        <% } %>
                    </div>
                </div>
            <% } else { %>
                <div class="alert alert-info">No se encontraron datos del fiscal.</div>
            <% } %>
        </div>

        <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    </body>
</html>
