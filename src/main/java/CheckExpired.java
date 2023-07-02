import java.sql.*;
import java.util.Properties;

public class CheckExpired {
    private Connection connection = null;
    private PreparedStatement earliestSent = null;

    public CheckExpired(Properties props) {
        try {
            Class.forName("org.postgresql.Driver");
            String pgURL = props.getProperty("DATABASE_URL");
            String pgUser = props.getProperty("DATABASE_USER");
            String pgPassword = props.getProperty("DATABASE_PASS");
            String mnemonic = props.getProperty("MNEMONIC");
            connection = DriverManager.getConnection(pgURL, pgUser, pgPassword);
            String sql = String.format("SELECT MIN(send_timestamp) FROM \"%s\".log WHERE status = 'SENT';", mnemonic);
            earliestSent = connection.prepareStatement(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public int getEarliestSentDate() {
        try {
            ResultSet resultSet = earliestSent.executeQuery();
            while (resultSet.next()) {
                Timestamp earliestTimestamp = resultSet.getTimestamp(1);
                if (earliestTimestamp == null) {
                    return 0;
                }
                java.util.Date now = new java.util.Date();
                long milliseconds = now.getTime() - earliestTimestamp.getTime();
                return (int) (milliseconds / (24 * 60 * 60 * 1000));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
