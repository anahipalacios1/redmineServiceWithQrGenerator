<%@ page import="java.util.List" %>
<%@ page import="com.mycompany.qrcode.beans.Issue" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Listado de Issues - Municipalidad de Asunción</title>
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
        .btn-generate-qr, .btn-export-pdf {
            color: #fff;
            background-color: #007bff;
            border: none;
            padding: 8px 12px;
            border-radius: 4px;
        }
        .btn-export-pdf {
            background-color: #28a745; /* Color verde para el botón de exportar */
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>Listado de Issues - Municipalidad de Asunción</h1>
    </div>

    <div class="container">
        <%
            List<Issue> issues = (List<Issue>) request.getAttribute("issues");
            String error = (String) request.getAttribute("error");
        %>

        <% if (error != null) { %>
            <div class="alert alert-danger"><%= error %></div>
        <% } else if (issues != null && !issues.isEmpty()) { %>
            <p class="text-right"><strong>Total de Issues:</strong> <%= issues.size() %></p>

            <% for (Issue issue : issues) { %>
                <div class="issue-card">
                    <h4><strong>ID:</strong> <%= issue.getId() %> - <%= issue.getSubject() %></h4>
                    <p><strong>Descripción:</strong> <%= issue.getDescription() != null ? issue.getDescription() : "Sin descripción" %></p>
                    <p><strong>Creado el:</strong> <%= issue.getStartDate() %></p>
                    <p><strong>Autor:</strong> <%= issue.getAuthor() != null ? issue.getAuthor().getName() : "Sin autor" %></p>
                    <p><strong>Proyecto:</strong> <%= issue.getProject() != null ? issue.getProject().getName() : "Sin proyecto" %></p>
                    <p><strong>Tracker:</strong> <%= issue.getTracker() != null ? issue.getTracker().getName() : "Sin tracker" %></p>
                    <a href="/qr/<%= issue.getId() %>" class="btn btn-generate-qr">Generar QR</a>
                    
                    <!-- Botón de exportar a PDF para cada Issue -->
                    <a href="/front/pdf/<%= issue.getId() %>" class="btn btn-export-pdf">Exportar carnet Frontal</a>
                    <a href="/back/pdf/<%= issue.getId() %>" class="btn btn-export-pdf">Exportar carnet Dorsal</a>
                </div>
            <% } %>
        <% } else { %>
            <div class="alert alert-info">No se encontraron issues de Redmine.</div>
        <% } %>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>
