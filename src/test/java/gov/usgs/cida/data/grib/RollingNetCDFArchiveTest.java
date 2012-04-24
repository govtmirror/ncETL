
package gov.usgs.cida.data.grib;

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.google.common.io.Flushables;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.apache.commons.io.IOUtils;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import ucar.nc2.dt.grid.GridDataset;

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
        roll.define(new File("/home/jordan/test/QPE.20100319.009.160"), 
                    Lists.newArrayList("time1", "time_bounds", "time1_bounds", "Total_precipitation_surface_Mixed_intervals_Accumulation"), "time");
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
            Flushables.flushQuietly(roll);
            Closeables.closeQuietly(roll);
            IOUtils.closeQuietly(lonWriter);
            IOUtils.closeQuietly(latWriter);
        }
    }

}