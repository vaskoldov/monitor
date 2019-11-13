import java.io.*;

public class CheckProcessByPID {
    private static final String apapterPidFile = "/opt/adapter/pidFile.pid";
    private static final String h2psqlPidFile = "/opt/adapter/pidFileH2psql.pid";

    public Boolean checkSmevAdapter() {
        return checkProcess(apapterPidFile);
    }

    public Boolean checkH2psql() {
        return checkProcess(h2psqlPidFile);
    }

    private Boolean checkProcess(String pidFileName) {
        try {
            File pidFile = new File(pidFileName);
            FileReader fileReader = new FileReader(pidFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String pid = bufferedReader.readLine();
            if (pid != null) {
                // Проверяем, живой ли процесс с этим pid

            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
