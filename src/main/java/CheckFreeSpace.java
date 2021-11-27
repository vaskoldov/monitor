import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckFreeSpace {
    private static Logger LOG = LoggerFactory.getLogger(CheckFreeSpace.class.getName());
    public int checkFreeSpace(String diskPartition) {
        LOG.info("Проверка свободного места на диске");
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
            LOG.info(String.format("Свободное место на диске - %d", fillPercentage));
            return fillPercentage;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return 0;
        }
    }

}
