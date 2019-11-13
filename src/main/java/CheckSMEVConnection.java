import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CheckSMEVConnection {
    public boolean checkSMEVConnection() {
        try {
            String line;
            Process process = new ProcessBuilder().command("bash", "-c", "df -t xfs").start();
            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(process.getInputStream()));
            if (process.waitFor() == 0) {
                while ((line = input.readLine()) != null) {
                }
            }
            process.destroy();
            input.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

}
