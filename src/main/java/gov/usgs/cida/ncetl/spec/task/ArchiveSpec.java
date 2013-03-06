package gov.usgs.cida.ncetl.spec.task;

import com.google.common.collect.Maps;
import gov.usgs.cida.ncetl.spec.AbstractNcetlSpec;
import gov.usgs.webservices.jdbc.spec.Spec;
import gov.usgs.webservices.jdbc.spec.mapping.ColumnMapping;
import gov.usgs.webservices.jdbc.spec.mapping.SearchMapping;
import gov.usgs.webservices.jdbc.spec.mapping.WhereClauseType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jwalker
 */
public class ArchiveSpec extends AbstractNcetlSpec {

    private static final long serialVersionUID = 1L;
    
    private static final String TABLE_NAME = "archive_config";
    
    public static final String INPUT_DIR = "input_dir";
    public static final String OUTPUT_DIR = "output_dir";
    public static final String COMPLETE_DIR = "complete_dir";
    public static final String RFC_CODE = "rfc_code";
    public static final String FILE_REGEX = "file_regex";
    public static final String UNLIM_DIM = "unlimited_dim";
    public static final String UNLIM_UNITS = "unlimited_units";
    
    public static final String DIM_EXCLUDES = "dim";
    public static final String VAR_EXCLUDES = "var";
    public static final String XY_EXCLUDES = "xy";
    public static final String RENAMES = "renames";
    
    private static Logger log = LoggerFactory.getLogger(ArchiveSpec.class);

    @Override
    public String setupTableName() {
        return TABLE_NAME;
    }

    @Override
    public ColumnMapping[] setupColumnMap() {
        return new ColumnMapping[] {
                    new ColumnMapping(ID, ID),
                    new ColumnMapping(INPUT_DIR, INPUT_DIR),
                    new ColumnMapping(OUTPUT_DIR, OUTPUT_DIR),
                    new ColumnMapping(COMPLETE_DIR, COMPLETE_DIR),
                    new ColumnMapping(RFC_CODE, RFC_CODE),
                    new ColumnMapping(FILE_REGEX, FILE_REGEX),
                    new ColumnMapping(UNLIM_DIM, UNLIM_DIM),
                    new ColumnMapping(UNLIM_UNITS, UNLIM_UNITS),
                    new ColumnMapping(INSERTED, null),
                    new ColumnMapping(UPDATED, null)
                };
    }

    @Override
    public SearchMapping[] setupSearchMap() {
        return new SearchMapping[] {
                    new SearchMapping(ID, ID, null, WhereClauseType.equals, null,
                                      null, null),
                    new SearchMapping("s_" + INPUT_DIR, INPUT_DIR, INPUT_DIR,
                                      WhereClauseType.equals, null, null, null),
                    new SearchMapping("s_" + OUTPUT_DIR, OUTPUT_DIR, OUTPUT_DIR,
                                      WhereClauseType.equals, null, null, null),
                    new SearchMapping("s_" + COMPLETE_DIR, COMPLETE_DIR, COMPLETE_DIR,
                                      WhereClauseType.equals, null, null, null),
                    new SearchMapping("s_" + RFC_CODE, RFC_CODE, RFC_CODE,
                                      WhereClauseType.equals, null, null, null),
                    new SearchMapping("s_" + FILE_REGEX, FILE_REGEX, FILE_REGEX,
                                      WhereClauseType.equals, null, null, null),
                    new SearchMapping("s_" + UNLIM_DIM, UNLIM_DIM, UNLIM_DIM,
                                      WhereClauseType.equals, null, null, null),
                    new SearchMapping("s_" + UNLIM_UNITS, UNLIM_UNITS, UNLIM_UNITS,
                                      WhereClauseType.equals, null, null, null),
                    new SearchMapping("s_" + INSERTED, INSERTED, INSERTED,
                                      WhereClauseType.equals, null, null, null),
                    new SearchMapping("s_" + UPDATED, UPDATED, UPDATED,
                                      WhereClauseType.equals, null, null, null)
                };
    }

    @Override
    public ResultSet getInsertedRows(Connection con) throws SQLException {
        ResultSet rs = super.getInsertedRows(con);
        return rs;
    }

    @Override
    public ResultSet getUpdatedRows(Connection con) throws SQLException {
        ResultSet rs = super.getUpdatedRows(con);
        return rs;
    }
    
    public static Map<String, Object> getArchiveParams(int id, Connection con) throws SQLException {
        Spec spec = new ArchiveSpec();
        Map<String, String[]> params = Maps.newHashMap();
        params.put(ID, new String[] { "" + id });
        Spec.loadParameters(spec, params);
        
        Map<String, Object> returnMap = new HashMap<String, Object>();
        
        ResultSet rs = Spec.getResultSet(spec, con);
        if (rs.next()) {
            returnMap.put(INPUT_DIR, rs.getString(INPUT_DIR));
            returnMap.put(OUTPUT_DIR, rs.getString(OUTPUT_DIR));
            returnMap.put(COMPLETE_DIR, rs.getString(COMPLETE_DIR));
            returnMap.put(RFC_CODE, rs.getInt(RFC_CODE));
            returnMap.put(FILE_REGEX, rs.getString(FILE_REGEX));
            returnMap.put(UNLIM_DIM, rs.getString(UNLIM_DIM));
            returnMap.put(UNLIM_UNITS, rs.getString(UNLIM_UNITS));
            
            Map<String, List<String>> excludes = ExcludeSpec.getExcludes(id, con);
            Map<String, String> renames = RenameSpec.getRenames(id, con);
            
            returnMap.put(DIM_EXCLUDES, excludes.get(DIM_EXCLUDES));
            returnMap.put(VAR_EXCLUDES, excludes.get(VAR_EXCLUDES));
            returnMap.put(XY_EXCLUDES, excludes.get(XY_EXCLUDES));
            returnMap.put(RENAMES, renames);
        }
        return returnMap;
    }
   
}
