package com.island.louie.hive;

import com.cloudera.hiveserver2.hivecommon.core.IHadoopStatement;

import java.sql.*;

class HiveJDBCTest {

    public static void main(String[] args) throws Exception {
        Connection connection = null;
        String authMech = "1"; // 1:kerberos
        String host = "cdh2.cub.com.tw";
        String realm = "ISLAND.COM";
        String fqdn = "_HOST";
        String krbAuthType = "1"; //1:JAAS 2:Krb5_ccache
        String serviceName = "hive";
        String connUrl = "jdbc:hive2://" + host + ":10000;AuthMech=" + authMech
                + ";KrbRealm=" + realm + ";KrbHostFQDN=" + fqdn + ";KrbServiceName=" + serviceName + ";KrbAuthType=" + krbAuthType;
        connection = DriverManager.getConnection(connUrl);
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.unwrap(IHadoopStatement.class).executeAsync("SELECT * from sales limit 2");
        //ResultSet resultSet = statement.executeQuery("SELECT * from sales limit 2");

        ResultSetMetaData rsmd = resultSet.getMetaData();

        int columnsNumber = rsmd.getColumnCount();

        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));
            }
            System.out.println("");
        }
        connection.close();
    }

}
