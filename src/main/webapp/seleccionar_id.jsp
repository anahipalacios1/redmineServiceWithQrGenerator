<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Datos del Fiscal</title>
</head>
<body>
    <div>
        <c:choose>
            <c:when test="${not empty issue}">
                <h3>Datos del Fiscal</h3>
                <p><strong>ID:</strong> ${issue.id}</p>
                <p><strong>Nombre:</strong> ${issue.customFields['Nombre']}</p>
                <p><strong>Apellido:</strong> ${issue.customFields['Apellido']}</p>
                <p><strong>Cargo:</strong> ${issue.customFields['Cargo']}</p>
                <p><strong>Código Personal:</strong> ${issue.customFields['Codigo de Personal']}</p>
                <p><strong>Correo:</strong> ${issue.customFields['Correo']}</p>
                <p><strong>Teléfono:</strong> ${issue.customFields['Teléfono']}</p>

                <h3>Código QR generado:</h3>
                <img src="${qrImagePath}" alt="Código QR" />
            </c:when>
            <c:otherwise>
                <p>${error}</p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>