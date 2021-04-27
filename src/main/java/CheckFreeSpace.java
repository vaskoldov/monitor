import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CheckFreeSpace {
    public int checkFreeSpace(String diskPartition) {
        int fillPercentage = 0;
        try {
            String line;
            Process process = new ProcessBuilder().command("bash", "-c", "df -t xfs").start();
            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(process.getInputStream()));
            if (process.waitFor() == 0) {
                // Первую строку пропускаем, потому что в ней заголовки
                input.readLine();
                while ((line = input.readLine()) != null) {
                    // Находим строку, содержащую нужный раздел
                    if (!line.contains(diskPartition)) {continue;}
                    int percent = line.indexOf("%");
                    if (percent >= 0) {
                        String sub = line.substring(percent - 3, percent).trim();
                        fillPercentage = Integer.parseInt(sub);
                    }
                }
            }
            process.destroy();
            input.close();
            return fillPercentage;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
