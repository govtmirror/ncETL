package gov.usgs.cida.ncetl.spec;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gov.usgs.cida.ncetl.mocks.MockConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import thredds.catalog.SpatialRangeType;
import thredds.catalog.ThreddsMetadata.GeospatialCoverage;
import thredds.catalog.ThreddsMetadata.Range;
import thredds.catalog.ThreddsMetadata.Vocab;

/**
 *
 * @author M. Herold
 */
public class GeospatialCoverageSpecTest {

    private static final String GEO_TABLE = "geospatial_coverage";
    public static final String ID = "id";
    public static final String DATASET_ID = "dataset_id";
    public static final String CONTROLLED_VOCAB_ID = "controlled_vocabulary_id";
    public static final String NAME = "name";
    public static final String ZPOSITIVE_ID = "zpositive_id";
    private static final String RANGE_TABLE = "spatial_range";
    public static final String GEOSPATIAL_COVERAGE_ID = "geospatial_coverage_id";
    public static final String SPATIAL_RANGE_TYPE_ID = "spatial_range_type_id";
    public static final String START = "start";
    public static final String SIZE = "size";
    public static final String RESOLUTION = "resolution";
    public static final String UNITS = "units";
    private static final String VOCAB_TABLE = "controlled_vocabulary";
    public static final String VOCAB = "vocab";
    private static final String UP_DOWN_TABLE = "up_down_type";
    public static final String TYPE = "type";
    private static final String TYPE_TABLE = "spatial_range_type";
    private static MockConnection mc;
    private static Integer datasetId;
    private static HashMap mr;
    private static GeospatialCoverage result;
    private static List<GeospatialCoverage> results;
    private static GeospatialCoverage expResult;
    private static ArrayList<String> types;

    public GeospatialCoverageSpecTest() {
        mc = new MockConnection();
        datasetId = 0;
        types = new ArrayList();
        types.add("updown");
        types.add("eastwest");
        types.add("northsouth");
        results = new ArrayList();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of unmarshal method, of class GeospatialCoverageSpec.
     */
    @Test
    public void testUnmarshalOne() throws Exception {
        System.out.println("* GeospatialCoverageSpec: unmarshal (one)");

        // one complete entry
        mr = new HashMap();
        mr.put(NAME, "NAME");
        mr.put(CONTROLLED_VOCAB_ID, 1);
        mr.put(ZPOSITIVE_ID, 2);
        mr.put(ID, 3);
        mc.storeMockResult(mr, GEO_TABLE);

        mr = new HashMap();
        mr.put(VOCAB, "VOCAB");
        mc.storeMockResult(mr, VOCAB_TABLE, 1);

        mr = new HashMap();
        mr.put(TYPE, "TYPE");
        mc.storeMockResult(mr, UP_DOWN_TABLE, 2);

        for (int i = 0; i < 3; i++) {
            mr = new HashMap();
            mr.put(START, 0.0);
            mr.put(SIZE, 1.1);
            mr.put(RESOLUTION, 0.123098);
            mr.put(UNITS, "UNITS");
            mr.put(SPATIAL_RANGE_TYPE_ID, i);
            mc.storeMockResult(mr, RANGE_TABLE, 3);

            mr = new HashMap();
            mr.put(TYPE, types.get(i));
            mc.storeMockResult(mr, TYPE_TABLE, i);
        }

        result = GeospatialCoverageSpec.unmarshal(datasetId, mc);

        // expected result
        Vocab vocab = new Vocab("NAME", "VOCAB");
        String upOrDown = "TYPE";
        double start = 0.0;
        double size = 1.1;
        double resolution = 0.123098;
        String units = "UNITS";
        Map<SpatialRangeType, Range> ranges = Maps.newTreeMap();
        Range range = new Range(start, size, resolution, units);
        for (String t : types) {
            ranges.put(SpatialRangeType.getType(t), range);
        }

        expResult = new GeospatialCoverage(range, range, range, Lists.asList(vocab, new Vocab[0]), upOrDown);

        assertEquals(expResult.getNames().get(0).getVocabulary(), result.getNames().get(0).getVocabulary());
        assertEquals(expResult.getZPositive(), result.getZPositive());

        assertEquals(expResult.getUpDownRange().getUnits(), result.getUpDownRange().getUnits());
        assertEquals(expResult.getUpDownRange().getStart(), result.getUpDownRange().getStart(), 0);
        assertEquals(expResult.getUpDownRange().getSize(), result.getUpDownRange().getSize(), 0);
        assertEquals(expResult.getUpDownRange().getResolution(), result.getUpDownRange().getResolution(), 0);

        assertEquals(expResult.getEastWestRange().getUnits(), result.getEastWestRange().getUnits());
        assertEquals(expResult.getEastWestRange().getStart(), result.getEastWestRange().getStart(), 0);
        assertEquals(expResult.getEastWestRange().getSize(), result.getEastWestRange().getSize(), 0);
        assertEquals(expResult.getEastWestRange().getResolution(), result.getEastWestRange().getResolution(), 0);

        assertEquals(expResult.getNorthSouthRange().getUnits(), result.getNorthSouthRange().getUnits());
        assertEquals(expResult.getNorthSouthRange().getStart(), result.getNorthSouthRange().getStart(), 0);
        assertEquals(expResult.getNorthSouthRange().getSize(), result.getNorthSouthRange().getSize(), 0);
        assertEquals(expResult.getNorthSouthRange().getResolution(), result.getNorthSouthRange().getResolution(), 0);
    }
    
    /**
     * Test of unmarshal method, of class GeospatialCoverageSpec.
     */
    @Test
    public void testUnmarshalEmpy() throws Exception {
        System.out.println("* GeospatialCoverageSpec: unmarshal (empty)");

        result = GeospatialCoverageSpec.unmarshal(datasetId, mc);
        expResult = null;
        assertEquals(expResult, result);
    }

 
}
