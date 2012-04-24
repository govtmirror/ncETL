package gov.usgs.cida.data.grib;

import gov.usgs.cida.gdp.coreprocessing.analysis.grid.CRSUtility;
import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.util.List;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;
import ucar.nc2.constants.AxisType;
import ucar.nc2.constants.FeatureType;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.ft.FeatureDataset;
import ucar.nc2.ft.FeatureDatasetFactoryManager;
import ucar.unidata.geoloc.ProjectionImpl;

/**
 *
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public class RollingNetCDFArchive implements Closeable, Flushable {

    private static final Logger log = LoggerFactory.getLogger(
            RollingNetCDFArchive.class);
    
    private NetcdfFileWriteable netcdf;
    private GridDataset gridDs;
    private CoordinateReferenceSystem crs;
    private GridDatatype gdt;
    

    // should be able to open existing file here
    public RollingNetCDFArchive(File rollingFile) throws IOException {
        String fileAsString = rollingFile.getAbsolutePath();
        if (rollingFile.exists() && NetcdfFileWriteable.canOpen(fileAsString)) {
            netcdf = NetcdfFileWriteable.openExisting(fileAsString);
        }
        else {
            netcdf = NetcdfFileWriteable.createNew(fileAsString);
            gridDs = null;
            crs = null;
            gdt = null;
        }
    }
    
    public void define(File gribPrototype, List<String> excludeVars, String unlimitedDim) throws IOException, FactoryException, TransformException {
        FeatureDataset featureDataset = getFeatureDatasetFromFile(gribPrototype);
        gridDs = getGridDatasetFromFeatureDataset(featureDataset);
        gdt = getDatatypeFromDataset(gridDs);
        crs = getCRSFromDatatype(gdt);
        double[] latLonPairs = transformToLatLon(getXCoords(), getYCoords());
        NetcdfDataset srcNc = gridDs.getNetcdfDataset();
        
        
        Dimension unlimited = null;
        for (Dimension dim : srcNc.getDimensions()) {
            if (unlimitedDim.equals(dim.getName())) {
                unlimited = dim;
            }
            else if (excludeVars.contains(dim.getName())) {
                // hold this one out
            }
            else {
                netcdf.addDimension(dim.getName(), dim.getLength());
            }
        }
        if (unlimited != null) {
            netcdf.addUnlimitedDimension(unlimited.getName());
        }
        
        for (Variable var : srcNc.getVariables()) {
            if (!excludeVars.contains(var.getFullName())) {
                Variable newVar = netcdf.addVariable(var.getFullName(), var.getDataType(), var.getDimensionsString());
                for (Attribute varAttr : var.getAttributes()) {
                    netcdf.addVariableAttribute(newVar, varAttr);
                }
            }
        }
        for (Attribute attr : srcNc.getGlobalAttributes()) {
            netcdf.addGlobalAttribute(attr);
        }
        
        netcdf.create();
    }
    
    private void checkDefined() {
        if (netcdf == null || gridDs == null || crs == null || gdt == null) {
            throw new UnsupportedOperationException("Must define prototype before continuing");
        }
    }
    
    private FeatureDataset getFeatureDatasetFromFile(File grib) throws IOException {
        FeatureDataset dataset = FeatureDatasetFactoryManager.open(
                FeatureType.ANY, grib.getAbsolutePath(), null, null);
        return dataset;
    }
    
    private GridDataset getGridDatasetFromFeatureDataset(FeatureDataset dataset) {
        if (dataset != null && dataset instanceof GridDataset) {
            return (GridDataset)dataset;
        }
        throw new UnsupportedOperationException("Dataset must be of type: GRID");
    }
    
    private GridDatatype getDatatypeFromDataset(GridDataset gridDataset) {
        if (gridDataset != null) {
            List<GridDatatype> gdts = gridDataset.getGrids();
            if (!gdts.isEmpty()) {
                return gdts.get(0);
            }
        }
        throw new UnsupportedOperationException("Not a valid gridDataset");
    }
    
    private CoordinateReferenceSystem getCRSFromDatatype(GridDatatype gridDatatype) {
        GridCoordSystem coordinateSystem = gridDatatype.getCoordinateSystem();
        return CRSUtility.getCRSFromGridCoordSystem(coordinateSystem);
    } 
    
    public NetcdfDataset getNetcdfFromGrib(File gribIn) throws IOException {
        FeatureDataset featureDataset = getFeatureDatasetFromFile(gribIn);
        GridDataset gds = getGridDatasetFromFeatureDataset(featureDataset);
        return gds.getNetcdfDataset();
    }

    public GridDataset getDataset() {
        checkDefined();
        return gridDs;
    }

    public CoordinateReferenceSystem getCRS() {
        checkDefined();
        return crs;
    }
    
    public double[][] transformToLatLonNetCDFStyle(double[] xCoords, double[] yCoords) {
        checkDefined();
        double[][] from = new double[2][xCoords.length * yCoords.length];
        for (int x=0; x<xCoords.length; x++) {
            for (int y=0; y<yCoords.length; y++) {
                from[0][x*yCoords.length+y] = xCoords[x];
                from[1][x*yCoords.length+y] = yCoords[y];
            }
        }
        ProjectionImpl projection = gdt.getCoordinateSystem().getProjection();
        double[][] projToLatLon = projection.projToLatLon(from);
        return projToLatLon;
    }
    
    public double[] transformToLatLon(double[] xCoords, double[] yCoords) throws FactoryException, TransformException {
        checkDefined();
        double[] transformArray = new double[2 * xCoords.length * yCoords.length];
        int i=0;
        for (int y=0; y<yCoords.length; y++) {
            for (int x=0; x<xCoords.length; x++) {
                transformArray[i++] = xCoords[x];
                transformArray[i++] = yCoords[y];
            }
        }
        
        MathTransform toWGS84Transform = CRS.findMathTransform(
                crs,
                DefaultGeographicCRS.WGS84,
                true);  // must be true if missing bursa-wolf parameters (akak TOWGS84[...])
        
        toWGS84Transform.transform(transformArray, 0, transformArray, 0, transformArray.length / 2);
        return transformArray;
    }

    public double[] getXCoords() {
        checkDefined();
        NetcdfDataset nc = gridDs.getNetcdfDataset();
        CoordinateAxis axis = nc.findCoordinateAxis(AxisType.GeoX);
        if (axis instanceof CoordinateAxis1D) {
            CoordinateAxis1D x = (CoordinateAxis1D)axis;
            double[] coordValues = x.getCoordValues();
            return coordValues;
        }
        throw new RuntimeException("Must contain 1D GeoX axis type");
    }

    public double[] getYCoords() {
        checkDefined();
        NetcdfDataset nc = gridDs.getNetcdfDataset();
        CoordinateAxis axis = nc.findCoordinateAxis(AxisType.GeoY);
        if (axis instanceof CoordinateAxis1D) {
            CoordinateAxis1D y = (CoordinateAxis1D)axis;
            double[] coordValues = y.getCoordValues();
            return coordValues;
        }
        throw new RuntimeException("Must contain 1D GeoX axis type");
    }

    public void addFile(File gribOrSomething) throws IOException {
        // make GridDataset out of it
        NetcdfDataset ncd = null;
        try {
            ncd = NetcdfDataset.openDataset(gribOrSomething.getAbsolutePath());
            // Read through the gridDataset and write it to netcdf
        }
        finally {
            ncd.close();
        }
    }
    
    @Override
    public void flush() throws IOException {
        netcdf.flush();
    }
    
    @Override
    public void close() throws IOException {
        netcdf.close();
    }
}
