/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Control;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import beans.Ejemplar;

/**
 *
 * @author garri
 */
@WebServlet(name = "EjemplarControl", urlPatterns = {"/EjemplarControl"})
public class EjemplarControl extends HttpServlet {

    
 public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        String accion = request.getParameter("boton");
    Ejemplar p = new Ejemplar();

    switch (accion) {
        
        case "Alta Ejemplar":
            p.setId_libro(Integer.parseInt(request.getParameter("id_libro")));
            p.setNumero_copia(Integer.parseInt(request.getParameter("numero_copia")));
            p.alta();
            break;

        case "Eliminar Ejemplar":
            p.setId_ejemplar(Integer.parseInt(request.getParameter("id_ejemplar")));
            p.bajaLogica();
            break;

        case "Consultar Ejemplar":
            p.setId_ejemplar(Integer.parseInt(request.getParameter("id_ejemplar")));
            p.consulta();
            break;
            
        // --- INICIO DE LA LÓGICA DE 2 PASOS ---

        case "Buscar Ejemplar para Modificar":
            p.setId_ejemplar(Integer.parseInt(request.getParameter("id_ejemplar")));
            p.consultaParaModificar(); 
            break;

        case "Modificar Ejemplar":
            p.setId_ejemplar(Integer.parseInt(request.getParameter("id_ejemplar")));
            p.setId_libro(Integer.parseInt(request.getParameter("id_libro")));
            p.setNumero_copia(Integer.parseInt(request.getParameter("numero_copia")));
            p.setEstado(request.getParameter("estado"));
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
        return "Controlador de altas, bajas y consultas de ejemplar";
    }
}
