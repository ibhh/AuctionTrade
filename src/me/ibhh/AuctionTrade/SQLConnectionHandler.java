/**
 * 
 */
package me.ibhh.AuctionTrade;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.command.CommandSender;

/**
 * @author Simon
 * 
 */
public class SQLConnectionHandler 
{

	private String dbPath;
	private String dbUser;
	private String dbPassword;
	private Connection cn;
	private boolean useMySQL;
	private AuctionTrade auTrade;
	
	public SQLConnectionHandler(AuctionTrade AuctTrade)
	{	
		auTrade = AuctTrade;
		dbPath = auTrade.getConfig().getString("dbPath");
		dbUser = auTrade.getConfig().getString("dbUser");
		dbPassword = auTrade.getConfig().getString("dbPassword");
		cn = null;
		useMySQL = auTrade.getConfig().getBoolean("SQL");

	}
	
	public void PrepareDB()
	  {
	    Statement st = null;
	    try {
	      st = cn.createStatement();
	      if (auTrade.getConfig().getBoolean("SQL"))
	      {
	        st.executeUpdate("CREATE TABLE IF NOT EXISTS AuctionTrade (Time DOUBLE, sender CHAR(20), ItemId CHAR(20), Begin DOUBLE, End DOUBLE, Winner CHAR(20), PRIMARY KEY (ID))");
	        System.out.println("[AuctionTrade] Table created!");
	      }
	      else
	      {
	        st.executeUpdate("CREATE TABLE IF NOT EXISTS \"AuctionTrade\" (\"ID\" VARCHAR PRIMARY KEY  NOT NULL , \"Time\" DOUBLE, \"sender\" VARCHAR, \"ItemId\" VARCHAR, `Begin` DOUBLE, `End` DOUBLE, Winner VARCHAR)");
	        System.out.println("[AuctionTrade] Table created!");
	      }
	      cn.commit();
	      st.close();
	    }
	    catch (SQLException e)
	    {
	      System.out.println("[AuctionTrade]: Error while creating tables! - " + e.getMessage());
	      e.printStackTrace();
	    }
//	    UpdateDB();
	  }

	public boolean InsertAuction(int i, CommandSender sender, String ItemId, Date date, Date date1, String Winner) {

	    try {
	        PreparedStatement ps = cn.prepareStatement("INSERT INTO jail_prisoners  (Time, sender, ItemId, Begin, End, Winner) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
	        ps.setInt(1, i);
	        ps.setString(2, sender.getName());
	        ps.setString(3, ItemId);
	        ps.setDate(4, date);
	        ps.setDate(5, date1);
	        ps.setString(6, Winner);
	        ps.executeUpdate();
	        cn.commit();
	        ps.close();
	        return true;
	      } catch (SQLException e) {
	        System.out.println("[AuctionTrade] Error while inserting Prisoner into DB! - " + e.getMessage());
	        e.printStackTrace();
	        return false;
	      }
	}

	public Connection createConnection() {
		try {
			if ((dbPath == "unknown") || (dbUser == "unknown")
					|| (dbPassword == "unknown")) {
				System.out
						.println("[AuctionTrade] could not be enabled: Failed to connect to DB: Check config settings dbPath, dbUser and dbPassword");
				return null;
			} else if (useMySQL) {
				try {
					Class.forName("com.mysql.jdbc.Driver");
					cn = DriverManager.getConnection("jdbc:mysql://" + dbPath,
							dbUser, dbPassword);
				    cn.setAutoCommit(false);
					return cn;
				} catch (SQLException e) {
					System.out
							.println("[AuctionTrade] could not be enabled: Exception occured while trying to connect to DB");
					e.printStackTrace();
					if (cn != null) {
						System.out
								.println("[AuctionTrade] Old Connection still activated");

						try {
							cn.close();
							System.out
									.println("[AuctionTrade] Old connection that was still activated has been successfully closed");
						} catch (SQLException e2) {
							System.out
									.println("[AuctionTrade] Failed to close old connection that was still activated");
							e2.printStackTrace();
						}
					}
					return null;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				}
			} else if (!useMySQL) {
				Class.forName("org.sqlite.JDBC");
				cn = DriverManager.getConnection("jdbc:sqlite:plugins"
						+ File.separator + "AuctionTrade" + File.separator
						+ "AuctionTrade.sqlite");
			    cn.setAutoCommit(false);
				return cn;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean CloseCon() 
	{
		try {
			cn.close();
			return true;
		} catch (SQLException e) {
			System.out
					.println("[AuctionTrade] Failed to close connection to DB!");
			e.printStackTrace();
			return false;
		}
	}

}
