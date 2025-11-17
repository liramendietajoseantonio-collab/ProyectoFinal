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
// Asumiendo que tu clase Conexion.java está en el paquete 'beans'
// import beans.Conexion;

/**
 * BEAN "Todo-en-Uno" (Modelo)
 * Representa la tabla 'Libros' y contiene toda su lógica de BD.
 */
public class Libro {

    // --- Atributos de la tabla Libros ---
    private int id_libro; // Para bajas, consultas y modificas
    private String titulo;
    private String isbn;
    private int stock_total;
    private int stock_disponible;
    private int id_autor;
    private int id_editorial;
    
    private String respuesta;

    // --- Getters y Setters ---

    public int getId_libro() {
        return id_libro;
    }
    public void setId_libro(int id_libro) {
        this.id_libro = id_libro;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getStock_total() {
        return stock_total;
    }
    public void setStock_total(int stock_total) {
        this.stock_total = stock_total;
    }

    public int getStock_disponible() {
        return stock_disponible;
    }
    public void setStock_disponible(int stock_disponible) {
        this.stock_disponible = stock_disponible;
    }
    
    public String getRespuesta() {
        return respuesta;
    }
    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public int getId_autor() {
        return id_autor;
    }

    public int getId_editorial() {
        return id_editorial;
    }

    public void setId_autor(int id_autor) {
        this.id_autor = id_autor;
    }

    public void setId_editorial(int id_editorial) {
        this.id_editorial = id_editorial;
    }
    
    

  

    /**
     * Da de alta un nuevo Libro.
     * Asigna Stock_Disponible = Stock_Total por defecto.
     */
    public void alta() {
        try (Connection cn = new Conexion().conectar()) {
            
            // --- SQL CORREGIDO ---
            // Tu SQL anterior estaba incompleto. Este incluye los campos obligatorios.
            String sql = "INSERT INTO Libros (Titulo, ISBN, ID_Autor, ID_Editorial, Stock_Total, Stock_Disponible) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            
            PreparedStatement ps = cn.prepareStatement(sql);
            
            ps.setString(1, this.titulo);
            ps.setString(2, this.isbn);
            ps.setInt(3, this.id_autor); // Campo que faltaba
            ps.setInt(4, this.id_editorial); // Campo que faltaba
            ps.setInt(5, this.stock_total);
            
            // El controlador pasa el stock_total, pero la lógica de negocio
            // es que al inicio, disponible = total.
            ps.setInt(6, this.stock_total); 
            
            ps.executeUpdate();
            
            respuesta = "Libro registrado exitosamente.";
            
        } catch (Exception e) {
            respuesta = "Error en alta de libro: " + e.getMessage();
            if (e.getMessage().contains("FOREIGN KEY constraint")) {
                respuesta = "Error: El ID de Autor o Editorial no existen.";
            }
            e.printStackTrace();
        }
    }

    // --- bajaLogica() (Tu código estaba bien) ---
    public void bajaLogica() {
        try (Connection cn = new Conexion().conectar()) {
            String sql = "UPDATE Libros SET BajaLogica = 1 WHERE ID_Libro = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id_libro);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                respuesta = "Libro dado de baja logicamente.";
            } else {
                respuesta = "No se encontró el ID del libro para eliminar.";
            }
        } catch (Exception e) {
            respuesta = "Error en baja lógica de libro: " + e.getMessage();
            e.printStackTrace();
        }
    }

    // --- consulta() (Tu código estaba bien) ---
    public void consulta() {
         try (Connection cn = new Conexion().conectar()) {
            String sql = "SELECT * FROM Libros WHERE ID_Libro = ? AND BajaLogica = 0";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id_libro);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                respuesta = "<b>ID Libro:</b> " + rs.getInt("ID_Libro") +
                            "<br><b>Título:</b> " + rs.getString("Titulo") +
                            "<br><b>ISBN:</b> " + rs.getString("ISBN") +
                            "<br><b>ID Autor:</b> " + rs.getInt("ID_Autor") +
                            "<br><b>ID Editorial:</b> " + rs.getInt("ID_Editorial") +
                            "<br><b>Stock Total:</b> " + rs.getInt("Stock_Total") +
                            "<br><b>Stock Disponible:</b> " + rs.getInt("Stock_Disponible");
            } else {
                respuesta = "No se encontró el libro o está dado de baja.";
            }
        } catch (Exception e) {
            respuesta = "Error en consulta de libro: " + e.getMessage();
            e.printStackTrace();
        }
    }

    // --- MÉTODO AÑADIDO (El que faltaba para el flujo de 2 pasos) ---
    public void consultaParaModificar() {
        try (Connection cn = new Conexion().conectar()) {
            String sql = "SELECT * FROM Libros WHERE ID_Libro = ? AND BajaLogica = 0";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, this.id_libro);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Carga los datos encontrados
                this.titulo = rs.getString("Titulo");
                this.isbn = rs.getString("ISBN");
                this.stock_total = rs.getInt("Stock_Total");
                this.stock_disponible = rs.getInt("Stock_Disponible");
                this.id_autor = rs.getInt("ID_Autor");
                this.id_editorial = rs.getInt("ID_Editorial");

                // Construye el formulario HTML
                respuesta = "<h1>Modificar Libro (Paso 2: Actualizar)</h1>";
                respuesta += "<form action='LibroControl' method='post'>";
                respuesta += "<b>ID Libro: " + this.id_libro + "</b><br>";
                respuesta += "<input type='hidden' name='id_libro' value='" + this.id_libro + "'>";

                // CAMPOS Editables
                respuesta += "Título: <input type='text' name='titulo' value='" + this.titulo + "'><br>";
                respuesta += "ISBN: <input type='text' name='isbn' value='" + this.isbn + "'><br>";
                respuesta += "ID Autor: <input type='number' name='id_autor' value='" + this.id_autor + "'><br>";
                respuesta += "ID Editorial: <input type='number' name='id_editorial' value='" + this.id_editorial + "'><br>";
                respuesta += "Stock Total: <input type='number' name='stock_total' value='" + this.stock_total + "'><br>";
                respuesta += "Stock Disponible: <input type='number' name='stock_disponible' value='" + this.stock_disponible + "'><br><br>";

                // BOTÓN DE SUBMIT
                respuesta += "<input type='submit' name='boton' value='Modificar Libro'>";
                respuesta += "</form>";
                
            } else {
                respuesta = "No se encontró el ID de libro '" + this.id_libro + "' o está dado de baja.";
            }
        } catch (Exception e) {
            respuesta = "Error en consulta para modificar libro: " + e.getMessage();
            e.printStackTrace();
        }
    }

    // --- modifica() (Tu código estaba bien) ---
    public void modifica() {
        try (Connection cn = new Conexion().conectar()) {
            String sql = "UPDATE Libros SET Titulo = ?, ISBN = ?, ID_Autor = ?, ID_Editorial = ?, Stock_Total = ?, Stock_Disponible = ? WHERE ID_Libro = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            
            ps.setString(1, this.titulo);
            ps.setString(2, this.isbn);
            ps.setInt(3, this.id_autor);
            ps.setInt(4, this.id_editorial);
            ps.setInt(5, this.stock_total);
            ps.setInt(6, this.stock_disponible);
            ps.setInt(7, this.id_libro);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                respuesta = "Libro actualizado correctamente.";
            } else {
                respuesta = "No se encontró el ID del libro para modificar.";
            }
        } catch (Exception e) {
            respuesta = "Error en modificación de libro: " + e.getMessage();
            e.printStackTrace();
        }
    }
}
