package gov.usgs.cida.ncetl.spec;

import gov.usgs.cida.ncetl.mocks.MockConnection;
import java.util.HashMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import thredds.catalog.SpatialRangeType;

/**
 *
 * @author M. Herold
 */
public class SpatialRangeTypeSpecTest {

    private static final String TABLE_NAME = "spatial_range_type";
    public static final String TYPE = "type";
    private static MockConnection mc;
    private static Integer datasetId;
    private static HashMap mr;
    private static SpatialRangeType result;
    private static SpatialRangeType expResult;

    public SpatialRangeTypeSpecTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        mc = new MockConnection();
        datasetId = 0;
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of unmarshal method, of class SpatialRangeTypeSpec.
     */
    @Test
    public void testUnmarshalOne() throws Exception {
        System.out.println("* SpatialRangeTypeSpec: unmarshal (one)");

        // complete entry
        mr = new HashMap();
        mr.put(TYPE, "updown");
        mc.storeMockResult(mr, TABLE_NAME);

        result = SpatialRangeTypeSpec.unmarshal(datasetId, mc);
        expResult = SpatialRangeType.getType("updown");
        assertEquals(expResult, result);
    }

    /**
     * Test of unmarshal method, of class SpatialRangeTypeSpec.
     */
    @Test
    public void testUnmarshalEmpty() throws Exception {
        System.out.println("* SpatialRangeTypeSpec: unmarshal (empty)");

        result = SpatialRangeTypeSpec.unmarshal(datasetId, mc);
        expResult = SpatialRangeType.getType(null);
        assertEquals(expResult, result);
    }
}
