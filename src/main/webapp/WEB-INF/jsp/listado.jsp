<%@page import="java.util.List"%>
<%@ page import="com.mycompany.qrcode.beans.Issue" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<h1>Issues de Redmine</h1>

<%
    List<Issue> issues = (List<Issue>) request.getAttribute("issues");
    String error = (String) request.getAttribute("error");
%>

<% if (error != null) {%>
<div class="alert alert-danger"><%= error%></div>
<% } else if (issues != null && !issues.isEmpty()) {%>
<strong>Cantidad de Issues: </strong> <%= issues.size()%> <br />
<% for (Issue issue : issues) {%>
<div>
    <strong>Asunto:</strong> <%= issue.getSubject()%><br />
    <strong>Descripción:</strong> <%= issue.getDescription()!= null ? issue.getDescription(): "Sin descripción"%><br />
    <strong>Creado el:</strong> <%= issue.getStartDate()%><br />
</div>
<hr />
<% } %>
<% } else { %>
<div>No se encontraron issues de Redmine.</div>
<% }%>
