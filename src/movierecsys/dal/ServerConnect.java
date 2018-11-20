/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movierecsys.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.Connection;

/**
 *
 * @author thor1
 */
public class ServerConnect {

    private static final String serverName = "10.176.111.x";
    private static final String databaseName = "movie";
    private static final String userName = "CS2018A_x";
    private static final String userPassword = "CS2018A_x";
    private final SQLServerDataSource ds;

    public ServerConnect() {
        this.ds = new SQLServerDataSource();
        ds.setServerName(serverName);
        ds.setDatabaseName(databaseName);
        ds.setUser(userName);
        ds.setPassword(userPassword);
    }

    public Connection getConnection() throws SQLServerException {
        return ds.getConnection();
    }

}
