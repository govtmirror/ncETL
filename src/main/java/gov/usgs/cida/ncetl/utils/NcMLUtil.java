package gov.usgs.cida.ncetl.utils;
import com.google.common.collect.Sets;
import org.jdom.JDOMException;
import thredds.server.metadata.bean.Extent;
import thredds.server.metadata.exception.ThreddsUtilitiesException;
import thredds.server.metadata.util.ThreddsTranslatorUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import thredds.server.metadata.util.NCMLModifier;
import thredds.server.metadata.util.ThreddsExtentUtil;
import ucar.nc2.Attribute;
import ucar.nc2.Group;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.WrapperNetcdfFile;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.ncml.NcMLReader;

/**
 *
 * @author jwalker
 */
public final class NcMLUtil {
    
    private NcMLUtil(){}

    /**
     * Write an aggregation of some files to a single netCDF file
     * 
     * @param ncmlIn aggregation or wrapper NcML
     * @param outfile output netCDF file
     * @throws IOException 
     */
    public static File writeNetCDFFile(InputStream ncmlIn, String outfile) throws IOException {
        if (StringUtils.isBlank(outfile)) {
            throw new IllegalArgumentException("outfile cannot be blank or null");
        }
        
        NetcdfDataset dataset = NcMLReader.readNcML(ncmlIn, null);
        NetcdfFile referencedFile = dataset.getReferencedFile();
        NetcdfFileWriteable netcdfFileWriteable = NetcdfFileWriteable.createNew(outfile);
        
//        netcdfFileWriteable.
        
        File result = new File(outfile);
//        NetcdfFile netCdfFile = NetcdfFileWriteable.
//        
//        file.writeNcML(new FileOutputStream(result), outfile);
        return result;
    }

    /**
     * Read the global attributes of a dataset for retaining the history
     * and comments
     * @param inFile netcdf file to open (dataset should work)
     * @param attNcml 
     * @return 
     * @throws IOException 
     */
    public static Group globalAttributesToMeta(File inFile, WrapperNetcdfFile attNcml) throws
            IOException {
        NetcdfFile ncf = NetcdfFile.open(inFile.getPath());
        List<Attribute> globalAttributes = ncf.getGlobalAttributes();
        Group group = new Group(attNcml, attNcml.getRootGroup(), inFile.getName());
        for (Attribute att : globalAttributes) {
            group.addAttribute(att);
        }
        return group;
    }
    
    public static void globalAttributesToMeta(File inFile, Map<String, Set<String>> attrMap) throws IOException {
        NetcdfFile ncf = NetcdfFile.open(inFile.getPath());
        List<Attribute> globalAttributes = ncf.getGlobalAttributes();
        for (Attribute att : globalAttributes) {
            Set<String> attrVals = attrMap.get(att.getName());
            if (attrVals == null) {
                attrVals = Sets.newLinkedHashSet();
                attrMap.put(att.getName(), attrVals);
            }
            attrVals.add(att.getStringValue());
        }
    }

    /**
     * Uses ncISO to generate ncml, should be replaced by the wrapper work
     * and this should be moved to actually calling ncISO on the catalog.xml
     * 
     * @param filename NetCDF dataset location
     * @return ncml file created
     * @throws ThreddsUtilitiesException 
     */
    public static synchronized File createNcML(String catalogLocation) throws
            ThreddsUtilitiesException {
        File catalog = new File(catalogLocation);
        String ncmlName = catalog.getParent() + File.separator + "nciso.ncml";
        File ncmlFile = new File(ncmlName);
        if (!ncmlFile.exists()) {
            try {
                String ncmlPath = ncmlFile.getCanonicalPath();
                ncmlFile = ThreddsTranslatorUtil.getNcml(catalogLocation, ncmlPath);
                Extent extent = ThreddsExtentUtil.getExtent(ncmlPath);
                NCMLModifier ncmod = new NCMLModifier();
                Element rootElement = getRootElement(ncmlPath);
                ncmod.addCFMetadata(extent, rootElement);
                Document document = rootElement.getDocument();
                writeDocument(document, ncmlFile);
            }
            catch (Exception ex) {
                throw new ThreddsUtilitiesException(
                        "Difficulty writing ncml, check dataset",
                        ex,
                        ThreddsUtilitiesException.EXCEPTION_TYPES.IO_EXCEPTION);
            }
        }
        return ncmlFile;
    }
    
    public static File createAggregationWrapper(String dataset) {
        String datasetdir = FileHelper.dirAppend(FileHelper.getDatasetsDirectory(), dataset);
        String ncmlWrapperName = FileHelper.dirAppend(datasetdir, "aggregation.ncml");
        File ncml = new File(ncmlWrapperName);
        //TODO- If ncml variable doesn't exist, create NCML stub
//        if (!ncml.exists()) {
//            createNcmlStub();
//        }
        return null;
    }

    public static Element getRootElement(String filename) throws ThreddsUtilitiesException {
        try {
            Document doc = getDocument(filename);
            return doc.getRootElement();
        }
        catch (JDOMException ex) {
            throw new ThreddsUtilitiesException(
                    "JDOMException while getting root element",
                    ex,
                    ThreddsUtilitiesException.EXCEPTION_TYPES.IO_EXCEPTION);
        }
        catch (IOException ex) {
            throw new ThreddsUtilitiesException(
                    "IOException while getting root element",
                    ex,
                    ThreddsUtilitiesException.EXCEPTION_TYPES.IO_EXCEPTION);
        }

    }

    public static Document getDocument(String location) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(location);
    }
    
    public static void writeDocument(Document doc, File ncml) throws IOException {
        XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
        FileWriter writer = new FileWriter(ncml);
        output.output(doc, writer);
    }
}
