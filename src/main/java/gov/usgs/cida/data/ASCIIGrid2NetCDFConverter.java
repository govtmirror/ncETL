package gov.usgs.cida.data;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayLong;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;

/**
 *
 * @author jwalker
 */
public class ASCIIGrid2NetCDFConverter {
    public static final String ALB = "albers_conical_equal_area";
    
    private static final Logger LOG = LoggerFactory.getLogger(ASCIIGrid2NetCDFConverter.class);
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "elev";
    private static final String T = "time";
    private static final String ID = "GRIDid";
    private final String VAR;
    public static final RuntimeException rtex = new RuntimeException("Exited execution for some reason, check logs");
    
    private ASCIIGridHeaderFile gridHeaderInfoFile;
    private ASCIIGridDataFile gridDataFile;
    private File directory;
    
    public ASCIIGrid2NetCDFConverter(File gridInfo, File gridData, File outputDirectory) {
        this.gridHeaderInfoFile = new ASCIIGridHeaderFile(gridInfo);
        this.gridDataFile = new ASCIIGridDataFile(gridData);
        VAR = this.gridDataFile.getVariableName();
        this.directory = outputDirectory;
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }
        else if (!this.directory.isDirectory()) {
            LOG.error("Specified directory is not a directory");
            throw rtex;
        }
    }
    
    public void convert() throws IOException {

        gridHeaderInfoFile.readExtents();
        gridDataFile.inspectFile();
        gridDataFile.openForReading(gridHeaderInfoFile.getXLength());

        Map<Integer, List<Long>> timestepIndices = gridDataFile.getTimestepIndices();

        for (int year : timestepIndices.keySet()) {

            String fileWithPath = directory.getAbsolutePath() + File.separator +
                    VAR + "." + year + ".nc";
            NetcdfFileWriteable nc = NetcdfFileWriteable.createNew(fileWithPath, false);
            List<Long> timesteps = timestepIndices.get(year);

            try {
                nc.setLargeFile(true);
                
                // Define dimensions
                Dimension xdim = nc.addDimension(X, gridHeaderInfoFile.getXLength());
                Dimension ydim = nc.addDimension(Y, gridHeaderInfoFile.getYLength());
                Dimension tdim = nc.addDimension(T, timesteps.size());

                // Define variables
                Variable xvar = nc.addVariable(X, DataType.FLOAT, new Dimension[]{xdim});
                Variable yvar = nc.addVariable(Y, DataType.FLOAT, new Dimension[]{ydim});
                // is this the right order?
                Variable zvar = nc.addVariable(Z, DataType.FLOAT, new Dimension[]{ydim, xdim});
                Variable tvar = nc.addVariable(T, DataType.INT, new Dimension[]{tdim});
                Variable idvar = nc.addVariable(ID, DataType.INT, new Dimension[]{ydim, xdim});
                Variable datavar = nc.addVariable(VAR, DataType.FLOAT, new Dimension[]{tdim, ydim, xdim});
                // Also have to do grid mapping
                Variable albvar = nc.addVariable(ALB, DataType.INT, new Dimension[0]);

                // Define attributes
                // TODO allow users to define some extra attributes (std name, units, etc)
                nc.addGlobalAttribute(new Attribute("Conventions", "CF-1.6"));
                xvar.addAttribute(new Attribute("units", "m"));
                xvar.addAttribute(new Attribute("standard_name", "projection_x_coordinate"));
                yvar.addAttribute(new Attribute("units", "m"));
                yvar.addAttribute(new Attribute("standard_name", "projection_y_coordinate"));
                zvar.addAttribute(new Attribute("units", "m"));
                tvar.addAttribute(new Attribute("units", gridDataFile.getTimeUnits()));
                datavar.addAttribute(new Attribute("coordinates", "y x"));
                datavar.addAttribute(new Attribute("grid_mapping", ALB));
                datavar.addAttribute(new Attribute("coordsys", "Albers Conical Equal Area"));
                albvar.addAttribute(new Attribute("grid_mapping_name", ALB));
                List<Float> stdParallels = Lists.newArrayList(29.5f, 45.5f);
                albvar.addAttribute(new Attribute("standard_parallel", stdParallels));
                albvar.addAttribute(new Attribute("longitude_of_central_meridian", -96.0));
                albvar.addAttribute(new Attribute("latitude_of_projection_origin", 23.0));
                albvar.addAttribute(new Attribute("units", "m"));
                albvar.addAttribute(new Attribute("false_easting", 0));
                albvar.addAttribute(new Attribute("false_northing", 0));
                albvar.addAttribute(new Attribute("semi_major_axis", 6378137.0));
                albvar.addAttribute(new Attribute("inverse_flattening", 298.257222101));

                nc.create();

                ArrayFloat.D1 dataX = new ArrayFloat.D1(xdim.getLength());
                ArrayFloat.D1 dataY = new ArrayFloat.D1(ydim.getLength());
                ArrayInt.D1 dataT = new ArrayInt.D1(tdim.getLength());

                ArrayFloat.D2 dataZ = new ArrayFloat.D2(ydim.getLength(), xdim.getLength());
                ArrayInt.D2 dataID = new ArrayInt.D2(ydim.getLength(), xdim.getLength());

                // Only going to write one timeslice at a time
                ArrayFloat.D3 dataData = new ArrayFloat.D3(1, ydim.getLength(), xdim.getLength());

                int i,j,k;
                i = 0;
                for (float val : gridHeaderInfoFile.getXDim()) {
                    dataX.set(i, val);
                    i++;
                }
                nc.write(X, dataX);

                i = 0;
                for (float val : gridHeaderInfoFile.getYDim()) {
                    dataY.set(i, val);
                    i++;
                }
                nc.write(Y, dataY);

                i = 0;
                for (long val : timesteps) {
                    // casting dates to int (dangerous!!)
                    dataT.set(i, (int)val);
                    i++;
                }
                nc.write(T, dataT);

                i = 0;
                for(float[] valArr : gridHeaderInfoFile.getZGrid()) {
                    j = 0;
                    for (float val : valArr) {
                        dataZ.set(i, j, val);
                        j++;
                    }
                    i++;
                }
                nc.write(Z, new int[2], dataZ);

                i = 0;
                for (int[] valArr : gridHeaderInfoFile.getGridIds()) {
                    j = 0;
                    for (int val : valArr) {
                        dataID.set(i, j, val);
                        j++;
                    }
                    i++;
                }
                nc.write(ID, new int[2], dataID);

                int[] origins = new int[3];
                
                // i is the time dim, don't need to keep track I think
                for (long day : timesteps) {
                    gridDataFile.readNextLine();
                    j = 0;
                    while (gridDataFile.hasMoreStrides()) {
                        float[] stride = gridDataFile.readTimestepByStride();
                        k = 0;
                        for (float val : stride) {
                            dataData.set(0, j, k, val);
                            k++;
                        }
                        j++;
                    }

                    nc.write(VAR, origins, dataData);
                    origins[0]++;
                }
            }
            catch (FileNotFoundException ex) {
                LOG.error("file not found", ex);
                throw rtex;
            }
            catch (InvalidRangeException ex) {
                LOG.error("tried to write too much data to variable");
                throw rtex;
            }
            finally {
                nc.close();
            }
        }
        gridDataFile.closeForReading();
    }
}