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
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jwalker
 */
public class ExcludeTypeSpec extends AbstractNcetlSpec {

    private static final long serialVersionUID = 1L;
    
    private static final String TABLE_NAME = "exclude_type";
    
    public static final String TYPE = "type";
    
    private static Logger log = LoggerFactory.getLogger(ExcludeTypeSpec.class);

    @Override
    public String setupTableName() {
        return TABLE_NAME;
    }

    @Override
    public ColumnMapping[] setupColumnMap() {
        return new ColumnMapping[] {
                    new ColumnMapping(ID, ID),
                    new ColumnMapping(TYPE, TYPE)
                };
    }

    @Override
    public SearchMapping[] setupSearchMap() {
        return new SearchMapping[] {
                    new SearchMapping(ID, ID, null, WhereClauseType.equals, null,
                                      null, null),
                    new SearchMapping("s_" + TYPE, TYPE, TYPE,
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
    
    public static String lookup(int id, Connection con) throws SQLException {
        Spec spec = new ExcludeTypeSpec();
        Map<String, String[]> params = Maps.newHashMap();
        params.put(ID, new String[] { "" + id });
        Spec.loadParameters(spec, params);
        ResultSet rs = Spec.getResultSet(spec, con);
        String type = null;
        if (rs.next()) {
            type = rs.getString(TYPE);
        }
        return type;
    }
   
}
