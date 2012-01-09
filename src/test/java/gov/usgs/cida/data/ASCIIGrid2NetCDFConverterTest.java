package gov.usgs.cida.data;

import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jwalker
 */
public class ASCIIGrid2NetCDFConverterTest {
    /**
     * Test of convert method, of class ASCIIGrid2NetCDFConverter.
     */
    @Test
    public void testConvert() throws Exception {
        System.out.println("convert");
        File header = new File(ASCIIGrid2NetCDFConverterTest.class.getClassLoader().getResource("ASCII_HEADER").getFile());
        File data = new File(ASCIIGrid2NetCDFConverterTest.class.getClassLoader().getResource("PPT.DAY.grid").getFile());
        File output = new File("/tmp/ncout/");
        ASCIIGrid2NetCDFConverter instance = new ASCIIGrid2NetCDFConverter(header, data, output);
        instance.convert();
    }
}
