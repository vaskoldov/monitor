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

    private String signFNSAlias;
    private String signEGRNAlias;
    private String signISAlias;

    public MonitorThread(Properties props) {
        // Параметры процесса мониторинга
        statusFileName = props.getProperty("LOG_FILE");
        isRunnable = true;
        interval = Long.parseLong(props.getProperty("INTERVAl"));
        // Параметры проверяемых подписей
        signFNSAlias = props.getProperty("FNS_SIGN_ALIAS");
        signEGRNAlias = props.getProperty("EGRN_SIGN_ALIAS");
        signISAlias = props.getProperty("IS_SIGN_ALIAS");
    }
    public void run() {
        CheckProcess checkProcess = new CheckProcess();
        CheckFreeSpace checkFreeSpace = new CheckFreeSpace();
        CheckExpired checkExpired = new CheckExpired();
        CheckKeyContainers checkKeyContainers = new CheckKeyContainers();
        while (isRunnable) {
            File statusFile = new File(statusFileName);
            FileWriter fw = null;
            try {
                fw = new FileWriter(statusFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Calendar now = Calendar.getInstance();
            try {
                fw.write("CreatedAt=");
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                fw.write(df.format(now.getTime()));
                fw.write("\n");
                fw.write("PHPStatus=");
                String PHPStatus = checkProcess.checkH2psql() ? "true" : "false";
                fw.write(PHPStatus);
                fw.write("\n");
                fw.write("SMEVAdapterStatus=");
                String SMEVAdapterStatus = checkProcess.checkAdapter() ? "true" : "false";
                fw.write(SMEVAdapterStatus);
                fw.write("\n");
                fw.write("IsPresentSystemSign=");
                String systemSignStatus = checkKeyContainers.isSignAvailable(signISAlias) ? "true" : "false";
                fw.write(systemSignStatus);
                fw.write("\n");
                fw.write("ValidTillSystemSign=");
                fw.write(checkKeyContainers.signValidTill(signISAlias));
                fw.write("\n");
                fw.write("IsPresentUserSign=");
                String fnsSignStatus = checkKeyContainers.isSignAvailable(signFNSAlias) ? "true" : "false";
                fw.write(fnsSignStatus);
                fw.write("\n");
                fw.write("ValidTillUserSign=");
                fw.write(checkKeyContainers.signValidTill(signFNSAlias));
                fw.write("\n");
                fw.write("IsPresentEGRNSign=");
                String egrnSignStatus = checkKeyContainers.isSignAvailable(signEGRNAlias) ? "true" : "false";
                fw.write(egrnSignStatus);
                fw.write("\n");
                fw.write("ValidTillEGRNSign=");
                fw.write(checkKeyContainers.signValidTill(signEGRNAlias));
                fw.write("\n");
                fw.write("SMEVConnectionStatus=true");
                fw.write("\n");
                fw.write("FreeSpaceStatus=");
                String usedSpace = Integer.toString(checkFreeSpace.checkFreeSpace());
                fw.write(usedSpace + "%");
                fw.write("\n");
                fw.write("SentRequestGap=");
                fw.write(Integer.toString(checkExpired.getEarliestSentDate()));
                fw.write("\n");
                fw.flush();
                // Засыпаем на определенное время
                sleep(interval);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
