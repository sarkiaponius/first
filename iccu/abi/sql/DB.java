package it.sbn.iccu.abi.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Description of the Class */

public class DB
{
	public Connection conn;
	public Connection connTest;
	public static String urlTest = "jdbc:postgresql://192.168.20.46:5432/abi_test3";
	public static String urlEsercizio = "jdbc:postgresql://anagrafe.iccu.sbn.it:5432/abi2";
	public static String urlEsercizioGiuliano = "jdbc:postgresql://192.168.20.131:5432/abi2";

	private void err(String str)
	{
		System.err.println(str);
	}

	public PreparedStatement prepare(String sql)
	{
		PreparedStatement temp = null;
		try
		{
			temp = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	public ResultSet select(String query)
	{
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(query);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return rs;
	}

	/*
	 * Costruttore parametrico
	 */
	public DB(String url)
	{
		try
		{
			Class.forName("org.postgresql.Driver");
		}
		catch(Exception e)
		{
			err("ERROR: failed to load JDBC driver.");
		}
		try
		{
			conn = DriverManager.getConnection(url, "andrea", "andrea");
			if(conn == null)
			{
				err("Connessione nulla!");
			}
		}
		catch(SQLException e)
		{
			err("ERROR: failed to connect!");
		}
	}

	public DB()
	{
		try
		{
			Class.forName("org.postgresql.Driver");
		}
		catch(Exception e)
		{
			err("ERROR: failed to load JDBC driver.");
		}
		// String url = "jdbc:postgresql://192.168.20.131:5432/abi2";
		String url = "jdbc:postgresql://anagrafe.iccu.sbn.it:5432/abi2";
		try
		{
			conn = DriverManager.getConnection(url, "andrea", "andrea");
			if(conn == null)
			{
				err("Connessione nulla!");
			}
		}
		catch(SQLException e)
		{
			err("ERROR: failed to connect!");
		}
	}

	public void free()
	{
		try
		{
			conn.close();
			if(connTest != null)
				connTest.close();
		}
		catch(SQLException e)
		{
			this.err("ERROR: Fetch statement failed: " + e.getMessage());
			this.err(e.toString());
		}
	}
}
