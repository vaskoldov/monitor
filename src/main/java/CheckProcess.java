import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CheckProcess {
    public boolean checkAdapter() {
        return checkProcess("bpm-service");
    }

    public boolean checkH2psql() {
        return checkProcess("converter");
    }

    private boolean checkProcess(String processName) {
        try {
            String line;
            Process process = new ProcessBuilder().command("bash", "-c", "ps axu | grep java").start();
            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(process.getInputStream()));
            if (process.waitFor() == 0) {
                while ((line = input.readLine()) != null) {
                    if (line.contains(processName)) {
                        process.destroy();
                        input.close();
                        return true;
                    }
                }
            }
            process.destroy();
            input.close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
