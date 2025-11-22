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
import java.sql.Date;



public class Prestamo {

   
    private int id_prestamo;
    private String matricula;
    private Date fecha_prestamo;
    private int id_ejemplar;
    
    private String respuesta;

    public void setFecha_prestamo(Date fecha_prestamo) {
        this.fecha_prestamo = fecha_prestamo;
    }

    public Date getFecha_prestamo() {
        return fecha_prestamo;
    }

    
    public int getId_prestamo() {
        return id_prestamo;
    }
    public void setId_prestamo(int id_prestamo) {
        this.id_prestamo = id_prestamo;
    }

    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public int getId_ejemplar() { return id_ejemplar; }
    public void setId_ejemplar(int id_ejemplar) { this.id_ejemplar = id_ejemplar; }
    
    public String getRespuesta() {
        return respuesta;
    }
    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
    
    public void alta() {
    Connection cn = null;
    try {
        cn = new Conexion().conectar();
        cn.setAutoCommit(false); 

        // --- 1. Verificar Persona ---
        PreparedStatement psCheckPersona = cn.prepareStatement("SELECT Estado FROM Personas WHERE Matricula = ? AND BajaLogica = 0");
        psCheckPersona.setString(1, this.matricula);
        ResultSet rsPersona = psCheckPersona.executeQuery();
        if (!rsPersona.next() || !rsPersona.getString("Estado").equals("Habilitado")) {
            throw new Exception("Error: Persona no encontrada o está 'Deshabilitada'.");
        }

        // --- 2. Verificar Ejemplar ---
        PreparedStatement psCheckEjemplar = cn.prepareStatement("SELECT Estado FROM Ejemplares WHERE ID_Ejemplar = ? AND BajaLogica = 0");
        psCheckEjemplar.setInt(1, this.id_ejemplar);
        ResultSet rsEjemplar = psCheckEjemplar.executeQuery();
        if (!rsEjemplar.next() || !rsEjemplar.getString("Estado").equals("Disponible")) {
            throw new Exception("Error: Ejemplar no encontrado o no está disponible.");
        }

     
        // --- 3. Obtener el ID_Libro del Ejemplar ---
         int idLibro = 0;
         PreparedStatement psGetLibro = cn.prepareStatement("SELECT ID_Libro FROM Ejemplares WHERE ID_Ejemplar = ?");
         psGetLibro.setInt(1, this.id_ejemplar);
          ResultSet rsLibro = psGetLibro.executeQuery();
        if (rsLibro.next()) {
    idLibro = rsLibro.getInt("ID_Libro");
   }

      // --- 4. Insertar Préstamo ---
       String sqlInsert = "INSERT INTO Prestamos (Matricula, ID_Libro, ID_Ejemplar, Fecha_Prestamo) VALUES (?, ?, ?, ?)";
         PreparedStatement psInsert = cn.prepareStatement(sqlInsert);
           psInsert.setString(1, this.matricula);
        psInsert.setInt(2, idLibro);
        psInsert.setInt(3, this.id_ejemplar);
        psInsert.setDate(4, this.fecha_prestamo);
        psInsert.executeUpdate();

        // Actualizar Estado del Ejemplar ---
        String sqlUpdate = "UPDATE Ejemplares SET Estado = 'Prestado' WHERE ID_Ejemplar = ?";
        PreparedStatement psUpdate = cn.prepareStatement(sqlUpdate);
        psUpdate.setInt(1, this.id_ejemplar);
        psUpdate.executeUpdate();

        cn.commit(); 
        respuesta = "Préstamo registrado exitosamente.";

    } catch (Exception e) {
        try { if (cn != null) cn.rollback(); } catch (SQLException se) { se.printStackTrace(); }
        respuesta = "Error en alta de préstamo: " + e.getMessage();
        e.printStackTrace();
    } finally {
        try { if (cn != null) cn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}


   public void bajaLogica(){
    try (Connection cn = new Conexion().conectar()) {
        
        String sql = "UPDATE Prestamos SET Estado = 'Cancelado' WHERE ID_Prestamo = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setInt(1, this.id_prestamo);
        
        int filas = ps.executeUpdate();
        
        if (filas > 0) {
            respuesta = "Prestamo dado de baja logicamente; ha cambiado su estado dentro de la base de datos.";
        } else {
            respuesta = "No se encontró el ID para la baja.";
        }
        
    } catch (Exception e) {
        respuesta = "Error en baja lógica: " + e.getMessage();
        e.printStackTrace();
    }
}

    
    
  public void consulta() {
    try (Connection cn = new Conexion().conectar()) {
        
        String sql = "SELECT p.*, e.Numero_Copia, l.Titulo " +
                     "FROM Prestamos p " +
                     "INNER JOIN Ejemplares e ON p.ID_Ejemplar = e.ID_Ejemplar " +
                     "INNER JOIN Libros l ON e.ID_Libro = l.ID_Libro " +
                     "WHERE p.ID_Prestamo = ? AND p.Estado = 'Activo'";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setInt(1, this.id_prestamo);
        
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            respuesta = "<b>ID Préstamo:</b> " + rs.getInt("ID_Prestamo") +
                        "<br><b>Matrícula (Persona):</b> " + rs.getString("Matricula") +
                        "<br><b>ID Ejemplar:</b> " + rs.getInt("ID_Ejemplar") +
                        "<br><b>Título del Libro:</b> " + rs.getString("Titulo") +
                        "<br><b>Número de Copia:</b> " + rs.getInt("Numero_Copia") +
                        "<br><b>Fecha Préstamo:</b> " + rs.getDate("Fecha_Prestamo") +
                        "<br><b>Fecha Devolución Esperada:</b> " + rs.getDate("Fecha_Devolucion_Esperada") +
                        "<br><b>Estado:</b> " + rs.getString("Estado");
        } else {
            respuesta = "No se encontró el préstamo o no está activo.";
        }
        
    } catch (Exception e) {
        respuesta = "Error en consulta: " + e.getMessage();
        e.printStackTrace();
    }
}


    
    public void modifica() {
    Connection cn = null;
    try {
        cn = new Conexion().conectar();
        cn.setAutoCommit(false);
        
        int idEjemplarDevuelto = 0;
        int diasRetraso = 0;
        String matriculaPersona = "";

        // --- 1. Obtener datos y Días de Retraso ---
        String sqlCheck = "SELECT ID_Ejemplar, Matricula, DATEDIFF(day, Fecha_Devolucion_Esperada, GETDATE()) AS DiasRetraso " +
                          "FROM Prestamos WHERE ID_Prestamo = ? AND Estado = 'Activo'";
        PreparedStatement psCheck = cn.prepareStatement(sqlCheck);
        psCheck.setInt(1, this.id_prestamo);
        ResultSet rs = psCheck.executeQuery();

        if (!rs.next()) {
            throw new Exception("Error: No se encontró el préstamo o ya había sido devuelto.");
        }
        idEjemplarDevuelto = rs.getInt("ID_Ejemplar");
        matriculaPersona = rs.getString("Matricula");
        diasRetraso = rs.getInt("DiasRetraso");
        
        // --- 2. Actualizar Préstamo a 'Devuelto' ---
        String sqlUpdateP = "UPDATE Prestamos SET Estado = 'Devuelto', Fecha_Devolucion_Real = GETDATE() WHERE ID_Prestamo = ?";
        PreparedStatement psUpdateP = cn.prepareStatement(sqlUpdateP);
        psUpdateP.setInt(1, this.id_prestamo);
        psUpdateP.executeUpdate();

        // --- 3. Actualizar Estado del Ejemplar ---
        String sqlUpdateE = "UPDATE Ejemplares SET Estado = 'Disponible' WHERE ID_Ejemplar = ?";
        PreparedStatement psUpdateE = cn.prepareStatement(sqlUpdateE);
        psUpdateE.setInt(1, idEjemplarDevuelto);
        psUpdateE.executeUpdate();

        // --- 4. Lógica de Multa ---
        String msgMulta = "Devolución registrada exitosamente.";
        
        if (diasRetraso > 0) {
            // A) Deshabilitar a la persona
            String sqlUpdatePer = "UPDATE Personas SET Estado = 'Deshabilitado' WHERE Matricula = ?";
            PreparedStatement psUpdatePer = cn.prepareStatement(sqlUpdatePer);
            psUpdatePer.setString(1, matriculaPersona);
            psUpdatePer.executeUpdate();

            // B) Insertar la Multa
            String sqlInsertMulta = "INSERT INTO Multas (ID_Prestamo, Matricula, Dias_Retraso, Estado) VALUES (?, ?, ?, 'Pendiente')";
            PreparedStatement psInsertMulta = cn.prepareStatement(sqlInsertMulta);
            psInsertMulta.setInt(1, this.id_prestamo);
            psInsertMulta.setString(2, matriculaPersona);
            psInsertMulta.setInt(3, diasRetraso);
            psInsertMulta.executeUpdate();
            
            msgMulta = "<b>¡Devolución TARDÍA!</b> Se registró la devolución.<br>" +
                       "Se generó una multa por " + diasRetraso + " días.<br>" +
                       "La persona ha sido <b>Deshabilitada</b> para futuros préstamos.";
        }

        cn.commit();
        respuesta = msgMulta;

    } catch (Exception e) {
        try { if (cn != null) cn.rollback(); } catch (SQLException se) { se.printStackTrace(); }
        respuesta = "Error al devolver préstamo: " + e.getMessage();
        e.printStackTrace();
    } finally {
        try { if (cn != null) cn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
}

   

  
