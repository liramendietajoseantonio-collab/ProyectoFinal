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
import beans.Persona;

@WebServlet(name = "control", urlPatterns = {"/Control"})
public class control extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("boton");
        
        // 2. Creamos el bean
        Persona p = new Persona();
        
        // 3. Usamos un switch para decidir qué hacer
        switch (accion) {
            
            case "Alta": // Asegúrate que tu botón de alta diga "Alta Alumno"
        p.setMatricula(request.getParameter("matricula"));
        p.setNombre(request.getParameter("nombre"));
        p.setApellido(request.getParameter("apellido"));
        p.setTipo(request.getParameter("tipo"));
        
        // CAPTURAR LA CONTRASEÑA
        p.setPassword(request.getParameter("password")); 
        
        p.alta();
        break;

            case "Eliminar Persona": // Botón de baja
                p.setMatricula(request.getParameter("matricula"));
                p.bajaLogica();
                break;

            case "Consultar Persona": // Botón de consulta
                p.setMatricula(request.getParameter("matricula"));
                p.consulta();
                break;

            // --- INICIO DEL FLUJO DE MODIFICACIÓN (2 PASOS) ---
            
            // PASO 1: Viene de tu formulario de búsqueda (modifica_buscar.html)
            case "Buscar para Modificar":
                // 1. Solo obtiene la matrícula
                p.setMatricula(request.getParameter("matricula"));
                
                // 2. Llama al método que GENERA EL FORMULARIO HTML
                p.consultaParaModificar(); 
                break;

            // PASO 2: Viene del formulario generado por el paso anterior
            case "Modificar Alumno": 
                // 1. Obtiene TODOS los datos (incluido el oculto)
                p.setMatricula(request.getParameter("matricula")); // Campo oculto
                p.setNombre(request.getParameter("nombre"));
                p.setApellido(request.getParameter("apellido"));
                p.setTipo(request.getParameter("tipo"));
                
                // 2. Llama al método que ejecuta el UPDATE
                p.modifica();
                break;
                
            // --- FIN DEL FLUJO DE MODIFICACIÓN ---
                
            default:
                p.setRespuesta("Error: Acción desconocida (" + accion + ").");
                break;
        }
        
        // 9. ENVIAR A LA RESPUESTA
        request.setAttribute("respuesta", p.getRespuesta());
        request.getRequestDispatcher("respuesta.jsp").forward(request, response);
    }
    
    @Override
    public String getServletInfo() {
        return "Controlador de altas, bajas y consultas de persona";
    }
}
