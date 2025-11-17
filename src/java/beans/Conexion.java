/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans;

/**
 *
 * @author garri
 */
import java.sql.*;
public class Conexion {
    public static Connection conectar(){
     try {
         Connection cn;
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                cn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=BibliotecaDB;user=sa;password=sasa;");
                return cn;   
     }catch(Exception er){
            return null;
            }      
    }

}
