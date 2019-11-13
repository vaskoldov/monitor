import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MonitorThread extends Thread {
    public static final String statusFileName = "/opt/adapter/SystemStatus.log";
    public static final Boolean isRunnable = true;
    public static final Long interval = 300000L;

    public void run() {
        CheckProcess checkProcess = new CheckProcess();
        CheckFreeSpace checkFreeSpace = new CheckFreeSpace();
        CheckExpired checkExpired = new CheckExpired();
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
                fw.write("IsPresentSystemSign=true");
                fw.write("\n");
                fw.write("ValidTillSystemSign=09/01/2020");
                fw.write("\n");
                fw.write("IsPresentUserSign=true");
                fw.write("\n");
                fw.write("ValidTillUserSign=07/03/2020");
                fw.write("\n");
                fw.write("IsPresentEGRNSign=true");
                fw.write("\n");
                fw.write("ValidTillEGRNSign=26/06/2020");
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
