<%-- 
    Document   : respuesta
    Created on : 8/11/2025, 11:17:05 PM
    Author     : linkl
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="style.css">
        <title>JSP Page</title>
    </head>
    <body>
        <h1> Resultado de la Operación </h1>
    
    <hr>

    <%
        // Obtener el mensaje
        String mensaje = (String) request.getAttribute("respuesta");
        if (mensaje != null) {
            out.println(mensaje);
        } else {
            out.println("No se recibió ninguna respuesta del servidor.");
        }
        
        // Obtener el tipo de usuario de la sesión
        String tipo = (String) session.getAttribute("tipo");
        String menuUrl = "index.html"; // Por defecto
        
        if (tipo != null) {
            if (tipo.equals("Alumno") || tipo.equals("Profesor")) {
                menuUrl = "Usuarios.html";
            } else if (tipo.equals("Bibliotecario")) {
                menuUrl = "Bibliotecario.html";
            } else if (tipo.equals("Admin")) {
                menuUrl = "Administrador.html";
            }
        }
    %>

    <br><br>
    
    <a href="<%= menuUrl %>">Volver al Menú Principal</a>
    </body>
</html>
