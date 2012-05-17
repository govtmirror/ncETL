package gov.usgs.cida.data.grib;

import gov.usgs.cida.gdp.coreprocessing.analysis.grid.CRSUtility;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
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
public class GribUtils {
    
    public static double[][] transformToLatLonNetCDFStyle(double[] xCoords, double[] yCoords, GridDatatype gdt) {
        double[][] from = new double[2][xCoords.length * yCoords.length];
        for (int y=0; y<yCoords.length; y++) {
            for (int x=0; x<xCoords.length; x++) {
                from[0][y*xCoords.length+x] = xCoords[x];
                from[1][y*xCoords.length+x] = yCoords[y];
            }
        }
        ProjectionImpl projection = gdt.getCoordinateSystem().getProjection();
        double[][] projToLatLon = projection.projToLatLon(from);
        return projToLatLon;
    }
    
    public static double[] getXCoords(GridDataset gridDs) {
        NetcdfDataset nc = gridDs.getNetcdfDataset();
        CoordinateAxis axis = nc.findCoordinateAxis(AxisType.GeoX);
        if (axis instanceof CoordinateAxis1D) {
            CoordinateAxis1D x = (CoordinateAxis1D)axis;
            double[] coordValues = x.getCoordValues();
            return coordValues;
        }
        throw new RuntimeException("Must contain 1D GeoX axis type");
    }

    public static double[] getYCoords(GridDataset gridDs) {
        NetcdfDataset nc = gridDs.getNetcdfDataset();
        CoordinateAxis axis = nc.findCoordinateAxis(AxisType.GeoY);
        if (axis instanceof CoordinateAxis1D) {
            CoordinateAxis1D y = (CoordinateAxis1D)axis;
            double[] coordValues = y.getCoordValues();
            return coordValues;
        }
        throw new RuntimeException("Must contain 1D GeoX axis type");
    }
    
    public static FeatureDataset getFeatureDatasetFromFile(File grib) throws IOException {
        FeatureDataset dataset = FeatureDatasetFactoryManager.open(
                FeatureType.ANY, grib.getAbsolutePath(), null, null);
        return dataset;
    }
    
    public static GridDataset getGridDatasetFromFeatureDataset(FeatureDataset dataset) {
        if (dataset != null && dataset instanceof GridDataset) {
            return (GridDataset)dataset;
        }
        throw new UnsupportedOperationException("Dataset must be of type: GRID");
    }
    
    public static GridDatatype getDatatypeFromDataset(GridDataset gridDataset) {
        if (gridDataset != null) {
            List<GridDatatype> gdts = gridDataset.getGrids();
            if (!gdts.isEmpty()) {
                return gdts.get(0);
            }
        }
        throw new UnsupportedOperationException("Not a valid gridDataset");
    }
    
    public static CoordinateReferenceSystem getCRSFromDatatype(GridDatatype gridDatatype) {
        GridCoordSystem coordinateSystem = gridDatatype.getCoordinateSystem();
        return CRSUtility.getCRSFromGridCoordSystem(coordinateSystem);
    }
}
