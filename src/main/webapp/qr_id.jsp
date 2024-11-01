<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Datos del Fiscal</title>
    <link rel="stylesheet" 
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css" 
          integrity="sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2" 
          crossorigin="anonymous">
</head>
<body>
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Datos del Fiscal</h5>
            </div>
            <div class="modal-body">
                <c:choose>
                    <c:when test="${not empty issue}">
                        <div class="form-group">
                            <label>Nombre y Apellido</label>
                            <p class="form-control-static">
                                ${issue.customFields['Nombre']} ${issue.customFields['Apellido']}
                            </p>
                        </div>
                        <div class="form-group">
                            <label>Cargo</label>
                            <p class="form-control-static">
                                ${issue.customFields['Cargo']}
                            </p>
                        </div>
                        <div class="form-group">
                            <label>Código Personal</label>
                            <p class="form-control-static">
                                ${issue.customFields['Codigo de Personal']}
                            </p>
                        </div>
                        <div class="form-group">
                            <label>Correo</label>
                            <p class="form-control-static">
                                ${issue.customFields['Correo']}
                            </p>
                        </div>
                        <div class="form-group">
                            <label>Teléfono</label>
                            <p class="form-control-static">
                                ${issue.customFields['Teléfono']}
                            </p>
                        </div>

                        <!-- Formulario para agregar un comentario -->
                        <form action="guardarComentario.jsp" method="POST">
                            <div class="form-group">
                                <label>Comentario</label>
                                <textarea class="form-control" name="mensaje" rows="3"></textarea>
                            </div>
                            <!-- Campo oculto para el ID del issue -->
                            <input type="hidden" name="id" value="${issue.id}">
                            <div class="modal-footer">
                                <a href="index.jsp" class="btn btn-danger">Regresar</a>
                                <!-- Botón para enviar el comentario -->
                                <button type="submit" class="btn btn-primary" name="enviar">Guardar Comentario</button>
                            </div>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <p>Issue no encontrado.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</body>
</html>