import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CheckProcess {
    public boolean checkAdapter(String kubernetesIP) {
        return checkKubernetesServer(kubernetesIP);
    }

    public boolean checkConverter() {
        return checkProcess("converter");
    }

    private boolean checkProcess(String processName) {
        try {
            String line;
            Process process = new ProcessBuilder().command("bash", "-c", "ps axu | grep converter").start();
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

    private boolean checkKubernetesServer(String kubernetesIP) {
        try {
            String line;
            String command = "ping -c 1 " + kubernetesIP;
            Process process = new ProcessBuilder().command("bash", "-c", command).start();
            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(process.getInputStream()));
            if (process.waitFor() == 0) {
                while ((line = input.readLine()) != null) {
                    if (line.contains("100% packet loss")) {
                        process.destroy();
                        input.close();
                        return false;
                    }
                }
            }
            process.destroy();
            input.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
