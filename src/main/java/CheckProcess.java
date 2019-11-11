import java.io.IOException;
import java.io.InputStream;

public class CheckProcess {
    public Boolean checkSmevAdapter() {
        return checkProcess("smev-adapter");
    }

    public Boolean checkH2psql() {
        return checkProcess("h2psql");
    }

    private Boolean checkProcess(String processName) {
        Boolean result = false;
        Runtime runtime = Runtime.getRuntime();
        Process proc = null;
        InputStream inputStream = null;
        byte[] buff = new byte[512];
        try {
            proc = runtime.exec("ps axu | grep java");
            int status = proc.waitFor();
            if (status == 0) {
                while (inputStream.available() != 0) {
                    int count = inputStream.read(buff);
                    if (count > 0) {
                        String out = buff.toString();
                        if (out.contains(processName)) {
                            return true;
                        }
                    }
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
