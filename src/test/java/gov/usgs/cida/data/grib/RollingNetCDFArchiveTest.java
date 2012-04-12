
package gov.usgs.cida.data.grib;

import java.io.File;
import java.util.List;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import ucar.nc2.constants.AxisType;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.CoordinateAxis2D;
import ucar.nc2.dt.grid.GridDataset;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public class RollingNetCDFArchiveTest {

    /**
     * Test of addFile method, of class RollingNetCDFArchive.
     * @throws Exception 
     */
    @Test
    public void testCRS() throws Exception {
        RollingNetCDFArchive roll = new RollingNetCDFArchive(new File("/tmp/test.nc"));
        roll.define(new File("/home/jordan/test/QPE.20100319.009.160"));
        CoordinateReferenceSystem crs = roll.getCRS();
        GridDataset dataset = roll.getDataset();
        double[] xCoords = roll.getXCoords();
        double[] yCoords = roll.getYCoords();
        assertThat(xCoords.length, is(equalTo(250)));
        assertThat(yCoords.length, is(equalTo(260)));
        double[] transformToLatLon = roll.transformToLatLon(xCoords, yCoords);
        assertThat(transformToLatLon.length, is(equalTo(2 * 250 * 260)));
    }

}