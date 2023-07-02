import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Класс ChtckSMEVService предназначен для проверки работоспособности транспортного сервиса СМЭВ,
 * URL которого задан в файле конфигурации
 */
public class CheckSMEVService {
    public boolean checkTransport(String url) {
        return checkTransportPing(url);
/*        Нижеследующий код проверяет web-сервис СМЭВ. Этот механизм перестал работать.
        try {
            String line;
            java.lang.Process process = new ProcessBuilder().command("bash", "-c", String.format("curl %s --connect-timeout 5", url)).start();
            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(process.getInputStream()));
            if (process.waitFor() == 0) {
                while ((line = input.readLine()) != null) {
                    if (line.contains("HTTP GET not supported")) {
                        // Если сервис по указанному URL работает, то возвращает такой ответ
                        process.destroy();
                        input.close();
                        return true;
                    }
                }
            }
            process.destroy();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkTransportPing(url);

 */
    }
    public boolean checkTransportPing(String url) {
        // Проверка через пинг сервера СМЭВ, если не работает вызов WS СМЭВ
        try {
            String line;
            // Выкусываем ip-адрес из url
            String ip;
            if (url.contains("https")) {
                ip = url.substring(8); // Отрезаем "https://
            } else {
                ip = url.substring(7); // Отрезаем "http://
            }
            ip = ip.substring(0, ip.indexOf(":")); // Отбрасываем правую часть, начиная с порта
            java.lang.Process process = new ProcessBuilder().command("bash", "-c", String.format("ping %s -c 1 -W 2", ip)).start();
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (process.waitFor() == 0) {
                while ((line = input.readLine()) != null) {
                    if (line.contains("bytes from")) {
                        // Если адрес транспорта СМЭВ доступен, то возвращается строка "ХХ bytes from ХХХ.ХХ.ХХХ.ХХХ: + статистика
                        process.destroy();
                        input.close();
                        return true;
                    }
                }
            }
            // Иначе возвращаем false
            process.destroy();
            input.close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
