package gov.usgs.cida.ncetl.spec;

import com.google.common.collect.Lists;
import thredds.catalog.ThreddsMetadata.Contributor;
import java.util.HashMap;
import gov.usgs.cida.ncetl.mocks.MockConnection;
import gov.usgs.webservices.jdbc.spec.mapping.ColumnMapping;
import gov.usgs.webservices.jdbc.spec.mapping.SearchMapping;
import java.sql.Connection;
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
    
    private static final String TABLE_NAME = "contributor";
    private static final String TABLE_NAME_JOIN = "contributor_join";
    public static final String CONTRIBUTOR_ID = "contributor_id";
    
    private static final String TEXT = "text";
    private static final String ROLE = "role";

    private static MockConnection mc;
    private static Integer datasetId;
    private static HashMap mr;
    private static List<Contributor> result;
    private static List<Contributor> expResult;
    private static Contributor expCon;
    private static Contributor con;
    
    public ContributorJoinSpecTest() {
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
     * Test of unmarshal method, of class ContributorJoinSpec.
     */
    @Test
    public void testUnmarshalOne() throws Exception {
        System.out.println("* ContributorJoinSpec: unmarshal (one)");      
        
        // one complete entry
        mr = new HashMap();
        mr.put(CONTRIBUTOR_ID, 1);
        mc.storeMockResult(mr, TABLE_NAME);
        mr = new HashMap();
        mr.put(TEXT, "TEXT");
        mr.put(ROLE, "ROLE");
        mc.storeMockResult(mr, "contributor", 1);
        
        result = ContributorJoinSpec.unmarshal(datasetId, mc);
        for(Contributor con : result) {
            assertEquals(con.getName(), "TEXT");
            assertEquals(con.getRole(), "ROLE");
        }
    }
}
