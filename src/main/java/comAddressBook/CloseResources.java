package comAddressBook;

import java.sql.*;

/**
 * to close the resources from jdbc
 */
public class CloseResources {

    /**
     * to close Statement
     * @param statement
     * @throws SQLException
     */
    public static void closeStatement(Statement statement) throws SQLException {
        statement.close();
    }

    /**
     * to close PreparedStatement
     */
    public static void closePreparedStatement(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.close();
    }

    /**
     * to close CallableStatement
     * @param callableStatement
     * @throws SQLException
     */
    public static void closeCallableStatement(CallableStatement callableStatement) throws SQLException {
        callableStatement.close();
    }

    /**
     * to close Connection
     * @param connection
     * @throws SQLException
     */
    public static void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }
}

