/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package thredds.catalog;

import com.google.common.collect.Lists;
import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.*;
import ucar.nc2.units.DateType;

/**
 *
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public class InvCatalogModifierTest {

    private static File catalogFile = null;
    private InvCatalog catalog = null;

    public InvCatalogModifierTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        catalogFile = File.createTempFile("test", "xml");
        CatalogHelper.createNewCatalog("test", catalogFile.getPath());

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        catalogFile.deleteOnExit();
    }

    @Before
    public void setUp() {
        catalog = CatalogHelper.readCatalog(catalogFile.toURI());
    }

    @After
    public void tearDown() {
        catalog = null;
    }

    /**
     * Test of setBaseURI method, of class InvCatalogModifier.
     */
    @Test
    public void testSetBaseURI() {
        URI baseURI = catalogFile.toURI();
        InvCatalogModifier instance = new InvCatalogModifier(catalog);
        instance.setBaseURI(baseURI);
        assertThat(catalog.getUriString(), is(equalTo(baseURI.toString())));
    }

    /**
     * Test of setDatasets method, of class InvCatalogModifier.
     */
    @Test
    public void testSetDatasets() {
        List<InvDataset> datasets = Lists.asList(
                new InvDatasetWrapper("test", "id").build(), new InvDataset[0]);
        InvCatalogModifier instance = new InvCatalogModifier(catalog);
        instance.setDatasets(datasets);
        assertThat(catalog.getDatasets().size(), is(not(equalTo(0))));
        assertThat(catalog.getDatasets().get(0).getName(), is(equalTo("test")));
    }

    /**
     * Test of setExpires method, of class InvCatalogModifier.
     */
    @Test
    public void testSetExpires() {
        DateType expires = new DateType(true, new Date());
        InvCatalogModifier instance = new InvCatalogModifier(catalog);
        instance.setExpires(expires);
        assertThat(catalog.getExpires(), is(equalTo(expires)));
    }

    /**
     * Test of setName method, of class InvCatalogModifier.
     */
    @Test
    public void testSetName() {
        String name = "test";
        InvCatalogModifier instance = new InvCatalogModifier(catalog);
        instance.setName(name);
        assertThat(catalog.getName(), is(equalTo(name)));
    }

    /**
     * Test of setProperties method, of class InvCatalogModifier.
     */
    @Test
    public void testSetProperties() {
        List<InvProperty> properties = Lists.asList(new InvProperty("test",
                                                                    "value"),
                                                    new InvProperty[0]);
        InvCatalogModifier instance = new InvCatalogModifier(catalog);
        instance.setProperties(properties);
        assertThat(catalog.getProperties().size(), is(not(equalTo(0))));
        assertThat(catalog.getProperties().get(0).getName(), is(equalTo("test")));
        assertThat(catalog.getProperties().get(0).getValue(), is(
                equalTo("value")));
    }

    /**
     * Test of setServices method, of class InvCatalogModifier.
     */
    @Test
    public void testSetServices() {
        List<InvService> services = Lists.asList(
                new InvServiceBuilder("test").build(), new InvService[0]);
        InvCatalogModifier instance = new InvCatalogModifier(catalog);
        instance.setServices(services);
        assertThat(catalog.getServices().size(), is(not(equalTo(0))));
        assertThat(catalog.getServices().get(0).getName(), is(equalTo("test")));
    }

    /**
     * Test of setVersion method, of class InvCatalogModifier.
     */
    @Test
    public void testSetVersion() {
        String version = "1.2.0";
        InvCatalogModifier instance = new InvCatalogModifier(catalog);
        instance.setVersion(version);
        assertThat(catalog.getVersion(), is(equalTo(version)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCatalog() {
        new InvCatalogModifier(null);
    }
}
