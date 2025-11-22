/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans;

/**
 *
 * @author linkl
 */

// Importamos todo lo necesario de SQL

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class Persona {

    
    private String matricula;
    private String nombre;
    private String apellido;
    private String tipo;
    private String estado;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    // --- Atributo para la respuesta (como en tu ejemplo) ---
    private String respuesta;

    // --- Getters y Setters ---
    
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;}
    

    /**
     * Da de alta una nueva Persona.
     * Asigna 'Habilitado' por defecto.
     */
    public void alta() {
        try (Connection cn = new Conexion().conectar()) { 
            
            // Agregamos el campo Password al INSERT
            String sql = "INSERT INTO Personas (Matricula, Nombre, Apellido, Tipo, Password, Estado) VALUES (?, ?, ?, ?, ?, 'Habilitado')";
            
            PreparedStatement ps = cn.prepareStatement(sql);
            
            ps.setString(1, this.matricula);
            ps.setString(2, this.nombre);
            ps.setString(3, this.apellido);
            ps.setString(4, this.tipo);
            ps.setString(5, this.password); // Guardamos la contraseña
            
            ps.executeUpdate();
            
            respuesta = "Persona registrada exitosamente.";
            
        } catch (Exception e) {
            respuesta = "Error en alta: " + e.getMessage();
        }
    }

    /**
     * Ejecuta una BAJA LÓGICA (UPDATE), no un DELETE.
     * Esto cumple con tu rúbrica.
     */
    public void bajaLogica() {
        try (Connection cn = new Conexion().conectar()) {
            
            String sql = "UPDATE Personas SET BajaLogica = 1 WHERE Matricula = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            
            ps.setString(1, this.matricula);
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                respuesta = "Persona dada de baja logicamente. Su estado ha cambiado dentro de la base de datos";
            } else {
                respuesta = "No se encontró la matrícula para la baja.";
            }
            
        } catch (Exception e) {
            respuesta = "Error en baja lógica: " + e.getMessage();
            e.printStackTrace();
        }
    }
    
    public void consultaParaModificar() {
        try (Connection cn = new Conexion().conectar()) {
            
            String sql = "SELECT * FROM Personas WHERE Matricula = ? AND BajaLogica = 0";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, this.matricula);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Carga los datos encontrados en los atributos del bean
                this.nombre = rs.getString("Nombre");
                this.apellido = rs.getString("Apellido");
                this.tipo = rs.getString("Tipo");

                // Construye el formulario HTML como respuesta
                respuesta = "<h1>Modificar Persona (Paso 2: Actualizar)</h1>";
                
                // El formulario apunta de vuelta al Controlador
                respuesta += "<form action='Control' method='post'>"; // Asegúrate que sea 'Control' o 'PersonaControl'

                // CAMPO MATRÍCULA (Oculto - Hidden)
                respuesta += "<b>Matrícula: " + this.matricula + "</b><br>";
                respuesta += "<input type='hidden' name='matricula' value='" + this.matricula + "'>";

                // CAMPO NOMBRE
                respuesta += "Nombre: <input type='text' name='nombre' value='" + this.nombre + "'><br>";
                
                // CAMPO APELLIDO
                respuesta += "Apellido: <input type='text' name='apellido' value='" + this.apellido + "'><br>";

                // CAMPO TIPO (Select)
                respuesta += "Tipo: <select name='tipo'>";
                respuesta += (this.tipo.equals("Alumno")) ? "<option value='Alumno' selected>Alumno</option>" : "<option value='Alumno'>Alumno</option>";
                respuesta += (this.tipo.equals("Profesor")) ? "<option value='Profesor' selected>Profesor</option>" : "<option value='Profesor'>Profesor</option>";
                respuesta += "</select><br><br>";

                // BOTÓN DE SUBMIT (con el 'value' para el Paso 2)
                respuesta += "<input type='submit' name='boton' value='Modificar Alumno'>";
                respuesta += "</form>";
                
            } else {
                respuesta = "No se encontró la matrícula '" + this.matricula + "' o está dada de baja.";
            }
            
        } catch (Exception e) {
            respuesta = "Error en consulta para modificar: " + e.getMessage();
            e.printStackTrace();
        }
    }

    /**
     * Consulta una Persona por su Matrícula.
     * Solo muestra si BajaLogica es 0.
     */
    public void consulta() {
        try (Connection cn = new Conexion().conectar()) {
            
            String sql = "SELECT * FROM Personas WHERE Matricula = ? AND BajaLogica = 0";
            PreparedStatement ps = cn.prepareStatement(sql);
            
            ps.setString(1, this.matricula);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Construimos el string de respuesta (como en tu ejemplo)
                respuesta = "<b>Matrícula:</b> " + rs.getString("Matricula") +
                            "<br><b>Nombre:</b> " + rs.getString("Nombre") + " " + rs.getString("Apellido") +
                            "<br><b>Tipo:</b> " + rs.getString("Tipo") +
                            "<br><b>Estado:</b> " + rs.getString("Estado");
            } else {
                respuesta = "No se encontró el registro o está dado de baja.";
            }
            
        } catch (Exception e) {
            respuesta = "Error en consulta: " + e.getMessage();
            e.printStackTrace();
        }
    }

    /**
     * Modifica los datos de una Persona usando su Matrícula.
     */
    public void modifica() {
        try (Connection cn = new  Conexion().conectar()) {
            
            String sql = "UPDATE Personas SET Nombre = ?, Apellido = ?, Tipo = ? WHERE Matricula = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            
            ps.setString(1, this.nombre);
            ps.setString(2, this.apellido);
            ps.setString(3, this.tipo);
            ps.setString(4, this.matricula); // La matrícula es el WHERE
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                respuesta = "Persona actualizada correctamente.";
            } else {
                respuesta = "No se encontró la matrícula para modificar.";
            }
            
        } catch (Exception e) {
            respuesta = "Error en modificación: " + e.getMessage();
            e.printStackTrace();
        }
    }
}
