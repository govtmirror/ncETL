package gov.usgs.cida.ncetl.spec;

import org.junit.Before;
import com.google.common.collect.Lists;
import thredds.catalog.ThreddsMetadata.Contributor;
import java.util.HashMap;
import gov.usgs.cida.ncetl.mocks.MockConnection;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author M. Herold
 */
public class ContributorJoinSpecTest {
    
    private static final String JOIN_TABLE = "contributor_join";
    private static final String CON_TABLE = "contributor";
    public static final String CONTRIBUTOR_ID = "contributor_id";
    private static final String TEXT = "text";
    private static final String ROLE = "role";

    private static MockConnection mc;
    private static Integer datasetId;
    private static HashMap mr;
    private static List<Contributor> result;
    
    public ContributorJoinSpecTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        datasetId = 0;
    }
    
    @Before
    public void setUpMethod() throws Exception {
        mc = new MockConnection();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of unmarshal method, of class ContributorJoinSpec.
     */
    @Test
    public void testUnmarshalOne() throws Exception {
        System.out.println("* ContributorJoinSpec: unmarshal (one)");      
        
        // one complete entry
        mr = new HashMap();
        mr.put(CONTRIBUTOR_ID, 1);
        mc.storeMockResult(mr, JOIN_TABLE);
        mr = new HashMap();
        mr.put(TEXT, "TEXT");
        mr.put(ROLE, "ROLE");
        mc.storeMockResult(mr, CON_TABLE, 1);
        
        result = ContributorJoinSpec.unmarshal(datasetId, mc);
        for(Contributor con : result) {
            assertEquals(con.getName(), "TEXT");
            assertEquals(con.getRole(), "ROLE");
        }
    }
    
    /**
     * Test of unmarshal method, of class ContributorJoinSpec.
     */
    @Test
    public void testUnmarshalMany() throws Exception {
        System.out.println("* ContributorJoinSpec: unmarshal (many)");      
        
        // many complete entries
        for(int i = 0; i < 3; i++) {
            mr = new HashMap();
            mr.put(CONTRIBUTOR_ID, i);
            mc.storeMockResult(mr, JOIN_TABLE);
            mr = new HashMap();
            mr.put(TEXT, "TEXT"+String.valueOf(i));
            mr.put(ROLE, "ROLE"+String.valueOf(i));
            mc.storeMockResult(mr, CON_TABLE, i);
        }
        result = ContributorJoinSpec.unmarshal(datasetId, mc);
        int i = 2; // since they're in a stack
        for(Contributor con : result) {
            System.out.println(con.getName());
            assertEquals("TEXT"+String.valueOf(i), con.getName());
            assertEquals("ROLE"+String.valueOf(i), con.getRole());
            i--;
        }
    }
    
    /**
     * Test of unmarshal method, of class ContributorJoinSpec.
     */
    @Test
    public void testUnmarshalTons() throws Exception {
        System.out.println("* ContributorJoinSpec: unmarshal (tons)");      
        
        // many complete entries
        for(int i = 0; i < 1000; i++) {
            mr = new HashMap();
            mr.put(CONTRIBUTOR_ID, i);
            mc.storeMockResult(mr, JOIN_TABLE);
            mr = new HashMap();
            mr.put(TEXT, "TEXT"+String.valueOf(i));
            mr.put(ROLE, "ROLE"+String.valueOf(i));
            mc.storeMockResult(mr, CON_TABLE, i);
        }
        result = ContributorJoinSpec.unmarshal(datasetId, mc);
        int i = 999; // since they're in a stack
        for(Contributor con : result) {
            assertEquals("TEXT"+String.valueOf(i), con.getName());
            assertEquals("ROLE"+String.valueOf(i), con.getRole());
            i--;
        }
    }
    
    /**
     * Test of unmarshal method, of class ContributorJoinSpec.
     */
    @Test
    public void testUnmarshalVaried() throws Exception {
        System.out.println("* ContributorJoinSpec: unmarshal (varied)");      
        
        // varied entries
        mr = new HashMap();
        mr.put(CONTRIBUTOR_ID, 1);
        mc.storeMockResult(mr, JOIN_TABLE);
        mr = new HashMap();
        mr.put(CONTRIBUTOR_ID, 2);
        mc.storeMockResult(mr, JOIN_TABLE);
        
        mr = new HashMap();
        mr.put(TEXT, "TEXT");
        mc.storeMockResult(mr, CON_TABLE, 1);
        mr = new HashMap();
        mr.put(ROLE, "ROLE");
        mc.storeMockResult(mr, CON_TABLE, 2);

        result = ContributorJoinSpec.unmarshal(datasetId, mc);
        for(Contributor con : result) {
            if(con.getName() != null)
                assertEquals("TEXT", con.getName());
            else
                assertEquals("ROLE", con.getRole());
        }
    } 
    
    /**
     * Test of unmarshal method, of class ContributorJoinSpec.
     */
    @Test
    public void testUnmarshalEmpty() throws Exception {
        System.out.println("* ContributorJoinSpec: unmarshal (empty)");      
        
        // one empty entry
        mr = new HashMap();
        mr.put(CONTRIBUTOR_ID, 1);
        mc.storeMockResult(mr, JOIN_TABLE);
            
        List<Contributor> expResult = Lists.newLinkedList();
        expResult.add(null);
        result = ContributorJoinSpec.unmarshal(datasetId, mc);
        assertEquals(expResult, result);
    } 
}
