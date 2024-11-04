<%@page import="java.util.List"%>
<%@ page import="com.mycompany.qrcode.beans.Issue" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<h1>Issues de Redmine</h1>

<%
    List<Issue> issues = (List<Issue>) request.getAttribute("issues");
    String error = (String) request.getAttribute("error");
%>

<% if (error != null) { %>
    <div class="alert alert-danger"><%= error %></div>
<% } else if (issues != null && !issues.isEmpty()) { %>
    <strong>Cantidad de Issues: </strong> <%= issues.size() %> <br />
    <% for (Issue issue : issues) { %>
        <div>
            <strong>Id:</strong> <%= issue.getId() %><br />
            <strong>Asunto:</strong> <%= issue.getSubject() %><br />
            <strong>Descripción:</strong> <%= issue.getDescription() != null ? issue.getDescription() : "Sin descripción" %><br />
            <strong>Creado el:</strong> <%= issue.getStartDate() %><br />
            <strong>Autor:</strong> <%= issue.getAuthor() != null ? issue.getAuthor().getName() : "Sin autor" %><br />
            <strong>Proyecto:</strong> <%= issue.getProject() != null ? issue.getProject().getName() : "Sin proyecto" %><br />
            <strong>Tracker:</strong> <%= issue.getTracker() != null ? issue.getTracker().getName() : "Sin tracker" %><br />
<!--            <strong>Fields:</strong> <%= issue.getCustomFields() != null ? issue.getCustomFields().get(0).getName() : "Sin tracker" %><br />-->
            <a href="/qr/<%= issue.getId() %>" class="card-link">Generar QR</a>
        </div>
        <hr />
    <% } %>
<% } else { %>
    <div>No se encontraron issues de Redmine.</div>
<% } %>
