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
public class Autores {
    
    // --- Atributos ---
    private int id;
    private String nombre;
    private String apellido;
    private String respuesta;
    private String nacionalidad;
    
    // --- Atributo CORREGIDO ---
    private boolean bajaLogica; // Estaba como String, debe ser boolean

    // --- Getters y Setters (CORREGIDOS) ---
    
    public int getId() { 
        return id; 
    }
    public void setId(int id) {
        this.id = id; 
    }
    
    public String getNombre() {
        return nombre; 
    }
    public void setNombre(String nombre) {
        this.nombre = nombre; 
    }
    
    public String getApellido() {
        return apellido; 
    }
    public void setApellido(String apellido) {
        this.apellido = apellido; 
    }
    
    public String getNacionalidad() 
    {
        return nacionalidad; 
    }
    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad; 
    }
    
    public String getRespuesta() {
        return respuesta; 
    }
    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta; 
    }

    // Getter y Setter para boolean
    public boolean isBajaLogica() {
        return bajaLogica; 
    }
    public void setBajaLogica(boolean bajaLogica) {
        this.bajaLogica = bajaLogica; 
    }
    
    
    // ===================================
    // === MÉTODOS CRUD (Con el método que faltaba)
    // ===================================

    // --- alta() (Corregido) ---
    public void alta() {
        try (Connection cn = new Conexion().conectar()) { 
            // Corregido: La BD espera 'Nombre', 'Apellido', 'Nacionalidad' (Mayúsculas)
            String sql = "INSERT INTO Autores (ID_Autor, Nombre, Apellido, Nacionalidad) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id);
            ps.setString(2, this.nombre);
            ps.setString(3, this.apellido);
            ps.setString(4, this.nacionalidad);
            ps.executeUpdate();
            respuesta = "Autor registrado con exito";
        } catch (Exception e) {
            respuesta = "Error en alta: " + e.getMessage();
            e.printStackTrace();
        }
    }

    // --- bajaLogica() (Corregido) ---
    public void bajaLogica() {
        try (Connection cn = new Conexion().conectar()) {
            // Corregido: 'ID_Autor' (Mayúscula)
            String sql = "UPDATE Autores SET BajaLogica = 1 WHERE ID_Autor = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                respuesta = "Autor dado de baja logicamente.";
            } else {
                respuesta = "No se encontró el ID para la baja.";
            }
        } catch (Exception e) {
            respuesta = "Error en baja lógica: " + e.getMessage();
            e.printStackTrace();
        }
    }

    // --- consulta() (Corregido) ---
    public void consulta() {
        try (Connection cn = new Conexion().conectar()) {
            String sql = "SELECT * FROM Autores WHERE ID_Autor = ? AND BajaLogica = 0";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Corregido: Nombres de columna con Mayúscula
                respuesta = "<b>ID:</b> " + rs.getInt("ID_Autor") +
                            "<br><b>Nombre:</b> " + rs.getString("Nombre") + 
                            "<br><b>Apellido:</b> " + rs.getString("Apellido") +
                            "<br><b>Nacionalidad:</b> " + rs.getString("Nacionalidad");
            } else {
                respuesta = "No se encontró el registro del autor; Es probable que su estado sea baja.";
            }
        } catch (Exception e) {
            respuesta = "Error en consulta: " + e.getMessage();
            e.printStackTrace();
        }
    }

    // --- MÉTODO FALTANTE (AÑADIDO) ---
    /**
     * PASO 1: CONSULTA PARA MODIFICAR
     * Busca un autor y genera un formulario HTML para editarlo.
     */
    public void consultaParaModificar() {
        try (Connection cn = new Conexion().conectar()) {
            String sql = "SELECT * FROM Autores WHERE ID_Autor = ? AND BajaLogica = 0";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Carga los datos encontrados
                this.nombre = rs.getString("Nombre");
                this.apellido = rs.getString("Apellido");
                this.nacionalidad = rs.getString("Nacionalidad");

                // Construye el formulario HTML
                respuesta = "<h1>Modificar Autor (Paso 2: Actualizar)</h1>";
                respuesta += "<form action='AutoresControl' method='post'>"; // Apunta al controlador de Autor

                // CAMPO ID_Autor (Oculto)
                respuesta += "<b>ID Autor: " + this.id + "</b><br>";
                respuesta += "<input type='hidden' name='ID_Autor' value='" + this.id + "'>";

                // CAMPOS Editables
                respuesta += "Nombre: <input type='text' name='nombre' value='" + this.nombre + "'><br>";
                respuesta += "Apellido: <input type='text' name='apellido' value='" + this.apellido + "'><br>";
                respuesta += "Nacionalidad: <input type='text' name='nacionalidad' value='" + this.nacionalidad + "'><br><br>";

                // BOTÓN DE SUBMIT (con el 'value' correcto)
                respuesta += "<input type='submit' name='boton' value='Modificar Autor'>";
                respuesta += "</form>";
                
            } else {
                respuesta = "No se encontró el ID de autor '" + this.id + "' o está dado de baja.";
            }
        } catch (Exception e) {
            respuesta = "Error en consulta para modificar autor: " + e.getMessage();
            e.printStackTrace();
        }
    }

    // --- modifica() (Corregido) ---
    public void modifica() {
         try (Connection cn = new Conexion().conectar()) {
            // Corregido: Nombres de columna con Mayúscula
            String sql = "UPDATE Autores SET Nombre = ?, Apellido = ?, Nacionalidad = ? WHERE ID_Autor = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            
            ps.setString(1, this.nombre);
            ps.setString(2, this.apellido);
            ps.setString(3, this.nacionalidad);
            ps.setInt(4, this.id); 
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                respuesta = "Datos del autor modificados exitosamente";
            } else {
                respuesta = "No se encontró el ID para modificar.";
            }
        } catch (Exception e) {
            respuesta = "Error en modificación: " + e.getMessage();
            e.printStackTrace();
        }
    }
}

    

