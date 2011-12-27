package gov.usgs.cida.ncetl.servlet;

import gov.usgs.cida.data.ASCIIGrid2NetCDFConverter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jwalker
 */
public class ASCIIConverterServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String header = request.getParameter("header");
        String data = request.getParameter("data");
        String netcdf = request.getParameter("outputDir");
        
        if (header == null || data == null || netcdf == null) {
            response.sendError(400, "Must specify 'header' for GRID_HEADERinfo file,"
                    + " 'data' for grid data file, and 'outputDir' for output directory");
            return;
        }
        
        File headerFile = new File(header);
        File dataFile = new File(data);
        File netcdfFile = new File(netcdf);
        
        ASCIIGrid2NetCDFConverter converter = new ASCIIGrid2NetCDFConverter(headerFile, dataFile, netcdfFile);
        converter.convert();
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
          
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ASCIIConverterServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Complete!</h1>");
            out.println("</body>");
            out.println("</html>");
             
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
