/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cida.ncetl.servlet;

import gov.usgs.cida.data.grib.GribFileTask;
import gov.usgs.cida.ncetl.spec.task.ArchiveSpec;
import gov.usgs.cida.ncetl.task.NcetlTask;
import gov.usgs.cida.ncetl.utils.DatabaseUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public class TaskRunner extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("task"));
        
        Connection connection = null;
        
        try {
            connection = DatabaseUtil.getConnection();
            Map<String, Object> archiver = ArchiveSpec.getArchiveParams(id, connection);
            NcetlTask task = new GribFileTask();
            task.setRunParams(archiver);
            task.run();
        }
        catch (Exception ex) {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                /* TODO output your page here. You may use following sample code. */
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet TaskRunner</title>");            
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Problem running task</h1>");
                out.println(ex.toString());
                out.println("</body>");
                out.println("</html>");
            } finally {            
                IOUtils.closeQuietly(out);
            }
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException sqlex) {
                    // whatever
                }
            }
        }
        
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TaskRunner</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Everything finished without exception</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            IOUtils.closeQuietly(out);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
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
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
