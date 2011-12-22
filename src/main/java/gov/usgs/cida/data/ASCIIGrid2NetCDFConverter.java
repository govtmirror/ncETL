package gov.usgs.cida.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;

/**
 *
 * @author jwalker
 */
public class ASCIIGrid2NetCDFConverter {
    
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
    private NetcdfFileWriteable nc;
    
    public ASCIIGrid2NetCDFConverter(File gridInfo, File gridData, File netcdfOut) {
        this.gridHeaderInfoFile = new ASCIIGridHeaderFile(gridInfo);
        this.gridDataFile = new ASCIIGridDataFile(gridData);
        VAR = this.gridDataFile.getVariableName();
        try {
            this.nc = NetcdfFileWriteable.createNew(netcdfOut.getAbsolutePath(), false);
        } catch (IOException ex) {
            LOG.error("Couldn't open up netcdf file for writing", ex);
        }
    }
    
    public void convert() throws IOException {
        try {
            gridHeaderInfoFile.readExtents();
            gridDataFile.inspectFile();
        
            // Define dimensions
            Dimension xdim = nc.addDimension(X, gridHeaderInfoFile.getXLength());
            Dimension ydim = nc.addDimension(Y, gridHeaderInfoFile.getYLength());
            Dimension tdim = nc.addDimension(T, gridDataFile.getTimestepCount());

            // Define variables
            Variable xvar = nc.addVariable(X, DataType.FLOAT, new Dimension[]{xdim});
            Variable yvar = nc.addVariable(Y, DataType.FLOAT, new Dimension[]{ydim});
            // is this the right order?
            Variable zvar = nc.addVariable(Z, DataType.FLOAT, new Dimension[]{ydim, xdim});
            Variable tvar = nc.addVariable(T, DataType.INT, new Dimension[]{tdim});
            Variable idvar = nc.addVariable(ID, DataType.INT, new Dimension[]{ydim, xdim});
            Variable datavar = nc.addVariable(VAR, DataType.FLOAT, new Dimension[]{tdim, ydim, xdim});

            // Define attributes
            // TODO allow users to define some extra attributes (std name, units, etc)
            xvar.addAttribute(new Attribute("units", "m"));
            yvar.addAttribute(new Attribute("units", "m"));
            zvar.addAttribute(new Attribute("units", "m"));
            tvar.addAttribute(new Attribute("units", gridDataFile.getTimeUnits()));

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
            while (i < gridDataFile.getTimestepCount()) {
                dataT.set(i, i);
                i++;
            }
            nc.write(T, dataT);

            i = j = 0;
            for(float[] valArr : gridHeaderInfoFile.getZGrid()) {
                for (float val : valArr) {
                    dataZ.set(i, j, val);
                    j++;
                }
                i++;
            }
            nc.write(Z, new int[2], dataZ);

            i = j = 0;
            for (int[] valArr : gridHeaderInfoFile.getGridIds()) {
                for (int val : valArr) {
                    dataID.set(i, j, val);
                    j++;
                }
                i++;
            }
            nc.write(ID, new int[2], dataID);

            int[] origins = new int[3];
            gridDataFile.openForReading(gridHeaderInfoFile.getXLength());
            // i is the time dim, don't need to keep track I think
            j = k = 0;
            while (gridDataFile.readNextLine()) {
                while (j < gridHeaderInfoFile.getYLength()) {
                    while (gridDataFile.hasMoreStrides()) {
                        float[] stride = gridDataFile.readTimestepByStride();
                        for (float val : stride) {
                            dataData.set(0, j, k, val);
                        }
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
            gridDataFile.closeForReading();
            nc.close();
        }
    }
    
}
