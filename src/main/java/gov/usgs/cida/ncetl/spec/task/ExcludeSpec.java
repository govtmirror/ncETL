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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jwalker
 */
public class ExcludeSpec extends AbstractNcetlSpec {

    private static final long serialVersionUID = 1L;
    
    private static final String TABLE_NAME = "exclude_mapping";
    
    public static final String ARCHIVE_ID = "archive_id";
    public static final String EXCLUDE_TYPE_ID = "exclude_type_id";
    public static final String EXCLUDE_TEXT = "exclude_text";
    
    private static Logger log = LoggerFactory.getLogger(ExcludeSpec.class);

    @Override
    public String setupTableName() {
        return TABLE_NAME;
    }

    @Override
    public ColumnMapping[] setupColumnMap() {
        return new ColumnMapping[] {
                    new ColumnMapping(ID, ID),
                    new ColumnMapping(ARCHIVE_ID, ARCHIVE_ID),
                    new ColumnMapping(EXCLUDE_TYPE_ID, EXCLUDE_TYPE_ID),
                    new ColumnMapping(EXCLUDE_TEXT, EXCLUDE_TEXT),
                    new ColumnMapping(INSERTED, null),
                    new ColumnMapping(UPDATED, null)
                };
    }

    @Override
    public SearchMapping[] setupSearchMap() {
        return new SearchMapping[] {
                    new SearchMapping(ID, ID, null, WhereClauseType.equals, null,
                                      null, null),
                    new SearchMapping("s_" + ARCHIVE_ID, ARCHIVE_ID, ARCHIVE_ID, WhereClauseType.equals, null,
                                      null, null),
                    new SearchMapping("s_" + EXCLUDE_TYPE_ID, EXCLUDE_TYPE_ID, EXCLUDE_TYPE_ID, WhereClauseType.equals, null,
                                      null, null),
                    new SearchMapping("s_" + EXCLUDE_TEXT, EXCLUDE_TEXT, EXCLUDE_TEXT, WhereClauseType.equals, null,
                                      null, null),
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
    
    public static Map<String, List<String>> getExcludes(int id, Connection con) throws SQLException {
        Spec spec = new ExcludeSpec();
        Map<String, String[]> params = Maps.newHashMap();
        params.put(ARCHIVE_ID, new String[] { "" + id });
        Spec.loadParameters(spec, params);
        
        Map<String, List<String>> excludes = new HashMap<String, List<String>>();
        
        ResultSet rs = Spec.getResultSet(spec, con);
        while (rs.next()) {
            String type = ExcludeTypeSpec.lookup(rs.getInt(EXCLUDE_TYPE_ID), con);
            String excludeText = rs.getString(EXCLUDE_TEXT);
            if (excludes.containsKey(type)) {
                excludes.get(type).add(excludeText);
            }
            else {
                List<String> exList = new LinkedList<String>();
                exList.add(excludeText);
                excludes.put(type, exList);
            }
        }
        return excludes;
    }
   
}
