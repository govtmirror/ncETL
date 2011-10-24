package gov.usgs.cida.ncetl.spec;

import com.sun.j3d.utils.geometry.Text2D;
import gov.usgs.cida.ncetl.mocks.MockConnection;
import gov.usgs.webservices.jdbc.spec.mapping.ColumnMapping;
import gov.usgs.webservices.jdbc.spec.mapping.SearchMapping;
import java.sql.Connection;
import java.util.HashMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import thredds.catalog.ThreddsMetadata.Contributor;

/**
 *
 * @author M. Herold
 */
public class ContributorSpecTest {
    
    private static final String TEXT = "text";
    private static final String ROLE = "role";
    private static final String TABLE_NAME = "contributor";
    
    private static int id;
    private static MockConnection mc;
    private static HashMap mr;
    
    public ContributorSpecTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        int id = 0;
        mc = new MockConnection();
        mr = new HashMap();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of lookup method, of class ContributorSpec.
     */
    @Test
    public void testLookupOne() throws Exception {
        System.out.println("* ContributorSpec: lookup (one)");

        // one complete entry
        mr = new HashMap();
        mr.put(TEXT, "TEXT");
        mr.put(ROLE, "ROLE");
        mc.storeMockResult(mr, TABLE_NAME);
        Contributor result = ContributorSpec.lookup(id, mc);
        assertEquals("ROLE", result.getRole());
        assertEquals("TEXT", result.getName());
    }
    
    /**
     * Test of lookup method, of class ContributorSpec.
     */
    @Test
    public void testLookupName() throws Exception {
        System.out.println("* ContributorSpec: lookup (name)");

        // partial entry
        mr = new HashMap();
        mr.put(TEXT, "TEXT");
        mc.storeMockResult(mr, TABLE_NAME);
        Contributor result = ContributorSpec.lookup(id, mc);
        assertEquals(null, result.getRole());
        assertEquals("TEXT", result.getName());
    }
    
    /**
     * Test of lookup method, of class ContributorSpec.
     */
    @Test
    public void testLookupRole() throws Exception {
        System.out.println("* ContributorSpec: lookup (role)");

        // partial entry
        mr = new HashMap();
        mr.put(ROLE, "ROLE");
        mc.storeMockResult(mr, TABLE_NAME);
        Contributor result = ContributorSpec.lookup(id, mc);
        assertEquals("ROLE", result.getRole());
        assertEquals(null, result.getName());
    }
    
    /**
     * Test of lookup method, of class ContributorSpec.
     */
    @Test
    public void testLookupEmpty() throws Exception {
        System.out.println("* ContributorSpec: lookup (empty)");

        // empty entry
        mr = new HashMap();
        mc.storeMockResult(mr, TABLE_NAME);
        Contributor result = ContributorSpec.lookup(id, mc);
        assertEquals(null, result.getRole());
        assertEquals(null, result.getName());
    }
}
