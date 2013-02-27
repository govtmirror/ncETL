package gov.usgs.cida.ncetl.spec;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gov.usgs.cida.ncetl.ingest.FTPIngestTask;
import gov.usgs.webservices.jdbc.spec.Spec;
import gov.usgs.webservices.jdbc.spec.mapping.ColumnMapping;
import gov.usgs.webservices.jdbc.spec.mapping.SearchMapping;
import gov.usgs.webservices.jdbc.spec.mapping.WhereClauseType;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    
    public static final String INPUT_DIR = "inputDir";
    public static final String OUTPUT_DIR = "outputDir";
    public static final String COMPLETE_DIR = "completeDir";
    public static final String RFC_CODE = "rfcCode";
    public static final String FILE_REGEX = "fileRegex";
    public static final String DIM_EXLUDES = "dimExclude";
    public static final String VAR_EXLUDES = "varExclude";
    public static final String XY_EXLUDES = "xyExclude";
    public static final String UNLIM_DIM = "unlimitedDim";
    public static final String UNLIM_UNITS = "unlimitedUnits";
    public static final String RENAME_FROM_VAR = "fromVar";
    public static final String RENAME_TO_VAR = "toVar";
    
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
                    new ColumnMapping(INSERTED, null),
                    new ColumnMapping(UPDATED, null)
                };
    }

    @Override
    public SearchMapping[] setupSearchMap() {
        return new SearchMapping[] {
                    new SearchMapping(ID, ID, null, WhereClauseType.equals, null,
                                      null, null),
                    new SearchMapping("s_" + FILE_REGEX, FILE_REGEX, FILE_REGEX,
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
        // TODO For new rows, start up ingestor
        return rs;
    }

    @Override
    public ResultSet getUpdatedRows(Connection con) throws SQLException {
        ResultSet rs = super.getUpdatedRows(con);
        // TODO for updated rows, restart ingestors
        return rs;
    }
   
}
