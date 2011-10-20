package gov.usgs.cida.ncetl.spec;

import com.google.common.collect.Lists;
import thredds.catalog.InvDocumentation;
import gov.usgs.cida.ncetl.mocks.MockConnection;
import java.util.HashMap;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author M. Herold
 */
public class DocumentationSpecTest {
    
    private static final String TABLE_NAME = "documentation";
    public static final String DOCUMENTATION_TYPE_ID = "documentation_type_id";
    public static final String ID = "id";
    public static final String DATASET_ID = "dataset_id";
    public static final String XLINK_HREF = "xlink_href";
    public static final String XLINK_TITLE = "xlink_title";
    public static final String TEXT = "text";
    public static final String TYPE = "type";
    
    private static MockConnection mc;
    private static Integer datasetId;
    private static HashMap mr;
    private static List<InvDocumentation> result;
    private static List<InvDocumentation> expResult;
    private static InvDocumentation expDoc;
    private static InvDocumentation doc;
    
    public DocumentationSpecTest() {
        
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
     * Test of unmarshal method, of class DocumentationSpec.
     */
    @Test
    public void testUnmarshalOne() throws Exception {
        System.out.println("* DocumentationSpec: unmarshal (one)");        
        
        // one complete entry
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mr.put(XLINK_HREF, "HREF");
        mr.put(XLINK_TITLE, "TITLE");
        mr.put(TEXT, "TEXT");
        mc.storeMockResult(mr, TABLE_NAME);
        mr = new HashMap();
        mr.put(TYPE, "TYPE");
        mc.storeMockResult(mr, TABLE_NAME + "_type");
        
        result = DocumentationSpec.unmarshal(datasetId, mc);
        expResult = Lists.newLinkedList();
        expDoc = new InvDocumentation("HREF", null, "TITLE", "TYPE", "TEXT");
        expResult.add(expDoc);
        assertEquals(expResult, result);
    }
    
    
    /**
     * Test of unmarshal method, of class DocumentationSpec.
     */
    @Test
    public void testUnmarshalMany() throws Exception {
        System.out.println("* DocumentationSpec: unmarshal (many)");        

        // multiple complete entries
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mr.put(XLINK_HREF, "HREF");
        mr.put(XLINK_TITLE, "TITLE");
        mr.put(TEXT, "TEXT");
        mc.storeMockResult(mr, TABLE_NAME);
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mr.put(TYPE, "TYPE");
        mc.storeMockResult(mr, TABLE_NAME + "_type");
        
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mr.put(XLINK_HREF, "HREF");
        mr.put(XLINK_TITLE, "TITLE");
        mr.put(TEXT, "TEXT");
        mc.storeMockResult(mr, TABLE_NAME);
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mr.put(TYPE, "TYPE");
        mc.storeMockResult(mr, TABLE_NAME + "_type");
        
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mr.put(XLINK_HREF, "HREF");
        mr.put(XLINK_TITLE, "TITLE");
        mr.put(TEXT, "TEXT");
        mc.storeMockResult(mr, TABLE_NAME);
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mr.put(TYPE, "TYPE");
        mc.storeMockResult(mr, TABLE_NAME + "_type");
        
        result = DocumentationSpec.unmarshal(datasetId, mc);
        expResult = Lists.newLinkedList();
        expDoc = new InvDocumentation("HREF", null, "TITLE", "TYPE", "TEXT");
        expResult.add(expDoc);
        expResult.add(expDoc);
        expResult.add(expDoc);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of unmarshal method, of class DocumentationSpec.
     */
    @Test
    public void testUnmarshalVaried() throws Exception {
        System.out.println("* DocumentationSpec: unmarshal (varied)");        
        
        // HREF-only entry
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mr.put(XLINK_HREF, "HREF");
        mc.storeMockResult(mr, TABLE_NAME);
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mc.storeMockResult(mr, TABLE_NAME + "_type");
        
        result = DocumentationSpec.unmarshal(datasetId, mc);
        doc = result.get(0);
        assertEquals("HREF", doc.getXlinkHref());
        assertEquals(null, doc.getInlineContent());
        assertEquals(null, doc.getType());
        assertEquals(null, doc.getURI());
        assertEquals(null, doc.getXlinkTitle());
        
        // TYPE-only entry
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mc.storeMockResult(mr, TABLE_NAME);
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mr.put(TYPE, "TYPE");
        mc.storeMockResult(mr, TABLE_NAME + "_type");
        
        result = DocumentationSpec.unmarshal(datasetId, mc);
        doc = result.get(0);
        assertEquals(null, doc.getXlinkHref());
        assertEquals(null, doc.getInlineContent());
        assertEquals("TYPE", doc.getType());
        assertEquals(null, doc.getURI());
        assertEquals(null, doc.getXlinkTitle());
        
        // TEXT and TITLE entry
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mr.put(XLINK_TITLE, "TITLE");
        mr.put(TEXT, "TEXT");
        mc.storeMockResult(mr, TABLE_NAME);
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mc.storeMockResult(mr, TABLE_NAME + "_type");
        
        result = DocumentationSpec.unmarshal(datasetId, mc);
        doc = result.get(0);
        assertEquals(null, doc.getXlinkHref());
        assertEquals("TEXT", doc.getInlineContent());
        assertEquals(null, doc.getType());
        assertEquals(null, doc.getURI());
        assertEquals("TITLE", doc.getXlinkTitle());
    }
    
    /**
     * Test of unmarshal method, of class DocumentationSpec.
     */
    @Test
    public void testUnmarshalEmpty() throws Exception {
        System.out.println("* DocumentationSpec: unmarshal (empty)");        
        
        // empty entry
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mc.storeMockResult(mr, TABLE_NAME);
        mr = new HashMap();
        mr.put(DOCUMENTATION_TYPE_ID, 0);
        mc.storeMockResult(mr, TABLE_NAME + "_type");
        
        result = DocumentationSpec.unmarshal(datasetId, mc);
        doc = result.get(0);
        assertEquals(null, doc.getXlinkHref());
        assertEquals(null, doc.getInlineContent());
        assertEquals(null, doc.getType());
        assertEquals(null, doc.getURI());
        assertEquals(null, doc.getXlinkTitle());
    }
    
    /**
     * Test of unmarshal method, of class DocumentationSpec.
     */
    @Test
    public void testUnmarshalTons() throws Exception {
        System.out.println("* DocumentationSpec: unmarshal (tons)");        
        
        // tons of complete entries
        for(int i = 0; i < 1000; i++) {
            mr = new HashMap();
            mr.put(DOCUMENTATION_TYPE_ID, 0);
            mr.put(XLINK_HREF, "HREF");
            mr.put(XLINK_TITLE, "TITLE");
            mr.put(TEXT, "TEXT");
            mc.storeMockResult(mr, TABLE_NAME);
            mr = new HashMap();
            mr.put(DOCUMENTATION_TYPE_ID, 0);
            mr.put(TYPE, "TYPE");
            mc.storeMockResult(mr, TABLE_NAME + "_type");
        }
        result = DocumentationSpec.unmarshal(datasetId, mc);
        for(InvDocumentation doc : result) {
            assertEquals("HREF", doc.getXlinkHref());
            assertEquals("TEXT", doc.getInlineContent());
            assertEquals("TYPE", doc.getType());
            assertEquals(null, doc.getURI());
            assertEquals("TITLE", doc.getXlinkTitle());
        }
    }
    
}
