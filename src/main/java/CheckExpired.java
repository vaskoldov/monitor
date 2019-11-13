import java.sql.*;

public class CheckExpired {
    private static final String pgURL = "jdbc:postgresql://localhost:5432/smev_adapter";
    private Connection connection = null;
    private PreparedStatement earliestSent = null;

    public CheckExpired() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(pgURL, "smev", "smev");
            String sql = "SELECT MIN(send_timestamp) FROM \"FSOR01_3T\".log WHERE status = 'SENT';";
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
