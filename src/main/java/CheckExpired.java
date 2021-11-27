import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckExpired {
    private static Logger LOG = LoggerFactory.getLogger(CheckExpired.class.getName());
    private Connection connection = null;
    private PreparedStatement earliestSent = null;

    public CheckExpired(String dbConnectionString, String mnemonic, String dbUser, String dbPass) {
        LOG.info("Проверка наличия просроченных ответов");
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(dbConnectionString, dbUser, dbPass);
            String sql = String.format("SELECT MIN(send_timestamp) FROM \"%s\".log WHERE status = 'SENT';", mnemonic);
            earliestSent = connection.prepareStatement(sql);
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage());
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
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
