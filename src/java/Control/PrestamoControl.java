/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Control;

import beans.Prestamo;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;

/**
 *
 * @author linkl
 */
@WebServlet(name = "PrestamoControl", urlPatterns = {"/PrestamoControl"})
public class PrestamoControl extends HttpServlet {

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
            out.println("<title>Servlet PrestamoControl</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PrestamoControl at " + request.getContextPath() + "</h1>");
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
        
        
        Prestamo p = new Prestamo();

        
        switch (accion) {

            
            case "Registrar Prestamo":
                // --- CÓDIGO CORREGIDO (Volvemos a añadir la fecha) ---
                
                p.setMatricula(request.getParameter("matricula"));
                p.setId_libro(Integer.parseInt(request.getParameter("id_libro")));
                
                // 1. Obtener el String de la fecha del formulario
                String fechaStr = request.getParameter("fecha_prestamo");
                
                // 2. Convertir el String a java.sql.Date
                java.sql.Date fechaSQL = java.sql.Date.valueOf(fechaStr);
                
                // 3. Pasarla al bean
                p.setFecha_prestamo(fechaSQL);
                
                p.alta();
                break;


           case "Devolver Prestamo":
           p.setId_prestamo(Integer.parseInt(request.getParameter("id_prestamo")));
           p.modifica();
           break;
                
          
                
           case "Baja Prestamo":
           p.setId_prestamo(Integer.parseInt(request.getParameter("id_prestamo")));
           p.bajaLogica();
          break;


          case "Consultar Prestamo":
          p.setId_prestamo(Integer.parseInt(request.getParameter("id_prestamo")));
          p.consulta();
          break;
               
                
            default:
                p.setRespuesta("Error: Acción desconocida en PrestamoControl.");
                break;
        }

        // 11. Envía la 'respuesta' (generada por el bean) al 'respuesta.jsp'
        request.setAttribute("respuesta", p.getRespuesta());
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
