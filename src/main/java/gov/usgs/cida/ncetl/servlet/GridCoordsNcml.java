package gov.usgs.cida.ncetl.servlet;

import com.google.common.io.Closeables;
import gov.usgs.cida.data.grib.GribUtils;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.ft.FeatureDataset;

/**
 *
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public class GridCoordsNcml extends HttpServlet {

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
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {
        String varParam = request.getParameter("dataVariables");
        String typeParam = request.getParameter("types");
        String gribFile = request.getParameter("file");
        if (varParam == null || typeParam == null || gribFile == null) {
            response.sendError(400, "Must specify file, data variables, and types");
            return;
        }
        String[] variables = varParam.split(",");
        String[] types = typeParam.split(",");
        if (variables.length != types.length) {
            response.sendError(400, "variables must match types in length");
            return;
        }
        
        File gribPrototype = new File(gribFile);
        FeatureDataset featureDataset = GribUtils.getFeatureDatasetFromFile(gribPrototype);
        GridDataset gridDs = GribUtils.getGridDatasetFromFeatureDataset(featureDataset);
        GridDatatype gdt = GribUtils.getDatatypeFromDataset(gridDs);
        double[] xCoords = GribUtils.getXCoords(gridDs);
        double[] yCoords = GribUtils.getYCoords(gridDs);
        double[][] ncCoords = GribUtils.transformToLatLonNetCDFStyle(xCoords, yCoords, gdt);
        
        response.setContentType("application/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<netcdf xmlns=\"http://www.unidata.ucar.edu/namespaces/netcdf/ncml-2.2\">");
            out.println("<remove name=\"x\" type=\"variable\" />");
            out.println("<remove name=\"y\" type=\"variable\" />");
            out.println("<remove name=\"Polar_Stereographic\" type=\"variable\" />");
            out.println("<attribute name=\"Conventions\" value=\"CF-1.6\" />");
            
            out.println("<variable name=\"Latitude_Longitude\" type=\"int\">");
            out.println("<attribute name=\"grid_mapping_name\" value=\"latitude_longitude\" />");
            out.println("<attribute name=\"semi_major_axis\" value=\"6378137.0\" />");
            out.println("<attribute name=\"semi_minor_axis\" value=\"6356752.314245\" />");
            out.println("<attribute name=\"longitude_of_prime_meridian\" value=\"0\" />");
            out.println("</variable>");
            
            for (int i=0; i<variables.length; i++) {
                out.println("<variable name=\"" + variables[i] + "\" type=\"" + types[i] + "\">");
                out.println("<attribute name=\"grid_mapping\" value=\"Latitude_Longitude\" />");
                out.println("<attribute name=\"coordinates\" value=\"lon lat\" />");
                // should probably try to get standard name in here
                out.println("</variable>");
            }
            
            out.println("<variable name=\"lat\" shape=\"y x\" type=\"double\">");
            out.println("<attribute name=\"units\" value=\"degrees_north\" />");
            out.println("<attribute name=\"long_name\" value=\"Latitude\" />");
            out.println("<attribute name=\"standard_name\" value=\"latitude\" />");
            out.println("<values>");
            Double[] lats = ArrayUtils.toObject(ncCoords[0]);
            out.println(StringUtils.join(lats, ' '));
            out.println("</values>");
            out.println("</variable>");
            
            out.println("<variable name=\"lon\" shape=\"y x\" type=\"double\">");
            out.println("<attribute name=\"units\" value=\"degrees_east\" />");
            out.println("<attribute name=\"long_name\" value=\"Longitude\" />");
            out.println("<attribute name=\"standard_name\" value=\"longitude\" />");
            out.println("<values>");
            Double[] lons = ArrayUtils.toObject(ncCoords[1]);
            out.println(StringUtils.join(lons, ' '));
            out.println("</values>");
            out.println("</variable>");
            
            out.println("</netcdf>");
        }
        finally {
            Closeables.closeQuietly(out);
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
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
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
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
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
