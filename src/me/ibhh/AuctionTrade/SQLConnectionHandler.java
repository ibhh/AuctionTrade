package me.ibhh.AuctionTrade;

/**
 *
 */


import java.io.File;
import java.sql.*;
import org.bukkit.command.CommandSender;
import sun.security.util.BigInt;

/**
 * @author Simon
 *
 */
public class SQLConnectionHandler {

    private String dbPath;
    private String dbUser;
    private String dbPassword;
    private Connection cn;
    private boolean useMySQL;
    private AuctionTrade auTrade;

    public SQLConnectionHandler(AuctionTrade AuctTrade) {
        auTrade = AuctTrade;
        dbPath = auTrade.getConfig().getString("dbPath");
        dbUser = auTrade.getConfig().getString("dbUser");
        dbPassword = auTrade.getConfig().getString("dbPassword");
        cn = null;
        useMySQL = auTrade.getConfig().getBoolean("SQL");

    }

    public void PrepareDB() {
        Statement st = null;
        try {
            String sql = "CREATE TABLE IF NOT EXISTS AuctionTrade (ID VARCHAR PRIMARY KEY AUTO_INCREMENT  NOT NULL , Time BIGINT NOT NULL, sender VARCHAR NOT NULL, ItemId VARCHAR NOT NULL, Begin DOUBLE NOT NULL, End DOUBLE, Winner VARCHAR)";
            st = cn.createStatement();
            if (auTrade.getConfig().getBoolean("SQL")) {
                st.executeUpdate(sql);
                System.out.println("[AuctionTrade] Table created!");
            } else {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS AuctionTrade (ID VARCHAR PRIMARY KEY  NOT NULL, Time BIGINT, sender VARCHAR, ItemId VARCHAR, Begin DOUBLE, End DOUBLE, Winner VARCHAR)");
                System.out.println("[AuctionTrade] Table created!");
            }
            cn.commit();
            st.close();
        } catch (SQLException e) {
            System.out.println("[AuctionTrade]: Error while creating tables! - " + e.getMessage());
            SQLErrorHandler(e);
        }
        //	    UpdateDB();
    }

    public boolean InsertAuction(long Time, CommandSender sender, String ItemId, double anfang, double ende, String Winner) {

        try {
            PreparedStatement ps = cn.prepareStatement("INSERT INTO AuctionTrade  (Time, sender, ItemId, Begin, End, Winner) VALUES (?,?,?,?,?,?)");
            ps.setLong(1, Time);
            ps.setString(2, sender.getName());
            ps.setString(3, ItemId);
            ps.setDouble(4, anfang);
            ps.setDouble(5, ende);
            ps.setString(6, Winner);
            ps.executeUpdate();
            cn.commit();
            ps.close();
            return true;
        } catch (SQLException e) {
            System.out.println("[AuctionTrade] Error while inserting Auction into DB! - " + e.getMessage());
            SQLErrorHandler(e);
            return false;
        }
    }

    private boolean checkLogin() {
        if (!(dbPassword.equalsIgnoreCase("nulll") || dbUser.equalsIgnoreCase("nulll"))) {
            return true;
        }
        return false;
    }

    public Connection createConnection() {
        if (checkLogin()) {
            System.out.println("[AuctionTrade] could not be enabled: Failed to connect to DB: Check config settings dbPath, dbUser and dbPassword");
            return null;
        } else if (useMySQL) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                cn = DriverManager.getConnection("jdbc:mysql://" + dbPath, dbUser, dbPassword);
                cn.setAutoCommit(false);
                return cn;
            } catch (SQLException e) {
                System.out.println("[AuctionTrade] could not be enabled: Exception occured while trying to connect to DB");
                SQLErrorHandler(e);
                if (cn != null) {
                    System.out.println("[AuctionTrade] Old Connection still activated");
                    try {
                        cn.close();
                        System.out.println("[AuctionTrade] Old connection that was still activated has been successfully closed");
                    } catch (SQLException e2) {
                        System.out.println("[AuctionTrade] Failed to close old connection that was still activated");
                        SQLErrorHandler(e2);
                    }
                }
                return null;
            } catch (ClassNotFoundException e) {
                ErrorLogger(e.getMessage());
                return null;
            }
        } else if (!useMySQL) {
            try {
                try {
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException cs) {
                    ErrorLogger(cs.getMessage());
                }
                cn = DriverManager.getConnection("jdbc:sqlite:plugins"
                        + File.separator + "AuctionTrade" + File.separator
                        + "AuctionTrade.sqlite");
                cn.setAutoCommit(false);
                return cn;
            } catch (SQLException e) {
                SQLErrorHandler(e);
            }
        }
        return null;
    }

    private void ErrorLogger(String Error) {
        System.err.println("[AuctionTrade] Error:" + Error);
    }

    private void SQLErrorHandler(SQLException ex) {
        do {
            try {
                ErrorLogger("Exception Message: " + ex.getMessage());
                ErrorLogger("DBMS Code: " + ex.getErrorCode());
            } catch (Exception ne) {
                ErrorLogger(ne.getMessage());
            }
        } while ((ex = ex.getNextException()) != null);
    }

    public boolean CloseCon() {
        try {
            cn.close();
            return true;
        } catch (SQLException e) {
            System.out.println("[AuctionTrade] Failed to close connection to DB!");
            SQLErrorHandler(e);
            return false;
        }
    }

    public String[][] getAuction(int id) {
        Statement st = null;
        String sql;
        ResultSet result = null;
        try {
            st = cn.createStatement();
        } catch (SQLException e) {
            SQLErrorHandler(e);
        }
        sql = "SELECT * from AuctionTrade WHERE ID=" + id;
        try {
            result = st.executeQuery(sql);
        } catch (SQLException e1) {
            SQLErrorHandler(e1);
        }
        Long Time;
        String Time1;
        String sender;
        String ItemId;
        double anfang, ende;
        String anfang1, ende1;
        String Winner;
        int round = 1;
        String[][] ergebnis = null;
        try {
            while (result.next() == true) {
                Time = result.getLong("Time");
                sender = result.getString("sender");
                ItemId = result.getString("ItemId");
                anfang = result.getDouble("anfang");
                ende = result.getDouble("ende");
                Winner = result.getString("Winner");
                Time1 = String.valueOf( Time );
                ergebnis[round][0] = Time1;
                ergebnis[round][1] = sender;
                ergebnis[round][2] = ItemId;
                anfang1 = String.valueOf( anfang );
                ende1 = String.valueOf( ende );
                ergebnis[round][3] = anfang1;
                ergebnis[round][4] = ende1;
                ergebnis[round][5] = Winner;
                round++;
            }
        } catch (SQLException e2) {
            SQLErrorHandler(e2);
        }


        return ergebnis;
    }
}
