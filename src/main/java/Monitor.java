import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Monitor {
    public static final String statusFileName = "/home/smev/SystemStatus.log";

    public static void main(String[] args) {
        CheckProcess checkProcess = new CheckProcess();
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
            String PHPStatus = checkProcess.checkH2psql()?"true":"false";
            fw.write(PHPStatus);
            fw.write("\n");
            fw.write("SMEVAdapterStatus=");
            String SMEVAdapterStatus = checkProcess.checkSmevAdapter()?"true":"false";
            fw.write(SMEVAdapterStatus);
            fw.write("\n");
            fw.write("IsPresentSystemSign=");
            fw.write("\n");
            fw.write("ValidTillSystemSign=");
            fw.write("\n");
            fw.write("IsPresentUserSign=");
            fw.write("\n");
            fw.write("ValidTillUserSign=");
            fw.write("\n");
            fw.write("IsPresentEGRNSign=");
            fw.write("\n");
            fw.write("ValidTillEGRNSign=");
            fw.write("\n");
            fw.write("SMEVConnectionStatus=");
            fw.write("\n");
            fw.write("FreeSpaceStatus=");
            fw.write("\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
