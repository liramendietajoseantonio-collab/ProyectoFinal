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
        <title>JSP Page</title>
    </head>
    <body>
        <h1> Resultado de la Operación </h1>
    
    <hr>

    <%
        // 1. Obtener el atributo "respuesta" que mandó el Controlador
        String mensaje = (String) request.getAttribute("respuesta");
    
        // 2. Imprimir el mensaje en la página
        // (Usamos out.println() para que interprete el HTML, 
        // como los <br> que pusiste en tu método de consulta)
        if (mensaje != null) {
            out.println(mensaje);
        } else {
            out.println("No se recibió ninguna respuesta del servidor.");
        }
    %>

    <br><br>
    
    <a href="index.html">Volver al Menú Principal</a>
    </body>
</html>
