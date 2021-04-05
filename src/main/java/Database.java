import java.sql.*;
import java.util.Locale;

public class Database {
    private String url;
    private String user;
    private String pass;
    private Connection con;

    public Database(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    /**
     * Adds the key into a table
     * If it exists, then doesn't do anything
     * @param guildId The server's unique id
     * @throws SQLException
     */
    public void logKey(String guildId, String guildOwner) throws SQLException {
        con = DriverManager.getConnection(url, user, pass);
        Statement statement = con.createStatement();
        statement.executeUpdate("INSERT INTO table_template (guild_id, guild_owner) VALUES ('" + guildId + "', '" + guildOwner + "')"
            + "\nON DUPLICATE KEY UPDATE guild_id=guild_id");
    }

    public void setChannelId(String guilId) throws SQLException {
        con = DriverManager.getConnection(url, user, pass);
        Statement statement = con.createStatement();
        statement.executeUpdate("");
    }

    public void setCanvasUrl(String guildId, String canvasUrl) throws SQLException {
        con = DriverManager.getConnection(url, user, pass);
        Statement statement = con.createStatement();
        /**statement.executeUpdate("INSERT INTO table_template (canvas_url)\n"
            + "SELECT canvas_url FROM table_template WHERE guild_id='" + guildId + "'");**/
        statement.executeUpdate("UPDATE table_template SET canvas_url='" + canvasUrl + "' WHERE guild_id='" + guildId + "'");
    }

    public String getCanvasUrl(String guildId) throws SQLException {
        con = DriverManager.getConnection(url, user, pass);
        Statement statement = con.createStatement();
        ResultSet result = statement.executeQuery("SELECT canvas_url FROM table_template WHERE guild_id='" + guildId + "'");
        ResultSetMetaData rsmd = result.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        String resultId = "";

        // There should be just one result
        while(result.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                resultId = result.getString(i);
            }
        }

        if(resultId.equalsIgnoreCase("null")) return "";

        return resultId;
    }

    public void connect() throws SQLException {
        con = DriverManager.getConnection(url, user, pass);
    }

    public void close() throws SQLException {
        con.close();
        con = null;
    }

}
