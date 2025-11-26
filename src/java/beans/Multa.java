/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans;

/**
 *
 * @author linkl
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Multa {
    
    private int id_multa;
    private String respuesta;

    public int getId_multa() {
        return id_multa;
    }

    public void setId_multa(int id_multa) {
        this.id_multa = id_multa;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
    
    public void consulta() {
        try (Connection cn = new Conexion().conectar()) {
            
            // 1. CAMBIO EN SQL: Agregamos M.Fecha_Generacion
            String sql = "SELECT M.ID_Multa, M.ID_Prestamo, M.Matricula, P.Nombre, P.Apellido, M.Dias_Retraso, M.Monto_Total, M.Fecha_Generacion " +
                         "FROM Multas M " +
                         "JOIN Personas P ON M.Matricula = P.Matricula " +
                         "WHERE M.Estado = 'Pendiente' AND P.BajaLogica = 0";
            
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            StringBuilder tablaHtml = new StringBuilder();
            tablaHtml.append("<h2>Multas Pendientes de Pago</h2>");
            tablaHtml.append("<table border='1'>");
            
            // 2. CAMBIO EN ENCABEZADOS: Agregamos 'Fecha'
            tablaHtml.append("<tr>")
                     .append("<th>ID Multa</th>")
                     .append("<th>Matrícula</th>")
                     .append("<th>Persona</th>")
                     .append("<th>Días Retr.</th>")
                     .append("<th>Monto Total ($)</th>")
                     .append("<th>Fecha Gen.</th>") // <--- Nuevo
                     .append("<th>ID Préstamo</th>")
                     .append("</tr>");
            
            int contador = 0;
            while (rs.next()) {
                tablaHtml.append("<tr>");
                tablaHtml.append("<td>").append(rs.getInt("ID_Multa")).append("</td>");
                tablaHtml.append("<td>").append(rs.getString("Matricula")).append("</td>");
                tablaHtml.append("<td>").append(rs.getString("Nombre")).append(" ").append(rs.getString("Apellido")).append("</td>");
                tablaHtml.append("<td>").append(rs.getInt("Dias_Retraso")).append("</td>");
                tablaHtml.append("<td>").append(rs.getBigDecimal("Monto_Total")).append("</td>");
                
                // 3. CAMBIO EN DATOS: Agregamos la celda de la fecha
                tablaHtml.append("<td>").append(rs.getDate("Fecha_Generacion")).append("</td>");
                
                tablaHtml.append("<td>").append(rs.getInt("ID_Prestamo")).append("</td>");
                tablaHtml.append("</tr>");
                contador++;
            }
            
            tablaHtml.append("</table>");
            
            if (contador == 0) {
                respuesta = "No se encontraron multas pendientes.";
            } else {
                respuesta = tablaHtml.toString();
            }
            
        } catch (Exception e) {
            respuesta = "Error en consulta de multas: " + e.getMessage();
        }
    }

    /**
     * MODIFICA (Pagar Multa)
     * 1. Obtiene la Matrícula de la multa.
     * 2. Actualiza la Multa a 'Pagada'.
     * 3. Actualiza a la Persona a 'Habilitado'.
     */
    public void modifica() {
        Connection cn = null;
        String matriculaParaHabilitar = null;
        
        try {
            cn = new Conexion().conectar();
            cn.setAutoCommit(false); // Iniciar transacción

            // --- 1. Obtener la Matrícula de la multa ---
            String sqlCheck = "SELECT Matricula FROM Multas WHERE ID_Multa = ? AND Estado = 'Pendiente'";
            PreparedStatement psCheck = cn.prepareStatement(sqlCheck);
            psCheck.setInt(1, this.id_multa);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                matriculaParaHabilitar = rs.getString("Matricula");
            } else {
                throw new Exception("No se encontró la multa o ya estaba pagada.");
            }

            // --- 2. Actualizar Multa a 'Pagada' ---
            String sqlUpdateM = "UPDATE Multas SET Estado = 'Pagada' WHERE ID_Multa = ?";
            PreparedStatement psUpdateM = cn.prepareStatement(sqlUpdateM);
            psUpdateM.setInt(1, this.id_multa);
            psUpdateM.executeUpdate();

            // --- 3. Actualizar Persona a 'Habilitado' ---
            String sqlUpdateP = "UPDATE Personas SET Estado = 'Habilitado' WHERE Matricula = ?";
            PreparedStatement psUpdateP = cn.prepareStatement(sqlUpdateP);
            psUpdateP.setString(1, matriculaParaHabilitar);
            psUpdateP.executeUpdate();

            cn.commit(); // Confirmar transacción
            respuesta = "Multa ID " + this.id_multa + " pagada. La persona '" + matriculaParaHabilitar + "' ha sido habilitada.";

        } catch (Exception e) {
            try { if (cn != null) cn.rollback(); } catch (SQLException se) { se.printStackTrace(); }
            respuesta = "Error al pagar la multa: " + e.getMessage();
            e.printStackTrace();
        } finally {
            try { if (cn != null) cn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}