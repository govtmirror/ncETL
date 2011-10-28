package gov.usgs.cida.ncetl.spec;

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
public class UpDownTypeSpecTest {

    private static final String TABLE_NAME = "up_down_type";
    public static final String TYPE = "type";
    private static MockConnection mc;
    private static Integer datasetId;
    private static HashMap mr;
    private static String text;
    private static String result;
    private static String expResult;

    public UpDownTypeSpecTest() {
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
     * Test of lookup method, of class UpDownTypeSpec.
     */
    @Test
    public void testLookupOne() throws Exception {
        System.out.println("* UpDownTypeSpec: lookup (one)");

        // complete entry
        mr = new HashMap();
        mr.put(TYPE, "TYPE");
        mc.storeMockResult(mr, TABLE_NAME);

        result = UpDownTypeSpec.lookup(datasetId, mc);
        expResult = "TYPE";
        assertEquals(expResult, result);
    }

    /**
     * Test of lookup method, of class UpDownTypeSpec.
     */
    @Test
    public void testLookupEmpty() throws Exception {
        System.out.println("* UpDownTypeSpec: lookup (empty)");

        result = UpDownTypeSpec.lookup(datasetId, mc);
        expResult = null;
        assertEquals(expResult, result);
    }
}
