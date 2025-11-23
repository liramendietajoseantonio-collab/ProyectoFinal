/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Control;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import beans.ReportesMultas;
import beans.ReportesLibros;

/**
 *
 * @author linkl
 */
@WebServlet(name = "ReporteControl", urlPatterns = {"/ReporteControl"})
public class ReporteControl extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ReporteControl</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ReporteControl at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("boton");
        String mensaje = "";
        
        // 1. OBTENER LA RUTA RELATIVA
    // Esto busca la carpeta "Documentos" dentro de tu carpeta "web"
    // Sin importar en qué computadora estés.
    String rutaCarpeta = getServletContext().getRealPath("/Documentos");
    
    // 2. Instanciamos los beans
    beans.ReportesMultas rMultas = new beans.ReportesMultas();
    // beans.ReporteLibros rLibros = new beans.ReporteLibros(); (Cuando toque editar este)

    switch (accion) {
        // --- MULTAS ---
        case "PDF Multas":
            // 3. PASAMOS LA RUTA COMO PARÁMETRO
            rMultas.crearPdf(rutaCarpeta);
            mensaje = rMultas.getRespuesta();
            break;
            
        case "Excel Multas":
            // 3. PASAMOS LA RUTA COMO PARÁMETRO
            rMultas.crearExcel(rutaCarpeta);
            mensaje = rMultas.getRespuesta();
            break;
            
        case "PDF Inventario":
            ReportesLibros rLibros = new ReportesLibros();
            rLibros.crearPdf(rutaCarpeta); // Pasamos la ruta
            mensaje = rLibros.getRespuesta();
            break;
            
        case "Excel Inventario":
            ReportesLibros rLibrosExcel = new ReportesLibros();
            rLibrosExcel.crearExcel(rutaCarpeta); // Pasamos la ruta
            mensaje = rLibrosExcel.getRespuesta();
            break;
            
        default:
            mensaje = "Acción no válida";
            break;
    }
    
    request.setAttribute("respuesta", mensaje);
    request.getRequestDispatcher("respuesta.jsp").forward(request, response);
}

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
