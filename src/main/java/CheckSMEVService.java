import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс ChtckSMEVService предназначен для проверки работоспособности транспортного сервиса СМЭВ,
 * URL которого задан в файле конфигурации
 */
public class CheckSMEVService {
    private static Logger LOG = LoggerFactory.getLogger(CheckSMEVService.class.getName());
    public boolean checkTransport(String url) {
        LOG.info("Проверка доступности транспорта СМЭВ");
        try {
            String line;
            Process process = new ProcessBuilder().command("bash", "-c", String.format("curl %s --connect-timeout 5", url)).start();
            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(process.getInputStream()));
            if (process.waitFor() == 0) {
                while ((line = input.readLine()) != null) {
                    if (line.contains("HTTP GET not supported")) {
                        // Если сервис по указанному URL работает, то возвращает такой ответ
                        process.destroy();
                        input.close();
                        LOG.info("Транспорт СМЭВ - Ок");
                        return true;
                    }
                }
            }
            process.destroy();
            input.close();
            LOG.info("Транспорт СМЭВ - Fail");
            return false;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
