package gov.usgs.cida.ncetl.spec;

import java.util.List;
import java.util.ArrayList;
import com.google.common.collect.Maps;
import thredds.catalog.ThreddsMetadata.Range;
import thredds.catalog.SpatialRangeType;
import java.util.Map;
import gov.usgs.cida.ncetl.mocks.MockConnection;
import java.util.HashMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author M. Herold
 */
public class SpatialRangeSpecTest {

    private static final String TYPE_TABLE = "spatial_range_type";
    public static final String TYPE = "type";
    private static final String RANGE_TABLE = "spatial_range";
    public static final String GEOSPATIAL_COVERAGE_ID = "geospatial_coverage_id";
    public static final String SPATIAL_RANGE_TYPE_ID = "spatial_range_type_id";
    public static final String START = "start";
    public static final String SIZE = "size";
    public static final String RESOLUTION = "resolution";
    public static final String UNITS = "units";
    private static MockConnection mc;
    private static Integer datasetId;
    private static HashMap mr;
    private static String text;
    private static Map<SpatialRangeType, Range> result;
    private static Map<SpatialRangeType, Range> expResult;
    private static Range expRange;
    private static SpatialRangeType expType;
    private List<String> types = new ArrayList();

    public SpatialRangeSpecTest() {
        mc = new MockConnection();
        datasetId = 0;
        types.add("northsouth");
        types.add("eastwest");
        types.add("updown");
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of unmarshal method, of class SpatialRangeSpec.
     */
    @Test
    public void testLookupOne() throws Exception {
        System.out.println("* SpatialRangeSpec: unmarshal (one)");

        // one complete entry
        mr = new HashMap();
        mr.put(START, 1.1);
        mr.put(SIZE, 2.2);
        mr.put(RESOLUTION, 3.3);
        mr.put(UNITS, "UNITS");
        mr.put(SPATIAL_RANGE_TYPE_ID, 1);
        mc.storeMockResult(mr, RANGE_TABLE);

        mr = new HashMap();
        mr.put(TYPE, "updown");
        mc.storeMockResult(mr, TYPE_TABLE, 1);

        result = SpatialRangeSpec.unmarshal(datasetId, mc);
        expRange = new Range(1.1, 2.2, 3.3, "UNITS");
        expType = SpatialRangeType.getType("updown");
        expResult = Maps.newTreeMap();
        expResult.put(expType, expRange);

        assertEquals(expResult, result);
    }

    /**
     * Test of unmarshal method, of class SpatialRangeSpec.
     */
    @Test
    public void testLookupTypless() throws Exception {
        System.out.println("* SpatialRangeSpec: unmarshal (typless)");

        // one varied entry
        mr = new HashMap();
        mr.put(START, 1.1);
        mr.put(SIZE, 2.2);
        mr.put(RESOLUTION, 3.3);
        mr.put(UNITS, "UNITS");
        mr.put(SPATIAL_RANGE_TYPE_ID, 1);
        mc.storeMockResult(mr, RANGE_TABLE);

        result = SpatialRangeSpec.unmarshal(datasetId, mc);
        expRange = new Range(1.1, 2.2, 3.3, "UNITS");
        expType = SpatialRangeType.getType(null);
        expResult = Maps.newTreeMap();
        expResult.put(expType, expRange);

        assertEquals(expResult, result);
    }

    /**
     * Test of unmarshal method, of class SpatialRangeSpec.
     */
    @Test
    public void testLookupMany() throws Exception {
        System.out.println("* SpatialRangeSpec: unmarshal (many)");

        // many entries
        for (int i = 0; i < 3; i++) {
            mr = new HashMap();
            mr.put(START, 1.1 * i);
            mr.put(SIZE, 2.2 * i);
            mr.put(RESOLUTION, 3.3 * i);
            mr.put(UNITS, "UNITS" + i);
            mr.put(SPATIAL_RANGE_TYPE_ID, i);
            mc.storeMockResult(mr, RANGE_TABLE);

            mr = new HashMap();
            mr.put(TYPE, types.get(i % 3));
            mc.storeMockResult(mr, TYPE_TABLE, i);
        }

        result = SpatialRangeSpec.unmarshal(datasetId, mc);
        expResult = Maps.newTreeMap();
        for (int i = 0; i < 3; i++) {
            expRange = new Range(1.1 * i, 2.2 * i, 3.3 * i, "UNITS" + i);
            expType = SpatialRangeType.getType(types.get(i % 3));
            expResult.put(expType, expRange);
        }

        assertEquals(expResult, result);
    }

    /**
     * Test of unmarshal method, of class SpatialRangeSpec.
     */
    @Test
    public void testLookupTons() throws Exception {
        System.out.println("* SpatialRangeSpec: unmarshal (tons)");

        // many entries
        for (int i = 0; i < 1000; i++) {
            mr = new HashMap();
            mr.put(START, 1.1 * i);
            mr.put(SIZE, 2.2 * i);
            mr.put(RESOLUTION, 3.3 * i);
            mr.put(UNITS, "UNITS" + i);
            mr.put(SPATIAL_RANGE_TYPE_ID, i);
            mc.storeMockResult(mr, RANGE_TABLE);

            mr = new HashMap();
            mr.put(TYPE, types.get(i % 3));
            mc.storeMockResult(mr, TYPE_TABLE, i);
        }

        result = SpatialRangeSpec.unmarshal(datasetId, mc);
        expResult = Maps.newTreeMap();
        for (int i = 999; i >= 0; i--) { // because the MockResultSet is a stack
            expRange = new Range(1.1 * i, 2.2 * i, 3.3 * i, "UNITS" + i);
            expType = SpatialRangeType.getType(types.get(i % 3));
            expResult.put(expType, expRange);
        }

        assertEquals(expResult, result);
    }

    /**
     * Test of unmarshal method, of class SpatialRangeSpec.
     */
    @Test
    public void testLookupEmpty() throws Exception {
        System.out.println("* SpatialRangeSpec: unmarshal (empty)");

        result = SpatialRangeSpec.unmarshal(datasetId, mc);
        expResult = Maps.newTreeMap();
        assertEquals(expResult, result);
    }
}
