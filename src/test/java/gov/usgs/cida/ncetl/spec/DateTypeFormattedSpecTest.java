package gov.usgs.cida.ncetl.spec;

import gov.usgs.cida.ncetl.mocks.MockConnection;
import java.util.HashMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import ucar.nc2.units.DateType;

/**
 *
 * @author M. Herold
 */
public class DateTypeFormattedSpecTest {

    private static final String FORMAT_TABLE = "date_type_formatted";
    public static final String FORMAT = "format";
    public static final String VALUE = "value";
    public static final String DATE_TYPE_ENUM_ID = "date_type_enum_id";
    private static final String ENUM_TABLE = "date_type_enum";
    public static final String TYPE = "type";
    private static MockConnection mc;
    private static Integer datasetId;
    private static HashMap mr;
    private static String text;
    private static DateType result;
    private static DateType expResult;

    public DateTypeFormattedSpecTest() {
        mc = new MockConnection();
        datasetId = 0;
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
        mr.put(VALUE, "AD");
        mr.put(FORMAT, "G");
        mr.put(DATE_TYPE_ENUM_ID, 0);
        mc.storeMockResult(mr, FORMAT_TABLE);

        mr = new HashMap();
        mr.put(TYPE, "Era designator");
        mc.storeMockResult(mr, ENUM_TABLE, 0);

        result = DateTypeFormattedSpec.lookup(datasetId, mc);
        expResult = new DateType("AD", "G", "Era designator");

        assertEquals(expResult, result);
    }

    /**
     * Test of unmarshal method, of class SpatialRangeSpec.
     */
    @Test
    public void testLookupHalfEmpty() throws Exception {
        System.out.println("* SpatialRangeSpec: unmarshal (half empty)");

        mr = new HashMap();
        mr.put(VALUE, "AD");
        mr.put(FORMAT, "G");
        mr.put(DATE_TYPE_ENUM_ID, 0);
        mc.storeMockResult(mr, FORMAT_TABLE);

        result = DateTypeFormattedSpec.lookup(datasetId, mc);
        expResult = new DateType("AD", "G", null);

        assertEquals(expResult, result);
    }

    /**
     * Test of unmarshal method, of class SpatialRangeSpec.
     */
    @Test
    public void testLookupEmpty() throws Exception {
        System.out.println("* SpatialRangeSpec: unmarshal (empty)");

        result = DateTypeFormattedSpec.lookup(datasetId, mc);
        expResult = null;

        assertEquals(expResult, result);
    }
}
