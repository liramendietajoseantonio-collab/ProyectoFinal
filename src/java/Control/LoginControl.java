/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Control;

import beans.Conexion;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 *
 * @author linkl
 */
@WebServlet(name = "LoginControl", urlPatterns = {"/LoginControl"})
public class LoginControl extends HttpServlet {

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
            out.println("<title>Servlet LoginControl</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginControl at " + request.getContextPath() + "</h1>");
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
        String mat = request.getParameter("matricula");
        String pass = request.getParameter("password");
        
        try (Connection cn = new Conexion().conectar()) {
            
            // 1. Consulta SQL (La que dio la maestra)
            String sql = "SELECT Tipo FROM Personas WHERE Matricula = ? AND Password = ? AND BajaLogica = 0";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, mat);
            ps.setString(2, pass);
            
            ResultSet rs = ps.executeQuery();
            
             if (rs.next()) {
             String tipo = rs.getString("Tipo");
    
            // AGREGAR ESTO: Crear sesión
           HttpSession session = request.getSession();
           session.setAttribute("matricula", mat);
           session.setAttribute("tipo", tipo);
    session.setAttribute("logueado", true);
    
    // 3. Lógica de redirección...
    if (tipo.equals("Alumno") || tipo.equals("Profesor")) {

                    // Alumnos y Profesores van a Usuarios.html
                    response.sendRedirect("Usuarios.html");
                    
                } else if (tipo.equals("Bibliotecario")) {
                    // Bibliotecario va a su página
                    response.sendRedirect("Bibliotecario.html");
                    
                } else if (tipo.equals("Admin")) {
                    // Admin va a su página
                    response.sendRedirect("Administrador.html");
                    
                } else {
                    // Por si acaso hay un rol raro
                    response.sendRedirect("index.html");
                }
                
            } else {
                // Si no existe o contraseña mal
                response.sendRedirect("index.html"); // O una página de error
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
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
