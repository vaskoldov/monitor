import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class MonitorThread extends Thread {
    private String statusFileName;
    private boolean isRunnable;
    private Long interval;

    private String mnemonic;
    private String signFNSAlias;
    private String signEGRNAlias;
    private String signISAlias;
    private String smevURL;

    public MonitorThread(Properties props) {
        // Параметры процесса мониторинга
        mnemonic = props.getProperty("MNEMONIC");
        statusFileName = props.getProperty("LOG_FILE");
        isRunnable = true;
        interval = Long.parseLong(props.getProperty("INTERVAl"));
        // Параметры проверяемых подписей
        signFNSAlias = props.getProperty("FNS_SIGN_ALIAS");
        signEGRNAlias = props.getProperty("EGRN_SIGN_ALIAS");
        signISAlias = props.getProperty("IS_SIGN_ALIAS");
        smevURL = props.getProperty("TRANSPORT_URL");
    }
    public void run() {
        CheckProcess checkProcess = new CheckProcess();
        CheckFreeSpace checkFreeSpace = new CheckFreeSpace();
        CheckExpired checkExpired = new CheckExpired(mnemonic);
        CheckKeyContainers checkKeyContainers = new CheckKeyContainers();
        CheckSMEVService checkSMEVService = new CheckSMEVService();
        while (isRunnable) {
            StringBuilder results = new StringBuilder();
            Calendar now = Calendar.getInstance();
            try {
                results.append("CreatedAt=");
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                results.append(df.format(now.getTime()));
                results.append("\n");
                results.append("PHPStatus=");
                String PHPStatus = checkProcess.checkH2psql() ? "true" : "false";
                results.append(PHPStatus);
                results.append("\n");
                results.append("SMEVAdapterStatus=");
                String SMEVAdapterStatus = checkProcess.checkAdapter() ? "true" : "false";
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
                String usedSpace = Integer.toString(checkFreeSpace.checkFreeSpace());
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
                e.printStackTrace();
            }
        }
    }
}
