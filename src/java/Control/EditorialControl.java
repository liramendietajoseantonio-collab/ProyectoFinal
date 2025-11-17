/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Control;

import beans.Editorial;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author garri
 */
@WebServlet(name = "Editorial", urlPatterns = {"/Editorial"})
public class EditorialControl extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        String accion = request.getParameter("boton");
    Editorial p = new Editorial();

    switch (accion) {
        
        case "Alta Editorial":
            p.setId(Integer.parseInt(request.getParameter("ID_Editorial")));
            p.setNombre(request.getParameter("nombre"));
            p.setPais(request.getParameter("pais"));
            p.alta();
            break;

        case "Eliminar Editorial": 
            p.setId(Integer.parseInt(request.getParameter("ID_Editorial")));
            p.bajaLogica();
            break;

        case "Consultar Editorial": 
            p.setId(Integer.parseInt(request.getParameter("ID_Editorial")));
            p.consulta();
            break;
            
        // --- INICIO DE LA LÓGICA DE 2 PASOS (CORREGIDA) ---

        // PASO 1: Viene del formulario de búsqueda (modifica_editorial_buscar.html)
        case "Buscar Editorial para Modificar": 
            p.setId(Integer.parseInt(request.getParameter("ID_Editorial")));
            
            // Llama al método que genera el formulario
            p.consultaParaModificar(); 
            break;

        // PASO 2: Viene del formulario generado en el paso anterior
        case "Modificar Editorial": 
            p.setId(Integer.parseInt(request.getParameter("ID_Editorial")));
            p.setNombre(request.getParameter("nombre"));
            p.setPais(request.getParameter("pais"));
            
            // Llama al método que ejecuta el UPDATE
            p.modifica();
            break;
        // --- FIN DE LA LÓGICA DE 2 PASOS ---
            
        default:
            p.setRespuesta("Error: Acción desconocida (" + accion + ").");
            break;
    }
    request.setAttribute("respuesta", p.getRespuesta());
        request.getRequestDispatcher("respuesta.jsp").forward(request, response);
    }
    
    @Override
    public String getServletInfo() {
        return "Controlador de altas, bajas y consultas de persona";
    }
}