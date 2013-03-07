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
public class RenameSpec extends AbstractNcetlSpec {

    private static final long serialVersionUID = 1L;
    
    private static final String TABLE_NAME = "rename_mapping";
    
    public static final String ARCHIVE_ID = "archive_id";
    public static final String FROM_NAME = "from_name";
    public static final String TO_NAME = "to_name";
    
    private static Logger log = LoggerFactory.getLogger(RenameSpec.class);

    @Override
    public String setupTableName() {
        return TABLE_NAME;
    }

    @Override
    public ColumnMapping[] setupColumnMap() {
        return new ColumnMapping[] {
                    new ColumnMapping(ID, ID),
                    new ColumnMapping(ARCHIVE_ID, ARCHIVE_ID),
                    new ColumnMapping(FROM_NAME, FROM_NAME),
                    new ColumnMapping(TO_NAME, TO_NAME),
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
                    new SearchMapping("s_" + FROM_NAME, FROM_NAME, FROM_NAME, WhereClauseType.equals, null,
                                      null, null),
                    new SearchMapping("s_" + TO_NAME, TO_NAME, TO_NAME, WhereClauseType.equals, null,
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
    
    public static Map<String, String> getRenames(int id, Connection con) throws SQLException {
        Spec spec = new RenameSpec();
        Map<String, String[]> params = Maps.newHashMap();
        params.put(ARCHIVE_ID, new String[] { "" + id });
        Spec.loadParameters(spec, params);
        
        Map<String, String> renames = new HashMap<String, String>();
        
        ResultSet rs = Spec.getResultSet(spec, con);
        while (rs.next()) {
            String fromName = rs.getString(FROM_NAME);
            String toName = rs.getString(TO_NAME);
            if (!renames.containsKey(fromName)) {
                renames.put(fromName, toName);
            }
            else {
                throw new IllegalStateException("Renames must be one-to-one");
            }
        }
        return renames;
    }
   
}
