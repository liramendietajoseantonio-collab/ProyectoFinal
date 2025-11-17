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
import beans.Libro;


/**
 *
 * @author linkl
 */
@WebServlet(name = "LibroControl", urlPatterns = {"/LibroControl"})
public class LibroControl extends HttpServlet {

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
            out.println("<title>Servlet LibroControl</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LibroControl at " + request.getContextPath() + "</h1>");
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
    Libro l = new Libro();

    switch (accion) {
        
        case "Alta Libro":
            // --- CÓDIGO ACTUALIZADO ---
            l.setTitulo(request.getParameter("titulo"));
            l.setIsbn(request.getParameter("isbn"));
            l.setStock_total(Integer.parseInt(request.getParameter("stock_total")));
            l.setId_autor(Integer.parseInt(request.getParameter("id_autor")));
            l.setId_editorial(Integer.parseInt(request.getParameter("id_editorial")));
            
            // (Ya no pasamos stock_disponible, el bean 'alta()' se encarga)
            
            l.alta();
            break;
            
        case "Eliminar Libro":
            l.setId_libro(Integer.parseInt(request.getParameter("id_libro")));
            l.bajaLogica();
            break;
            
        case "Consultar Libro":
            l.setId_libro(Integer.parseInt(request.getParameter("id_libro")));
            l.consulta();
            break;
            
        // --- INICIO DE LA LÓGICA DE 2 PASOS (CORREGIDA) ---

        // PASO 1: Viene del formulario de búsqueda (modifica_libro_buscar.html)
        case "Buscar Libro para Modificar": 
            l.setId_libro(Integer.parseInt(request.getParameter("id_libro")));
            l.consultaParaModificar(); 
            break;

        // PASO 2: Viene del formulario generado en el paso anterior
        case "Modificar Libro": 
            // Tu código original estaba bien, pero lo ordenamos
            l.setId_libro(Integer.parseInt(request.getParameter("id_libro")));
            l.setTitulo(request.getParameter("titulo"));
            l.setIsbn(request.getParameter("isbn"));
            l.setId_autor(Integer.parseInt(request.getParameter("id_autor")));
            l.setId_editorial(Integer.parseInt(request.getParameter("id_editorial")));
            l.setStock_total(Integer.parseInt(request.getParameter("stock_total")));
            l.setStock_disponible(Integer.parseInt(request.getParameter("stock_disponible")));
            l.modifica();
            break;
        // --- FIN DE LA LÓGICA DE 2 PASOS ---
            
        default:
            l.setRespuesta("Error: Acción desconocida en LibroControl.");
            break;
    }
    
    request.setAttribute("respuesta", l.getRespuesta());
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
