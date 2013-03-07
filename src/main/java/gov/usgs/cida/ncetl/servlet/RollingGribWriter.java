//package gov.usgs.cida.ncetl.servlet;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.io.Closeables;
//import com.google.common.io.Flushables;
//import gov.usgs.cida.data.grib.RollingNetCDFArchive;
//import java.io.File;
//import java.io.FileFilter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.apache.commons.io.filefilter.RegexFileFilter;
//import org.opengis.referencing.FactoryException;
//import org.opengis.referencing.operation.TransformException;
//import ucar.ma2.InvalidRangeException;
//
///**
// *
// * @author Jordan Walker <jiwalker@usgs.gov>
// *
//public class RollingGribWriter extends HttpServlet {
//
//    private List<String> rfcList = null;
//    
//    @Override
//    public void init() throws ServletException {
//        super.init();
//        rfcList = Lists.newArrayList();
//        rfcList.add("105");
//        rfcList.add("150");
//        rfcList.add("152");
//        rfcList.add("153");
//        rfcList.add("154");
//        rfcList.add("155");
//        rfcList.add("156");
//        rfcList.add("157");
//        rfcList.add("158");
//        rfcList.add("159");
//        rfcList.add("160");
//        rfcList.add("161");
//        rfcList.add("162");
//    }
//
//    /**
//     * Processes requests for both HTTP
//     * <code>GET</code> and
//     * <code>POST</code> methods.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    protected void processRequest(HttpServletRequest request,
//                                  HttpServletResponse response)
//            throws ServletException, IOException {
//        String inputDir = request.getParameter("inputDir");
//        String outputDir = request.getParameter("outputDir");
//        String completeDir = request.getParameter("completeDir");
//        if (inputDir == null || outputDir == null) {
//            response.sendError(400, "Must specify input and output directories");
//            return;
//        }
//        
//        File inputDirFile = new File(inputDir);
//        File outputDirFile = new File(outputDir);
//        if (!inputDirFile.exists() || !inputDirFile.isDirectory() ||
//            !outputDirFile.exists() || !outputDirFile.isDirectory()) {
//            response.sendError(400, "Input and output directories must exist");
//            return;
//        }
//        
//        for (String rfcCode : rfcList) {
//            Pattern rfcPattern = Pattern.compile("QPE\\.(\\d{4})(\\d{2})\\d{2}(\\.\\d{2})?\\.009\\." + rfcCode + "$");
//            FileFilter filter = new RegexFileFilter(rfcPattern);
//            File[] listFiles = inputDirFile.listFiles(filter);
//            RollingNetCDFArchive rollingNetCDF = null;
//            try {
//                String currentMonth = null;
//                Arrays.sort(listFiles);
//                for (File file : listFiles) {
//                    Matcher rfcMatcher = rfcPattern.matcher(file.getName());
//                    if (rfcMatcher.matches()) {
//                        String year = rfcMatcher.group(1);
//                        String month = rfcMatcher.group(2);
//                        if (!month.equals(currentMonth)) {
//                            currentMonth = month;
//                            if (rollingNetCDF != null) {
//                                Flushables.flushQuietly(rollingNetCDF);
//                                //rollingNetCDF.finish();
//                                Closeables.closeQuietly(rollingNetCDF);
//                            }
//                            rollingNetCDF = setupNetCDF(outputDirFile, year, month, rfcCode);
//                            try {
//                                rollingNetCDF.define(file);
//                            }
//                            catch (FactoryException ex) {
//                                response.sendError(400, "Error getting CRS from prototype grib\n" + ex.getMessage());
//                                return;
//                            }
//                            catch (TransformException ex) {
//                                response.sendError(400, "Error transforming to lat lon\n" + ex.getMessage());
//                                return;
//                            }
//                            catch (InvalidRangeException ex) {
//                                response.sendError(400, "Invalid range when defining netcdf\n" + ex.getMessage());
//                                return;
//                            }
//                        }
//                        try {
//                            rollingNetCDF.addFile(file);
//                        }
//                        catch (Exception ex) {
//                            response.sendError(400, "Exception occured adding new file, " + file + ex.getMessage());
//                            return;
//                        }
//                    }
//                    if (completeDir != null) {
//                        file.renameTo(new File(completeDir + File.separator + file.getName()));
//                    }
//                }
//            }
//            finally {
//                Closeables.closeQuietly(rollingNetCDF);
//            }
//        }
//        
//        response.setContentType("text/html;charset=UTF-8");
//        PrintWriter out = response.getWriter();
//        try {
//          
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet ASCIIConverterServlet</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<h1>Complete!</h1>");
//            out.println("</body>");
//            out.println("</html>");
//             
//        } finally {
//            out.close();
//        }
//    }
//    
//    private RollingNetCDFArchive setupNetCDF(File outputDir, String year, String month, String rfcCode) throws IOException {
//        String ncFilename = outputDir.getCanonicalPath() + File.separatorChar + "QPE." + year + "." + month + "." + rfcCode + ".nc";
//        RollingNetCDFArchive roll = new RollingNetCDFArchive(new File(ncFilename));
//        roll.setExcludeList(RollingNetCDFArchive.DIM, "time1");
//        roll.setExcludeList(RollingNetCDFArchive.VAR, "time1", "time_bounds", "time1_bounds", "Total_precipitation_surface_6_Hour_Accumulation",
//                                "Total_precipitation_surface_Mixed_intervals_Accumulation", "Total_precipitation_surface_1_Hour_Accumulation");
//        roll.setExcludeList(RollingNetCDFArchive.XY, "PolarStereographic_Projection", "x", "y");
//        roll.setGridMapping("Latitude_Longitude");
//        roll.setUnlimitedDimension("time", "hours since 2000-01-01 00:00:00");
//        Map<String, String> varMap = Maps.newHashMap();
//        varMap.put("1-hour_Quantitative_Precip_Estimate_surface_1_Hour_Accumulation","1-hour_Quantitative_Precip_Estimate_surface_1_Hour_Accumulation");
//        varMap.put("Total_precipitation_surface_Mixed_intervals_Accumulation", "1-hour_Quantitative_Precip_Estimate_surface_1_Hour_Accumulation");
//        varMap.put("Total_precipitation_surface_1_Hour_Accumulation","1-hour_Quantitative_Precip_Estimate_surface_1_Hour_Accumulation");
//        roll.setGridVariables(varMap);
//        return roll;
//    }
//
//    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
//    /**
//     * Handles the HTTP
//     * <code>GET</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doGet(HttpServletRequest request,
//                         HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//
//    /**
//     * Handles the HTTP
//     * <code>POST</code> method.
//     *
//     * @param request servlet request
//     * @param response servlet response
//     * @throws ServletException if a servlet-specific error occurs
//     * @throws IOException if an I/O error occurs
//     */
//    @Override
//    protected void doPost(HttpServletRequest request,
//                          HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }
//
//    /**
//     * Returns a short description of the servlet.
//     *
//     * @return a String containing servlet description
//     */
//    @Override
//    public String getServletInfo() {
//        return "Short description";
//    }// </editor-fold>
//}