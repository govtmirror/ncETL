package gov.usgs.cida.ncetl.spec;

import gov.usgs.webservices.jdbc.spec.Spec;
import gov.usgs.webservices.jdbc.spec.mapping.ColumnMapping;
import gov.usgs.webservices.jdbc.spec.mapping.SearchMapping;
import gov.usgs.webservices.jdbc.spec.mapping.WhereClauseType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import thredds.catalog.DataFormatType;

/**
 *
 * @author Ivan Suftin <isuftin@usgs.gov>
 */
public class DataFormatSpec extends AbstractNcetlSpec {
    private static final long serialVersionUID = 1L;
    
    private static final String TABLE_NAME = "data_format";
    public static final String TYPE = "type";

    @Override
    public String setupTableName() {
        return TABLE_NAME;
    }
    
    @Override
    public boolean setupAccess_DELETE() {
        return false;
    }

    @Override
    public boolean setupAccess_INSERT() {
        return false;
    }

    @Override
    public boolean setupAccess_UPDATE() {
        return false;
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
            new SearchMapping(ID, ID, null, WhereClauseType.equals, null, null, null),
            new SearchMapping("s_" + TYPE, TYPE, TYPE, WhereClauseType.equals, null, null, null)
        };
    }
    
    public static DataFormatType lookup(int id, Connection con) throws SQLException {
        Spec spec = new DataFormatSpec();
        Map<String, String[]> params = new HashMap<String, String[]>(1);
        params.put(ID, new String[] { "" + id });
        Spec.loadParameters(spec, params);
        ResultSet rs = Spec.getResultSet(spec, con);

        String type = null;
        if (rs.next()) {
            type = rs.getString(TYPE);
        }
        return DataFormatType.getType(type);
    }
}
