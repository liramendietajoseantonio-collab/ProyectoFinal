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
import beans.Autores;

/**
 *
 * @author garri
 */
@WebServlet(name = "AutoresControl", urlPatterns = {"/AutoresControl"})
public class AutoresControl extends HttpServlet {

    
 public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
        String accion = request.getParameter("boton");
    Autores p = new Autores();

    switch (accion) {
        
        case "Alta Autor":
            p.setId(Integer.parseInt(request.getParameter("ID_Autor")));
            p.setNombre(request.getParameter("nombre"));
            p.setApellido(request.getParameter("apellido"));
            p.setNacionalidad(request.getParameter("nacionalidad"));
            p.alta();
            break;

        case "Eliminar Autor": // Este 'value' debe estar en tu botón de baja
            p.setId(Integer.parseInt(request.getParameter("ID_Autor")));
            p.bajaLogica();
            break;

        case "Consultar Autor": // Este 'value' debe estar en tu botón de consulta
            p.setId(Integer.parseInt(request.getParameter("ID_Autor")));
            p.consulta();
            break;
            
        // --- INICIO DE LA LÓGICA DE 2 PASOS (CORREGIDA) ---

        // PASO 1: Viene del formulario de búsqueda (modifica_autor_buscar.html)
        case "Buscar Autor para Modificar": // <--- ESTE 'case' ES NUEVO
            p.setId(Integer.parseInt(request.getParameter("ID_Autor")));
            
            // Llama al método que genera el formulario
            p.consultaParaModificar(); 
            break;

        // PASO 2: Viene del formulario generado en el paso anterior
        case "Modificar Autor": // <--- Este 'case' ya lo tenías, y ahora SÍ funciona
            // Obtiene TODOS los datos (incluido el oculto)
            p.setId(Integer.parseInt(request.getParameter("ID_Autor")));
            p.setNombre(request.getParameter("nombre"));
            p.setApellido(request.getParameter("apellido"));
            p.setNacionalidad(request.getParameter("nacionalidad"));
            
            // Llama al método que ejecuta el UPDATE
            p.modifica();
            break;
        // --- FIN DE LA LÓGICA DE 2 PASOS ---
            
        default:
            p.setRespuesta("Error: Acción desconocida (" + accion + ").");
            break;
    }
    
    // Envía la respuesta (sea un mensaje o un formulario HTML) al JSP
    request.setAttribute("respuesta", p.getRespuesta());
    request.getRequestDispatcher("respuesta.jsp").forward(request, response);
    }
    
    @Override
    public String getServletInfo() {
        return "Controlador de altas, bajas y consultas de persona";
    }
}
