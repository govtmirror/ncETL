package gov.usgs.cida.ncetl.mocks;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Masquerades as a connection to a database, allowing for preconstruction of results
 * using storeMockResult. Conditional branching is done based on table name and id.
 *
 * @author M. Herold
 */
public class MockConnection implements Connection {
    
    private static final Integer TABLE_NAME_INDEX = 3;
    private static final Integer DEFAULT_ID = 0;
    
    private Map<String, MockResultSet> mrsMap;
    
    public MockConnection() {
        this.mrsMap = new HashMap<String, MockResultSet>();
    }
    
    /*
     * Store the mock result (Map) into the MockResultSet (mrs)
     * and associate it with a table name (String) and id (Integer).
     */
    public void storeMockResult(Map<String, Object> mr, String tableName) {
        this.storeMockResult(mr, tableName, DEFAULT_ID);
    }
    
    /*
     * Store the mock result (Map) into the MockResultSet (mrs)
     * and associate it with a table name (String) and id (Integer).
     */
    public void storeMockResult(Map<String, Object> mr, String tableName, Integer id) {
        String index = tableName + id;
        MockResultSet mrs = this.mrsMap.get(index);
        if(mrs == null)
            mrs = new MockResultSet();
        mrs.pushResult(mr);
        mrsMap.put(index, mrs);
    }

    @Override
    public Statement createStatement() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
     * Create and return the MockPreparedStatement. This statement holds the MockResultSet mapping
     * associated with a given table in the query and an id retrieved later.
     */
    @Override
    public PreparedStatement prepareStatement(String query) throws SQLException {
        String delims = "[ ]+";
        String[] tokens = query.split(delims);
        String tableName = tokens[TABLE_NAME_INDEX];
        return new MockPreparedStatement(mrsMap, tableName);
    }

    @Override
    public CallableStatement prepareCall(String string) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String nativeSQL(String string) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAutoCommit(boolean bln) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void commit() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rollback() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isClosed() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setReadOnly(boolean bln) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCatalog(String string) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getCatalog() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTransactionIsolation(int i) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Statement createStatement(int i, int i1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PreparedStatement prepareStatement(String string, int i, int i1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CallableStatement prepareCall(String string, int i, int i1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setHoldability(int i) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Savepoint setSavepoint(String string) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rollback(Savepoint svpnt) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void releaseSavepoint(Savepoint svpnt) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Statement createStatement(int i, int i1, int i2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PreparedStatement prepareStatement(String string, int i, int i1, int i2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CallableStatement prepareCall(String string, int i, int i1, int i2) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PreparedStatement prepareStatement(String string, int i) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PreparedStatement prepareStatement(String string, int[] ints) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PreparedStatement prepareStatement(String string, String[] strings) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isValid(int i) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setClientInfo(String string, String string1) throws SQLClientInfoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setClientInfo(Properties prprts) throws SQLClientInfoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getClientInfo(String string) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Array createArrayOf(String string, Object[] os) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Struct createStruct(String string, Object[] os) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T unwrap(Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWrapperFor(Class<?> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSchema(String arg0) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSchema() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void abort(Executor arg0) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setNetworkTimeout(Executor arg0, int arg1) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
