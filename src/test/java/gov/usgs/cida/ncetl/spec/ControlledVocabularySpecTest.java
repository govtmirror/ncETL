package gov.usgs.cida.ncetl.spec;

import thredds.catalog.ThreddsMetadata.Vocab;
import com.google.common.collect.Lists;
import thredds.catalog.InvDocumentation;
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
public class ControlledVocabularySpecTest {
    
    private static final String TABLE_NAME = "controlled_vocabulary";
    public static final String VOCAB = "vocab";
    
    private static MockConnection mc;
    private static Integer datasetId;
    private static HashMap mr;
    private static String text;
    private static Vocab result;
    private static Vocab expResult;
    
    public ControlledVocabularySpecTest() {
        
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        mc = new MockConnection();
        datasetId = 0;
        text = "text";
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of lookupAndAddText method, of class ControlledVocabularySpec.
     */
    @Test
    public void testLookupAndAddTextOne() throws Exception {
        System.out.println("* ControlledVocabularySpec: lookupAndAddText (one)");        
        
        // complete entry
        mr = new HashMap();
        mr.put(VOCAB, "VOCAB");
        mc.storeMockResult(mr, TABLE_NAME);
        
        result = ControlledVocabularySpec.lookupAndAddText(datasetId, text, mc);
        expResult = new Vocab(text, "VOCAB");
        assertEquals(expResult, result);
    }
    
     /**
     * Test of lookupAndAddText method, of class ControlledVocabularySpec.
     */
    @Test
    public void testLookupAndAddTextEmpty() throws Exception {
        System.out.println("* ControlledVocabularySpec: lookupAndAddText (empty)");        
        
        result = ControlledVocabularySpec.lookupAndAddText(datasetId, text, mc);
        expResult = null;
        assertEquals(expResult, result);
    }
}
