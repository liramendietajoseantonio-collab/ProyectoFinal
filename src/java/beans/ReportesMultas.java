/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans;

/**
 *
 * @author linkl
 */

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


public class ReportesMultas {
    
    private String respuesta;

    public String getRespuesta() { return respuesta; }

    // --- MÉTODO AUXILIAR PARA CREAR CARPETA SI NO EXISTE ---
    // Esto evita errores si la carpeta "Documentos" no se copió al servidor
    private void verificarCarpeta(String ruta) {
        File carpeta = new File(ruta);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }

    // ==========================================
    // 1. GENERAR GRÁFICA (Recibe la ruta)
    // ==========================================
    public void crearGrafica(String rutaCarpeta) {
        try (Connection c = new Conexion().conectar()) {
            if (c != null) {
                DefaultCategoryDataset datos = new DefaultCategoryDataset();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery("SELECT ID_Multa, Monto_Total FROM Multas");

                while (rs.next()) {
                    datos.addValue(rs.getDouble("Monto_Total"), "Deuda ($)", String.valueOf(rs.getInt("ID_Multa")));
                }

                JFreeChart grafico = ChartFactory.createBarChart3D(
                        "Reporte de Deudas por Multa", 
                        "ID Multa", 
                        "Monto ($)", 
                        datos, 
                        PlotOrientation.VERTICAL, 
                        true, true, true
                );

                // --- USO DE LA RUTA DINÁMICA ---
                verificarCarpeta(rutaCarpeta);
                File archivoImagen = new File(rutaCarpeta + File.separator + "grafico_multas.jpeg");
                
                ChartUtilities.saveChartAsJPEG(archivoImagen, grafico, 500, 400);
            }
        } catch (Exception er) {
            er.printStackTrace();
            respuesta = "Error al crear gráfico: " + er.getMessage();
        }
    }
    
    public void crearPdf(String rutaCarpeta) {
        
        // 1. Generamos la imagen primero (aunque la usemos al final)
        crearGrafica(rutaCarpeta); 
        
        String rutaArchivoPDF = rutaCarpeta + File.separator + "reporte_multas.pdf";
        String rutaImagenGrafica = rutaCarpeta + File.separator + "grafico_multas.jpeg";

        Document documento = new Document();

        try {
            PdfWriter.getInstance(documento, new FileOutputStream(rutaArchivoPDF));
            documento.open();

            // --- A) TÍTULO (Siempre va arriba) ---
            Paragraph titulo = new Paragraph("Reporte Detallado de Multas\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            documento.add(new Paragraph("\n")); // Espacio

            // --- B) TABLA DE DATOS (AHORA VA PRIMERO) ---
            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100); 

            tabla.addCell("ID Multa");
            tabla.addCell("Matrícula");
            tabla.addCell("Días Retraso");
            tabla.addCell("Monto Total");
            tabla.addCell("Estado");

            try (Connection cn = new Conexion().conectar()) {
                if (cn != null) {
                    Statement st = cn.createStatement();
                    String sql = "SELECT ID_Multa, Matricula, Dias_Retraso, Monto_Total, Estado FROM Multas";
                    ResultSet rs = st.executeQuery(sql);

                    while (rs.next()) {
                        tabla.addCell(String.valueOf(rs.getInt("ID_Multa")));
                        tabla.addCell(rs.getString("Matricula"));
                        tabla.addCell(String.valueOf(rs.getInt("Dias_Retraso")));
                        tabla.addCell("$" + rs.getString("Monto_Total"));
                        tabla.addCell(rs.getString("Estado"));
                    }
                } else {
                    tabla.addCell("Sin Conexión");
                    tabla.addCell(""); tabla.addCell(""); tabla.addCell(""); tabla.addCell("");
                }
            } catch (Exception sqlE) {
                tabla.addCell("Error BD: " + sqlE.getMessage());
            }
            // Agregamos la tabla al documento
            documento.add(tabla);

            // --- C) ESPACIO SEPARADOR ---
            documento.add(new Paragraph("\n\n")); 

            // --- D) IMAGEN DE LA GRÁFICA (AHORA VA AL FINAL) ---
            try {
                Image imagen = Image.getInstance(rutaImagenGrafica);
                imagen.setAlignment(Element.ALIGN_CENTER);
                imagen.scaleToFit(500, 400);
                documento.add(imagen);
            } catch (Exception imgE) {
                documento.add(new Paragraph("ERROR: No se pudo cargar la gráfica."));
            }

            documento.close();
            respuesta = "PDF generado correctamente en: " + rutaArchivoPDF;

        } catch (Exception e) {
            respuesta = "Error al crear PDF: " + e.getMessage();
        }
    }
    // ==========================================
    // 3. CREAR EXCEL (Recibe la ruta)
    // ==========================================
    public void crearExcel(String rutaCarpeta) {
        try {
            // USAMOS LA RUTA DINÁMICA
            verificarCarpeta(rutaCarpeta);
            File f = new File(rutaCarpeta + File.separator + "tabla_multas.xls");
            
            FileWriter fw = new FileWriter(f, false);
            
            try (Connection c = new Conexion().conectar()) {
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery("SELECT ID_Multa, Matricula, Dias_Retraso, Monto_Total, Estado FROM Multas");
                
                fw.write("ID\tMatricula\tDias\tMonto\tEstado\n");
                
                while (rs.next()) {
                    fw.write(rs.getInt("ID_Multa") + "\t");
                    fw.write(rs.getString("Matricula") + "\t");
                    fw.write(rs.getInt("Dias_Retraso") + "\t");
                    fw.write(rs.getDouble("Monto_Total") + "\t");
                    fw.write(rs.getString("Estado") + "\n");
                }
            }
            fw.close();
            respuesta = "Excel creado correctamente en: " + f.getAbsolutePath();
            
        } catch (Exception er) {
            respuesta = "Error al crear Excel: " + er.getMessage();
        }
    }
}
