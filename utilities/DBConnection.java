package com.tribu.qaselenium.testframework.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

	public static Connection getConnection(String dataBaseUrl, String dataBase, String user , String pass) throws SQLException {
		String jdbc_Url = "jdbc:mysql://" + dataBaseUrl + ":3306/" + dataBase
				+ "?verifyServerCertificate=false" + "&useSSL=true" + "&requireSSL=true";
		String jdbc_User = user;
		String jdbc_Password = pass;
		return DriverManager.getConnection(jdbc_Url, jdbc_User, jdbc_Password);
	}

	public static void close(ResultSet rs) throws SQLException {
		rs.close();
	}

	public static void close(PreparedStatement stmt) throws SQLException {
		stmt.close();
	}

	public static void close(Connection conn) throws SQLException {
		conn.close();
	}
}
