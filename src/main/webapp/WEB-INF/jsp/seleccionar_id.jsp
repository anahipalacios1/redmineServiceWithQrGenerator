<%@page import="java.util.Map" %>
<%@page import="com.mycompany.qrcode.beans.Issue" %>
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
            margin-bottom: 10px;
        }
        .fiscalizador-activo {
            background-color: #343a40;
            color: white;
            padding: 6px 12px;
            border-radius: 5px;
            display: inline-block;
            margin-bottom: 15px;
        }
        .espacio-seccion {
            margin-top: 20px;
            border-top: 1px solid #dee2e6;
        }
        .foto-container {
            margin-top: 10px;
            margin-bottom: 20px;
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
        Map<String, String> campos = (Map<String, String>) request.getAttribute("campos");
    %>

    <% if (error != null) { %>
        <div class="alert alert-danger"><%= error %></div>
    <% } else if (issue != null) { %>
        <div class="issue-details">
            <h4 class="text-center">Información del Fiscal</h4>

            <div class="custom-fields mt-3">
                <% 
                    String fotoUrl = null;
                    if (campos != null) {
                        // Primero, mostrar fiscalizador activo si existe
                        if (campos.containsKey("Fiscalizador activo")) {
                %>
                    <div class="fiscalizador-activo">
                        <strong>Fiscalizador activo:</strong> <%= campos.get("Fiscalizador activo") %>
                    </div>
                    <%-- Luego, mostrar la foto después del fiscalizador activo --%>
                    <% if (campos.containsKey("Fotografía") && !campos.get("Fotografía").isEmpty()) { %>
                        <div class="foto-container text-center">
                            <img src="<%= campos.get("Fotografía") %>" alt="Foto del fiscal" class="foto-fiscal img-fluid">
                        </div>
                    <% } else { %>
                        <div class="foto-container text-center">
                            <img src="https://via.placeholder.com/150?text=No+Foto" alt="Foto no disponible" class="foto-fiscal img-fluid">
                        </div>
                    <% } %>
                <% }

                    // Luego, recorrer y mostrar los campos comunes, omitiendo "Fiscalizador activo" y "Fotografía"
                    for (Map.Entry<String, String> entry : campos.entrySet()) {
                        String campo = entry.getKey();
                        String valor = entry.getValue();

                        if ("Fiscalizador activo".equalsIgnoreCase(campo) || "Fotografía".equalsIgnoreCase(campo)) {
                            continue; // Omitir estos campos
                        }

                        if ("Cargo".equalsIgnoreCase(campo)) {
                %>
                    <p><strong><%= campo %>:</strong> <%= valor %></p>
                    <div class="espacio-seccion"></div>
                <%  } else { %>
                    <p><strong><%= campo %>:</strong> <%= valor %></p>
                <%  }
                    }
                %>
                <% } else { %>
                <p>No hay datos disponibles.</p>
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
