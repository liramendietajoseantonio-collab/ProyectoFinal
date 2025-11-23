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
import java.text.DecimalFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class ReportesLibros {
    
    private String respuesta;

    public String getRespuesta() { return respuesta; }

    // --- MÉTODO AUXILIAR ---
    private void verificarCarpeta(String ruta) {
        File carpeta = new File(ruta);
        if (!carpeta.exists()) { carpeta.mkdirs(); }
    }

    // --- 1. GRÁFICA DE PASTEL ---
    public void crearGraficaPie(String rutaCarpeta) {
        try (Connection c = new Conexion().conectar()) {
            if (c != null) {
                DefaultPieDataset datos = new DefaultPieDataset();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery("SELECT Titulo, Stock_Total FROM Libros WHERE BajaLogica = 0");

                while (rs.next()) {
                    datos.setValue(rs.getString("Titulo"), rs.getInt("Stock_Total"));
                }

                JFreeChart grafico = ChartFactory.createPieChart3D(
                        "Distribución del Stock por Libro", 
                        datos, true, true, true
                );

                verificarCarpeta(rutaCarpeta);
                File archivoImagen = new File(rutaCarpeta + File.separator + "grafico_libros.jpeg");
                ChartUtilities.saveChartAsJPEG(archivoImagen, grafico, 500, 400);
            }
        } catch (Exception er) {
            er.printStackTrace();
        }
    }

    // --- 2. PDF (TABLA + GRÁFICA) ---
    public void crearPdf(String rutaCarpeta) {
        
        crearGraficaPie(rutaCarpeta); // Generar imagen
        
        String rutaArchivoPDF = rutaCarpeta + File.separator + "reporte_inventario.pdf";
        String rutaImagenGrafica = rutaCarpeta + File.separator + "grafico_libros.jpeg";

        Document documento = new Document();

        try {
            PdfWriter.getInstance(documento, new FileOutputStream(rutaArchivoPDF));
            documento.open();

            // A) TÍTULO
            Paragraph titulo = new Paragraph("Reporte de Inventario de Biblioteca\n",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE));
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            documento.add(new Paragraph("\n"));

            // B) TABLA DE DATOS
            PdfPTable tabla = new PdfPTable(3); // 3 Columnas
            tabla.setWidthPercentage(100);
            
            tabla.addCell("Título del Libro");
            tabla.addCell("Stock Total");
            tabla.addCell("Porcentaje (%)");

            try (Connection c = new Conexion().conectar()) {
                // Calcular total para porcentajes
                int totalStock = 0;
                Statement stTotal = c.createStatement();
                ResultSet rsTotal = stTotal.executeQuery("SELECT SUM(Stock_Total) as Total FROM Libros WHERE BajaLogica=0");
                if(rsTotal.next()){ totalStock = rsTotal.getInt("Total"); }
                
                // Llenar datos
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery("SELECT Titulo, Stock_Total FROM Libros WHERE BajaLogica = 0");
                DecimalFormat df = new DecimalFormat("#.00");

                while (rs.next()) {
                    int stock = rs.getInt("Stock_Total");
                    double porcentaje = 0;
                    if(totalStock > 0) porcentaje = ((double)stock / totalStock) * 100;

                    tabla.addCell(rs.getString("Titulo"));
                    tabla.addCell(String.valueOf(stock));
                    tabla.addCell(df.format(porcentaje) + "%");
                }
            } catch(Exception ex){
                tabla.addCell("Error BD: " + ex.getMessage());
            }
            documento.add(tabla);

            documento.add(new Paragraph("\n\n")); // Espacio

            // C) IMAGEN DE LA GRÁFICA
            try {
                Image imagen = Image.getInstance(rutaImagenGrafica);
                imagen.setAlignment(Element.ALIGN_CENTER);
                imagen.scaleToFit(500, 400);
                documento.add(imagen);
            } catch (Exception e) {}

            documento.close();
            respuesta = "PDF de Inventario creado en: " + rutaArchivoPDF;

        } catch (Exception e) {
            respuesta = "Error PDF: " + e.getMessage();
        }
    }

    // --- 3. EXCEL ---
    public void crearExcel(String rutaCarpeta) {
        try {
            verificarCarpeta(rutaCarpeta);
            File f = new File(rutaCarpeta + File.separator + "tabla_inventario.xls");
            FileWriter fw = new FileWriter(f, false);

            try (Connection c = new Conexion().conectar()) {
                // Calcular total
                int totalStock = 0;
                Statement stTotal = c.createStatement();
                ResultSet rsTotal = stTotal.executeQuery("SELECT SUM(Stock_Total) as Total FROM Libros WHERE BajaLogica=0");
                if(rsTotal.next()){ totalStock = rsTotal.getInt("Total"); }

                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery("SELECT Titulo, ISBN, Stock_Total FROM Libros WHERE BajaLogica = 0");
                
                DecimalFormat df = new DecimalFormat("#.00");

                fw.write("Titulo\tISBN\tStock\tPorcentaje\n");
                
                while (rs.next()) {
                    int stock = rs.getInt("Stock_Total");
                    double porcentaje = 0;
                    if(totalStock > 0) porcentaje = ((double)stock / totalStock) * 100;

                    fw.write(rs.getString("Titulo") + "\t");
                    fw.write(rs.getString("ISBN") + "\t");
                    fw.write(stock + "\t");
                    fw.write(df.format(porcentaje) + "%\n");
                }
            }
            fw.close();
            respuesta = "Excel de Inventario creado en: " + f.getAbsolutePath();
        } catch (Exception er) {
            respuesta = "Error Excel: " + er.getMessage();
        }
    }
}
