/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author garri
 */
public class Editorial {
    private int id;
    private String nombre;
    private String pais;
    private String respuesta;

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPais() {
        return pais;
    }

    public String getRespuesta() {
        return respuesta;
    }
    
    public void alta() {
        try (Connection cn = new Conexion().conectar()) { 
            
            // --- SQL CORREGIDO (Nombre, Pais) ---
            String sql = "INSERT INTO Editoriales (Nombre, Pais) VALUES (?, ?)";
            PreparedStatement ps = cn.prepareStatement(sql);
         
            ps.setString(1, this.nombre);
            ps.setString(2, this.pais);
            
            ps.executeUpdate();
            respuesta = "Editorial registrada con exito";
            
        } catch (Exception e) {
            respuesta = "Error en alta: " + e.getMessage();
            e.printStackTrace();
        }
    }

    public void bajaLogica() {
        try (Connection cn = new Conexion().conectar()) {
            String sql = "UPDATE Editoriales SET BajaLogica = 1 WHERE ID_Editorial = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                respuesta = "Editorial dada de baja; Su estado ha cambiado dentro de la Base de Datos.";
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
            String sql = "SELECT * FROM Editoriales WHERE ID_Editorial = ? AND BajaLogica = 0";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                
                
                respuesta = "<p> EDITORIAL ENCONTRADA </p><br>"+
                        "<b>ID de la editorial:</b> " + rs.getInt("ID_Editorial") +
                            "<br><b>Nombre:</b> " + rs.getString("Nombre") + 
                            "<br><b>Pais:</b> " + rs.getString("Pais");
                            
            } else {
                respuesta = "No se encontró el registro de la editorial.";
            }
        } catch (Exception e) {
            respuesta = "Error en consulta: " + e.getMessage();
            e.printStackTrace();
        }
    }

    // --- MÉTODO FALTANTE (AÑADIDO) ---
    /**
     * PASO 1: CONSULTA PARA MODIFICAR
     * Busca una editorial y genera un formulario HTML para editarla.
     */
    public void consultaParaModificar() {
        try (Connection cn = new Conexion().conectar()) {
            String sql = "SELECT * FROM Editoriales WHERE ID_Editorial = ? AND BajaLogica = 0";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // --- SQL CORREGIDO (Nombre, Pais) ---
                this.nombre = rs.getString("Nombre");
                this.pais = rs.getString("Pais");

                respuesta = "<h1>Modificar Editorial (Paso 2: Actualizar)</h1>";
                respuesta += "<form action='Editorial' method='post'>";
                respuesta += "<b>ID Editorial: " + this.id + "</b><br>";
                respuesta += "<input type='hidden' name='ID_Editorial' value='" + this.id + "'>";
                respuesta += "Nombre: <input type='text' name='nombre' value='" + this.nombre + "'><br>";
                respuesta += "País: <input type='text' name='pais' value='" + this.pais + "'><br><br>";
                respuesta += "<input type='submit' name='boton' value='Modificar Editorial'>";
                respuesta += "</form>";
                
            } else {
                respuesta = "No se encontró el ID de la editorial '" + this.id + "' o está dada de baja.";
            }
        } catch (Exception e) {
            respuesta = "Error en consulta para modificar: " + e.getMessage();
            e.printStackTrace();
        }
    }
    
    public void modifica() {
         try (Connection cn = new Conexion().conectar()) {
            
            // --- SQL CORREGIDO (Nombre, Pais) ---
            String sql = "UPDATE Editoriales SET Nombre = ?, Pais = ? WHERE ID_Editorial = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            
            ps.setString(1, this.nombre);
            ps.setString(2, this.pais);
            ps.setInt(3, this.id); 
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                respuesta = "Datos de la Editorial modificados exitosamente";
            } else {
                respuesta = "No se encontró el ID para modificar.";
            }
        } catch (Exception e) {
            respuesta = "Error en modificación: " + e.getMessage();
            e.printStackTrace();
        }
    }
}