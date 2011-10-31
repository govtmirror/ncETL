package gov.usgs.cida.ncetl.spec;

import java.util.HashMap;
import gov.usgs.cida.ncetl.mocks.MockConnection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author M. Herold
 */
public class DateTypeEnumSpecTest {

    private static final String TABLE_NAME = "date_type_enum";
    public static final String TYPE = "type";
    private static MockConnection mc;
    private static Integer datasetId;
    private static HashMap mr;
    private static String result;
    private static String expResult;

    public DateTypeEnumSpecTest() {
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
     * Test of lookup method, of class DateTypeEnumSpec.
     */
    @Test
    public void testLookupOne() throws Exception {
        System.out.println("* DateTypeEnumSpec: lookup (one)");

        // complete entry
        mr = new HashMap();
        mr.put(TYPE, "TYPE");
        mc.storeMockResult(mr, TABLE_NAME);

        result = DateTypeEnumSpec.lookup(datasetId, mc);
        expResult = "TYPE";
        assertEquals(expResult, result);
    }

    /**
     * Test of lookup method, of class DateTypeEnumSpec.
     */
    @Test
    public void testLookupEmpy() throws Exception {
        System.out.println("* DateTypeEnumSpec: lookup (empty)");

        result = DateTypeEnumSpec.lookup(datasetId, mc);
        expResult = null;
        assertEquals(expResult, result);
    }
}
