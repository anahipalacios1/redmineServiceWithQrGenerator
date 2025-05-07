<%@page import="java.util.List"%>
<%@page import="com.mycompany.qrcode.beans.Issue" %>
<%@page import="com.mycompany.qrcode.beans.CustomField" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Detalles del Fiscal - Municipalidad de Asunción</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
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
                width: 150px;
                height: 150px;
                object-fit: cover;
                border-radius: 8px;
                margin-top: 10px;
            }
        </style>
    </head>
    <body>

        <div class="header">
            <h1>Detalles del Fiscal - Municipalidad de Asunción</h1>
        </div>

        <div class="container-fluid px-3">
            <%
                Issue issue = (Issue) request.getAttribute("issue");
                String error = (String) request.getAttribute("error");
                String fotoUrl = (String) request.getAttribute("fotoUrl");
            %>

            <% if (error != null) { %>
            <div class="alert alert-danger"><%= error %></div>
            <% } else if (issue != null) { %>
            <div class="issue-details">
                <h4 class="text-center">Información del Fiscal</h4>

                <div class="text-center">
                    <img src="<%= (fotoUrl != null && !fotoUrl.isEmpty()) ? fotoUrl : "https://via.placeholder.com/150?text=No+Foto" %>"
                         alt="Fotografía del Fiscal" class="foto-fiscal img-fluid">
                </div>

                <div class="custom-fields mt-3">
                    <% if (issue.getCustomFields() != null) {
                            for (CustomField field : issue.getCustomFields()) {
                                if (!"Logo de Dpto.".equals(field.getName()) && !"Fotografia".equals(field.getName())) {
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
