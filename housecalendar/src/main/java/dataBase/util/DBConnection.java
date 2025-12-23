package dataBase.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	
	private static String username;
	private static String password;
	private static String dbDriver;
	private static String dbURL;
	
	private static void init() {
	
			dbDriver= "org.sqlite.JDBC";
			dbURL= "jdbc:sqlite:/Users/serena/Documents/UniLavoro/UPO/Materie/IngegneriaSoftware/MieSlide/12)DBConnection/Biblioteca.db";
			username = "";
			password = "";	
	}

	public static Connection startConnection(Connection conn, String schema)
	{
		init();
		System.out.println(dbURL);

		if ( isOpen(conn) )
			closeConnection(conn);
	
		try 
		{
			dbURL=String.format(dbURL,schema); 
			System.out.println(dbURL);
			Class.forName(dbDriver);
			
			conn = DriverManager.getConnection(dbURL, username, password);// Apertura connessione
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}
		return conn;
	}

	public static boolean isOpen(Connection conn)
	{
		return (conn == null)?false:true;
	}

	public static Connection closeConnection(Connection conn)
	{
		if ( !isOpen(conn) )
			return null;
		try 
		{
			conn.close();
			conn = null;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
		return conn;
	}
}


