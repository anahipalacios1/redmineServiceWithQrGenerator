<%@page import="java.net.URLEncoder"%>
<%@ page import="java.util.List" %>
<%@ page import="com.mycompany.qrcode.beans.Issue" %>
<%@ page import="com.mycompany.qrcode.beans.CustomField" %>
<%@ page import="com.mycompany.qrcode.config.RedmineConfig" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Listado de Fiscales - Municipalidad de Asunción</title>
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
            .issue-card {
                border: 1px solid #ced4da;
                border-radius: 8px;
                padding: 15px;
                margin-bottom: 15px;
                background-color: white;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            }
            .btn-export-pdf {
                color: #fff;
                background-color: #28a745;
                padding: 8px 12px;
                border-radius: 4px;
                text-decoration: none;
                display: inline-block;
            }
            .custom-fields {
                margin-top: 15px;
            }
            .fiscal-photo {
                width: 150px;
                height: 150px;
                border-radius: 8px;
                border: 1px solid #ccc;
                object-fit: cover;
                display: block;
                margin: 10px 0;
            }
        </style>
    </head>
    <body>
        <div class="header">
            <h1>Listado Detallado de Fiscales - Municipalidad de Asunción</h1>
        </div>
        <div class="container">
            <input type="text" id="searchBox" class="form-control mb-3" placeholder="Buscar por nombre o apellido..." onkeyup="filterFiscales()">
            <%
                List<Issue> issues = (List<Issue>) request.getAttribute("issues");
                String error = (String) request.getAttribute("error");
                if (error != null) {
            %>
            <div class="alert alert-danger"><%= error%></div>
            <% } else if (issues != null && !issues.isEmpty()) {
                String urlQr = (String) request.getAttribute("urlQr");
                String key = (String) request.getAttribute("key");
            %>
            <p class="text-right"><strong>Total de Issues:</strong> <%= issues.size()%></p>
            <%
                for (Issue issue : issues) {
                    String fotoUrl = null;
                    String nombre = null;
                    String apellido = null;

                    if (issue.getCustomFields() != null) {
                        for (CustomField field : issue.getCustomFields()) {
                            if ("Fotografía".trim().equals(field.getName().trim()) && field.getValue() != null) {
                                String fotoId = String.valueOf(field.getValue()).trim();

                                for (CustomField field2 : issue.getCustomFields()) {
                                    if ("Nombre".equals(field2.getName())) {
                                        nombre = String.valueOf(field2.getValue()).trim();
                                    } else if ("Apellido".equals(field2.getName())) {
                                        apellido = String.valueOf(field2.getValue()).trim();
                                    }
                                }

                                if (fotoId.matches("\\d+")) {
                                    fotoUrl = urlQr + "/attachments/download/" + fotoId + "?key=" + key;
                                } else {
                                    fotoUrl = fotoId; // Si no es un número, asumimos que ya es una URL válida
                                }

                            }
                        }
                    }
            %>
            <div class="issue-card" 
                 data-nombre="<%
                     String cedula = "";
                     if (issue.getCustomFields() != null) {
                         for (CustomField field : issue.getCustomFields()) {
                             if ("Cedula".equals(field.getName())) {
                                 cedula = field.getValue().toString() != null ? field.getValue().toString() : "";
                             }
                         }
                     }
                     out.print((nombre != null ? nombre.toLowerCase() : "") + " "
                             + (apellido != null ? apellido.toLowerCase() : "") + " "
                             + cedula);
                 %>">

                <h4><strong>ID:</strong> <%= issue.getId()%> - <%= issue.getSubject()%></h4>
                <p>URL de la foto: <%= fotoUrl%></p>
                <% if (fotoUrl != null && !fotoUrl.isEmpty()) {%>
                <img alt="Fotografía del Fiscal" class="fiscal-photo" src=<%= fotoUrl%>>
                <% } else { %>
                <img src="https://via.placeholder.com/150?text=No+Foto" alt="No disponible" class="fiscal-photo">
                <% } %>

                <div class="custom-fields">
                    <% if (issue.getCustomFields() != null) {
                            for (CustomField field : issue.getCustomFields()) {
                                if ("Nombre".equals(field.getName())
                                        || "Apellido".equals(field.getName())
                                        || "Cédula".equals(field.getName())
                                        || "Cargo".equals(field.getName())
                                        || "Sector".equals(field.getName())
                                        || "Departamento".equals(field.getName())
                                        || "Unidad".equals(field.getName())) {
                    %>
                    <p><strong><%= field.getName()%>:</strong> <%= field.getValue() != null ? field.getValue() : "Sin valor"%></p>
                    <%
                            }
                        }
                    } else {
                    %>
                    <p>No hay información disponible del fiscal.</p>
                    <% }%>
                </div>
                <a href="/front/pdf/<%= issue.getId()%>" class="btn btn-export-pdf">Exportar carnet</a>
            </div>
            <% } %>
            <% } else { %>
            <div class="alert alert-info">No se encontraron issues de Redmine.</div>
            <% }%>
        </div>
        <script>
            function filterFiscales() {
                let input = document.getElementById("searchBox").value.toLowerCase();
                let fiscales = document.querySelectorAll(".issue-card");

                fiscales.forEach(fiscal => {
                    let nombreCompleto = fiscal.getAttribute("data-nombre") || ""; // Evita errores si el atributo está vacío
                    if (nombreCompleto.includes(input)) {
                        fiscal.style.display = "block";
                    } else {
                        fiscal.style.display = "none";
                    }
                });
            }
        </script>
        <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    </body>
</html>
