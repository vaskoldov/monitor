import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Класс ChtckSMEVService предназначен для проверки работоспособности транспортного сервиса СМЭВ,
 * URL которого задан в файле конфигурации
 */
public class CheckSMEVService {
    public boolean checkTransport(String url) {
        try {
            String line;
            Process process = new ProcessBuilder().command("bash", "-c", String.format("curl -Is %s --connect-timeout 5", url)).start();
            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(process.getInputStream()));
            if (process.waitFor() == 0) {
                while ((line = input.readLine()) != null) {
                    if (line.contains("HTTP/1.1 405 Method Not Allowed")) {
                        // Если сервис по указанному URL работает, то возвращает такой ответ
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
