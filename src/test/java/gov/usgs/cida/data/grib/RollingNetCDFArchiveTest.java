
package gov.usgs.cida.data.grib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
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
    public void testGeotoolsProjection() throws Exception {
        RollingNetCDFArchive roll = new RollingNetCDFArchive(new File("/tmp/test.nc"));
        roll.define(new File("/home/jordan/test/QPE.20100319.009.160"));
        CoordinateReferenceSystem crs = roll.getCRS();
        GridDataset dataset = roll.getDataset();
        double[] xCoords = roll.getXCoords();
        double[] yCoords = roll.getYCoords();
        assertThat(xCoords.length, is(equalTo(250)));
        assertThat(yCoords.length, is(equalTo(260)));
        double[] latLons = roll.transformToLatLon(xCoords, yCoords);
        BufferedWriter lonWriter = null;
        BufferedWriter latWriter = null;
        try {
            lonWriter = new BufferedWriter(new FileWriter("/tmp/lons"));
            latWriter = new BufferedWriter(new FileWriter("/tmp/lats"));
            for (int i=0; i<latLons.length; i+=2) {
                lonWriter.append(" " + latLons[i]);
                latWriter.append(" " + latLons[i+1]);
            }
            assertThat(latLons.length, is(equalTo(2 * 250 * 260)));
        }
        finally {
            IOUtils.closeQuietly(lonWriter);
            IOUtils.closeQuietly(latWriter);
        }
    }
    
    @Test
    public void testNetCDFProjection() throws Exception {
        RollingNetCDFArchive roll = new RollingNetCDFArchive(new File("/tmp/test.nc"));
        roll.define(new File("/home/jordan/test/QPE.20100319.009.160"));
        CoordinateReferenceSystem crs = roll.getCRS();
        GridDataset dataset = roll.getDataset();
        double[] xCoords = roll.getXCoords();
        double[] yCoords = roll.getYCoords();
        assertThat(xCoords.length, is(equalTo(250)));
        assertThat(yCoords.length, is(equalTo(260)));
        double[][] latLons = roll.transformToLatLonNetCDFStyle(xCoords, yCoords);
        BufferedWriter lonWriter = null;
        BufferedWriter latWriter = null;
        try {
            lonWriter = new BufferedWriter(new FileWriter("/tmp/lonsnc"));
            latWriter = new BufferedWriter(new FileWriter("/tmp/latsnc"));
            for (int i=0; i<latLons[0].length; i++) {
                lonWriter.append(" " + latLons[0][i]);
                latWriter.append(" " + latLons[1][i]);
            }
            assertThat(latLons[0].length, is(equalTo(250 * 260)));
        }
        finally {
            IOUtils.closeQuietly(lonWriter);
            IOUtils.closeQuietly(latWriter);
        }
    }

}