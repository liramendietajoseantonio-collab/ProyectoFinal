<%-- 
    Document   : mascota
    Created on : 1/10/2025, 09:56:55 PM
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
        <%@ page import="java.sql.*" %>
<jsp:useBean id="beanM" class="bean.Mascota" scope="request"/>
<jsp:setProperty name="beanM" property="mascota" param="mascota"/>

<%
    // Actualizar la base
    beanM.modifica();

    // Ahora consultamos la tabla completa
    Connection cn = null;
    try {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        cn = DriverManager.getConnection(
            "jdbc:sqlserver://localhost:1433;databaseName=encuesta;user=sa;password=sasa;"
        );

        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery("SELECT mascota, frecuencia FROM mascota");

        out.println("<h2>Resultados de la encuesta</h2>");
        out.println("<table border='1'>");
        out.println("<tr><th>Mascota</th><th>Frecuencia</th><th>Porcentaje</th></tr>");

        while (rs.next()) {
            String masc = rs.getString("mascota");
            int frec = rs.getInt("frecuencia");

            double porcentaje = 0;
            if (beanM.getSuma() > 0) {
                porcentaje = (frec * 100.0) / beanM.getSuma();
            }

            out.println("<tr>");
            out.println("<td>" + masc + "</td>");
            out.println("<td>" + frec + "</td>");
            out.println("<td>" + String.format("%.2f", porcentaje) + " %</td>");
            out.println("</tr>");
        }

        out.println("</table>");

        rs.close();
        st.close();
        cn.close();

    } catch (Exception e) {
        out.println("Error: " + e.getMessage());
    }
%>

    </body>
</html>
