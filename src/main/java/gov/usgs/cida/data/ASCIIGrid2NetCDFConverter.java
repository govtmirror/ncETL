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
    public static final RuntimeException rtex = new RuntimeException("Exited execution for some reason, check logs");
    
    private ASCIIGridHeaderFile gridHeaderInfoFile;
    private ASCIIGridDataFile gridDataFile;
    private NetcdfFileWriteable nc;
    
    public ASCIIGrid2NetCDFConverter(File gridInfo, File gridData, File netcdfOut) {
        this.gridHeaderInfoFile = new ASCIIGridHeaderFile(gridInfo);
        this.gridDataFile = new ASCIIGridDataFile(gridData);
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
        Variable datavar = nc.addVariable(gridDataFile.getVariableName(), DataType.FLOAT, 
                new Dimension[]{tdim, ydim, xdim});
        
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
        
        ArrayFloat.D3 dataData = new ArrayFloat.D3(tdim.getLength(), ydim.getLength(), xdim.getLength());
        
        int i = 0;
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
        
        
        // Write the rest of the junk
        
        int stride = gridHeaderInfoFile.getXLength();
        
        //gridDataFile.readTimestepByStride(stride);
        
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
    
}
