package gov.usgs.cida.data.grib;

import gov.usgs.cida.gdp.coreprocessing.analysis.grid.CRSUtility;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;
import ucar.nc2.constants.AxisType;
import ucar.nc2.constants.FeatureType;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dataset.CoordinateAxis2D;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.ft.FeatureDataset;
import ucar.nc2.ft.FeatureDatasetFactoryManager;
import ucar.unidata.geoloc.Projection;
import ucar.unidata.geoloc.ProjectionImpl;

/**
 *
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public class RollingNetCDFArchive {

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
        }
    }
    
    public void define(File gribPrototype) throws IOException, FactoryException, TransformException {
        FeatureDataset dataset = FeatureDatasetFactoryManager.open(
                FeatureType.ANY, gribPrototype.getAbsolutePath(), null, null);
        if (dataset != null && dataset instanceof GridDataset) {
            // grib should be grid
            gridDs = (GridDataset) dataset;
            List<GridDatatype> gdts = gridDs.getGrids();
            if (!gdts.isEmpty()) {
                gdt = gdts.get(0);
                GridCoordSystem coordinateSystem = gdt.getCoordinateSystem();
                crs = CRSUtility.getCRSFromGridCoordSystem(coordinateSystem);
            }
        }
        double[] latLonPairs = transformToLatLon(getXCoords(), getYCoords());
        NetcdfDataset srcNc = gridDs.getNetcdfDataset();
        List<Variable> variables = srcNc.getVariables();
        List<Attribute> globalAttributes = srcNc.getGlobalAttributes();
        
        
    }
    
    public NetcdfDataset netcdfFromPrototype(File prototype) {
        return null;
    }

    public GridDataset getDataset() {
        return gridDs;
    }

    public CoordinateReferenceSystem getCRS() {
        return crs;
    }
    
    public double[][] transformToLatLonNetCDFStyle(double[] xCoords, double[] yCoords) {
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
        if (crs == null) {
            throw new TransformException("Must have source crs to perform transform");
        }
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
}
