/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author garri
 */
public class Ejemplar {
    
    // --- Atributos ---
    private int id_ejemplar;
    private int id_libro;
    private int numero_copia;
    private String estado;
    private String respuesta;
    private boolean bajaLogica;

    // --- Getters y Setters ---
    
    public int getId_ejemplar() { 
        return id_ejemplar; 
    }
    public void setId_ejemplar(int id_ejemplar) {
        this.id_ejemplar = id_ejemplar; 
    }
    
    public int getId_libro() {
        return id_libro; 
    }
    public void setId_libro(int id_libro) {
        this.id_libro = id_libro; 
    }
    
    public int getNumero_copia() {
        return numero_copia; 
    }
    public void setNumero_copia(int numero_copia) {
        this.numero_copia = numero_copia; 
    }
    
    public String getEstado() 
    {
        return estado; 
    }
    public void setEstado(String estado) {
        this.estado = estado; 
    }
    
    public String getRespuesta() {
        return respuesta; 
    }
    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta; 
    }

    public boolean isBajaLogica() {
        return bajaLogica; 
    }
    public void setBajaLogica(boolean bajaLogica) {
        this.bajaLogica = bajaLogica; 
    }
    
    
 

    /**
     * Da de alta un nuevo Ejemplar (copia física de un libro)
     */
    public void alta() {
        try (Connection cn = new Conexion().conectar()) { 
            String sql = "INSERT INTO Ejemplares (ID_Libro, Numero_Copia, Estado) VALUES (?, ?, 'Disponible')";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id_libro);
            ps.setInt(2, this.numero_copia);
            ps.executeUpdate();
            respuesta = "Ejemplar registrado con exito";
        } catch (Exception e) {
            respuesta = "Error en alta: " + e.getMessage();
            e.printStackTrace();
        }
    }

    /**
     * Baja lógica de un Ejemplar
     */
    public void bajaLogica() {
        try (Connection cn = new Conexion().conectar()) {
            String sql = "UPDATE Ejemplares SET BajaLogica = 1 WHERE ID_Ejemplar = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id_ejemplar);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                respuesta = "Ejemplar dado de baja logicamente.";
            } else {
                respuesta = "No se encontró el ID para la baja.";
            }
        } catch (Exception e) {
            respuesta = "Error en baja lógica: " + e.getMessage();
            e.printStackTrace();
        }
    }

    /**
     * Consulta un Ejemplar por su ID
     */
    public void consulta() {
        try (Connection cn = new Conexion().conectar()) {
            String sql = "SELECT e.*, l.Titulo FROM Ejemplares e " +
                        "INNER JOIN Libros l ON e.ID_Libro = l.ID_Libro " +
                        "WHERE e.ID_Ejemplar = ? AND e.BajaLogica = 0";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id_ejemplar);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = "<b>ID Ejemplar:</b> " + rs.getInt("ID_Ejemplar") +
                            "<br><b>ID Libro:</b> " + rs.getInt("ID_Libro") + 
                            "<br><b>Título:</b> " + rs.getString("Titulo") +
                            "<br><b>Número de Copia:</b> " + rs.getInt("Numero_Copia") +
                            "<br><b>Estado:</b> " + rs.getString("Estado");
            } else {
                respuesta = "No se encontró el registro del ejemplar; Es probable que su estado sea baja.";
            }
        } catch (Exception e) {
            respuesta = "Error en consulta: " + e.getMessage();
            e.printStackTrace();
        }
    }

    /**
     * PASO 1: CONSULTA PARA MODIFICAR
     * Busca un ejemplar y genera un formulario HTML para editarlo.
     */
    public void consultaParaModificar() {
        try (Connection cn = new Conexion().conectar()) {
            String sql = "SELECT * FROM Ejemplares WHERE ID_Ejemplar = ? AND BajaLogica = 0";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id_ejemplar);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                this.id_libro = rs.getInt("ID_Libro");
                this.numero_copia = rs.getInt("Numero_Copia");
                this.estado = rs.getString("Estado");

                respuesta = "<h1>Modificar Ejemplar (Paso 2: Actualizar)</h1>";
                respuesta += "<form action='EjemplarControl' method='post'>";

                respuesta += "<b>ID Ejemplar: " + this.id_ejemplar + "</b><br>";
                respuesta += "<input type='hidden' name='id_ejemplar' value='" + this.id_ejemplar + "'>";

                respuesta += "ID Libro: <input type='number' name='id_libro' value='" + this.id_libro + "'><br>";
                respuesta += "Número de Copia: <input type='number' name='numero_copia' value='" + this.numero_copia + "'><br>";
                respuesta += "Estado: <select name='estado'>";
                respuesta += (this.estado.equals("Disponible")) ? "<option value='Disponible' selected>Disponible</option>" : "<option value='Disponible'>Disponible</option>";
                respuesta += (this.estado.equals("Prestado")) ? "<option value='Prestado' selected>Prestado</option>" : "<option value='Prestado'>Prestado</option>";
                respuesta += (this.estado.equals("Dañado")) ? "<option value='Dañado' selected>Dañado</option>" : "<option value='Dañado'>Dañado</option>";
                respuesta += "</select><br><br>";

                respuesta += "<input type='submit' name='boton' value='Modificar Ejemplar'>";
                respuesta += "</form>";
                
            } else {
                respuesta = "No se encontró el ID de ejemplar '" + this.id_ejemplar + "' o está dado de baja.";
            }
        } catch (Exception e) {
            respuesta = "Error en consulta para modificar ejemplar: " + e.getMessage();
            e.printStackTrace();
        }
    }

    /**
     * Modifica los datos de un Ejemplar
     */
    public void modifica() {
         try (Connection cn = new Conexion().conectar()) {
            String sql = "UPDATE Ejemplares SET ID_Libro = ?, Numero_Copia = ?, Estado = ? WHERE ID_Ejemplar = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            
            ps.setInt(1, this.id_libro);
            ps.setInt(2, this.numero_copia);
            ps.setString(3, this.estado);
            ps.setInt(4, this.id_ejemplar); 
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                respuesta = "Datos del ejemplar modificados exitosamente";
            } else {
                respuesta = "No se encontró el ID para modificar.";
            }
        } catch (Exception e) {
            respuesta = "Error en modificación: " + e.getMessage();
            e.printStackTrace();
        }
    }
}