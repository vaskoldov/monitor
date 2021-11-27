import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorThread extends Thread {
    private static Logger LOG = LoggerFactory.getLogger(MonitorThread.class.getName());
    private String statusFileName;
    private boolean isRunnable;
    private Long interval;

    private String signFNSAlias;
    private String signEGRNAlias;
    private String signISAlias;
    private String smevURL;
    private String diskPartition;

    private String db_host;
    private String db_port;
    private String db_name;
    private String db_schema;
    private String db_user;
    private String db_pass;
    private String db_connection_string;

    private String kubernetesIP;

    public MonitorThread(Properties props) {
        // Параметры процесса мониторинга
        statusFileName = props.getProperty("LOG_FILE");
        isRunnable = true;
        interval = Long.parseLong(props.getProperty("INTERVAl"));
        diskPartition = props.getProperty("DISK");
        kubernetesIP = props.getProperty("ADAPTER_IP");

        // Параметры проверяемых подписей
        signFNSAlias = props.getProperty("FNS_SIGN_ALIAS");
        signEGRNAlias = props.getProperty("EGRN_SIGN_ALIAS");
        signISAlias = props.getProperty("IS_SIGN_ALIAS");
        smevURL = props.getProperty("TRANSPORT_URL");

        // Параметры базы данных
        db_host = props.getProperty("DB_HOST");
        db_port = props.getProperty("DB_PORT");
        db_name = props.getProperty("DB_NAME");
        db_schema = props.getProperty("DB_SCHEMA");
        db_user = props.getProperty("DB_USER");
        db_pass = props.getProperty("DB_PASS");
        db_connection_string = "jdbc:postgresql://" + db_host + ":" + db_port + "/" + db_name;
        LOG.info("Загружены параметры монитора:");
        LOG.info(String.format("statusFileName: %s", statusFileName));
        LOG.info(String.format("interval: %s", interval));
        LOG.info(String.format("diskPartition: %s", diskPartition));
        LOG.info(String.format("kubernetesIP: %s", kubernetesIP));
        LOG.info(String.format("signFNSAlias: %s", signFNSAlias));
        LOG.info(String.format("signEGRNAlias: %s", signEGRNAlias));
        LOG.info(String.format("signISAlias: %s", signISAlias));
        LOG.info(String.format("smevURL: %s", smevURL));
        LOG.info(String.format("db_host: %s", db_host));
        LOG.info(String.format("db_port: %s", db_port));
        LOG.info(String.format("db_name: %s", db_name));
        LOG.info(String.format("db_user: %s", db_user));
        LOG.info(String.format("db_pass: %s", db_pass));
        LOG.info(String.format("db_connection_string: %s", db_connection_string));
    }
    public void run() {
        CheckProcess checkProcess = new CheckProcess();
        LOG.info("Инициирован CheckProcess");
        CheckFreeSpace checkFreeSpace = new CheckFreeSpace();
        LOG.info("Инициирован CheckFreeSpace");
        CheckExpired checkExpired = new CheckExpired(db_connection_string, db_schema, db_user, db_pass);
        LOG.info("Инициирован CheckExpired");
        CheckKeyContainers checkKeyContainers = new CheckKeyContainers();
        LOG.info("Инициирован CheckKeyContainers");
        CheckSMEVService checkSMEVService = new CheckSMEVService();
        LOG.info("Инициирован CheckKeyContainers");
        while (isRunnable) {
            StringBuilder results = new StringBuilder();
            Calendar now = Calendar.getInstance();
            try {
                results.append("CreatedAt=");
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                results.append(df.format(now.getTime()));
                results.append("\n");
                results.append("PHPStatus=");
                String PHPStatus = checkProcess.checkConverter() ? "true" : "false";
                results.append(PHPStatus);
                results.append("\n");
                results.append("SMEVAdapterStatus=");
                String SMEVAdapterStatus = checkProcess.checkAdapter(kubernetesIP) ? "true" : "false";
                results.append(SMEVAdapterStatus);
                results.append("\n");
                results.append("IsPresentSystemSign=");
                String systemSignStatus = checkKeyContainers.isSignAvailable(signISAlias) ? "true" : "false";
                results.append(systemSignStatus);
                results.append("\n");
                results.append("ValidTillSystemSign=");
                results.append(checkKeyContainers.signValidTill(signISAlias));
                results.append("\n");
                results.append("IsPresentUserSign=");
                String fnsSignStatus = checkKeyContainers.isSignAvailable(signFNSAlias) ? "true" : "false";
                results.append(fnsSignStatus);
                results.append("\n");
                results.append("ValidTillUserSign=");
                results.append(checkKeyContainers.signValidTill(signFNSAlias));
                results.append("\n");
                results.append("IsPresentEGRNSign=");
                String egrnSignStatus = checkKeyContainers.isSignAvailable(signEGRNAlias) ? "true" : "false";
                results.append(egrnSignStatus);
                results.append("\n");
                results.append("ValidTillEGRNSign=");
                results.append(checkKeyContainers.signValidTill(signEGRNAlias));
                results.append("\n");
                results.append("SMEVConnectionStatus=");
                String smevStatus = checkSMEVService.checkTransport(smevURL) ? "true" : "false";
                results.append(smevStatus);
                results.append("\n");
                results.append("FreeSpaceStatus=");
                String usedSpace = Integer.toString(checkFreeSpace.checkFreeSpace(diskPartition));
                results.append(usedSpace + "%");
                results.append("\n");
                results.append("SentRequestGap=");
                results.append(Integer.toString(checkExpired.getEarliestSentDate()));
                results.append("\n");
                // Скидываем результаты мониторинга в файл
                StatusFileWriter.writeResults(statusFileName, results.toString());
                // Засыпаем на определенное время
                sleep(interval);
            } catch (IOException | InterruptedException e) {
                LOG.error(e.getMessage());
                continue;
                //System.exit(1);
            }
        }
    }
}
