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
                padding: 12px;
                margin-bottom: 12px;
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
            .fiscal-photo {
                width: 250px;
                height: 250px;
                border-radius: 8px;
                border: 1px solid #ccc;
                object-fit: cover;
                display: block;
                margin: auto;
            }
        </style>
    </head>
    <body>
        <div class="header">
            <h1>Listado Detallado de Fiscales - Municipalidad de Asunción</h1>
        </div>
        <div class="container">
            <input type="text" id="searchBox" class="form-control mb-3" placeholder="Buscar por nombre, apellido o cédula..." onkeyup="filterFiscales()">
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
                    if (issue.getCustomFields() != null) {
                        for (CustomField field : issue.getCustomFields()) {
                            if ("Fotografia".equals(field.getName()) && field.getValue() != null) {
                                String fotoId = field.getValue().toString().trim();
                                fotoUrl = fotoId.matches("\\d+") ? urlQr + "/attachments/download/" + fotoId + "?key=" + key : fotoId;
                            }
                        }
                    }
            %>
            <div class="issue-card" data-nombre="<%
                String nombre = "", apellido = "", cedula = "";
                if (issue.getCustomFields() != null) {
                    for (CustomField field : issue.getCustomFields()) {
                        if ("Nombre".equals(field.getName())) {
                            nombre = field.getValue() != null ? field.getValue().toString().toLowerCase() : "";
                        }
                        if ("Apellido".equals(field.getName())) {
                            apellido = field.getValue() != null ? field.getValue().toString().toLowerCase() : "";
                        }
                        if ("Cedula".equals(field.getName())) {
                            cedula = field.getValue() != null ? field.getValue().toString() : "";
                        }
                    }
                }
                out.print(nombre + " " + apellido + " " + cedula);
                 %>">
                <div class="row">
                    <div class="col-md-8">
                        <h4><strong>ID:</strong> <%= issue.getId()%> - <%= issue.getSubject()%></h4>
                        <div class="custom-fields">
                            <% if (issue.getCustomFields() != null) {
                                    for (CustomField field : issue.getCustomFields()) {
                                        if ("Nombre".equals(field.getName()) || "Apellido".equals(field.getName()) || "Cedula".equals(field.getName()) || "Cargo".equals(field.getName()) || "Dpto. Institucional".equals(field.getName()) || "Departamento".equals(field.getName()) || "Unidad".equals(field.getName())) {
                            %>
                            <p><strong><%= field.getName()%>:</strong> <%= field.getValue() != null ? field.getValue() : "Sin valor"%></p>
                            <% }
                            }
                        } else { %>
                            <p>No hay información disponible del fiscal.</p>
                            <% }%>
                        </div>
                        <a href="/front/pdf/<%= issue.getId()%>" class="btn btn-export-pdf">Exportar carnet</a>
                    </div>
                    <div class="col-md-4 d-flex align-items-center justify-content-center">
                        <% if (fotoUrl != null && !fotoUrl.isEmpty()) {%>
                        <img alt="Fotografía del Fiscal" class="fiscal-photo" src="<%= fotoUrl%>">
                        <% } else { %>
                        <img src="https://via.placeholder.com/150?text=No+Foto" alt="No disponible" class="fiscal-photo">
                        <% } %>
                    </div>
                </div>
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
                    let nombreCompleto = fiscal.getAttribute("data-nombre") || "";
                    fiscal.style.display = nombreCompleto.includes(input) ? "block" : "none";
                });
            }
        </script>
        <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
        <h6>version 1.0</h6>
    </body>
</html>