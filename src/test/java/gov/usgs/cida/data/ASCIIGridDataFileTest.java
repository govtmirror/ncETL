/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cida.data;

import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import java.io.File;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jwalker
 */
public class ASCIIGridDataFileTest {

    private static ASCIIGridDataFile data;

    @BeforeClass
    public static void setUpClass() throws Exception {
        File dataFile = new File(ASCIIGrid2NetCDFConverterTest.class.getClassLoader().getResource("PPT.DAY.grid").getFile());
        data = new ASCIIGridDataFile(dataFile);
        data.inspectFile();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        data = null;
    }

    /**
     * Test of getTimeUnits method, of class ASCIIGridDataFile.
     */
    @Test
    public void testGetTimeUnits() {
        String expResult = "days since 1980-01-01";
        String result = data.getTimeUnits();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTimestepIndices method, of class ASCIIGridDataFile.
     */
    @Test
    public void testGetTimestepIndices() {
        Map<Integer, List<Long>> result = data.getTimestepIndices();
        assertEquals(5, result.get(1980).size());
    }

    /**
     * Test of getVariableName method, of class ASCIIGridDataFile.
     */
    @Test
    public void testGetVariableName() {
        String expResult = "ppt";
        String result = data.getVariableName();
        assertEquals(expResult, result);
    }
}
