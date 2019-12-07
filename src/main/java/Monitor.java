import java.io.*;
import java.util.Properties;

public class Monitor {
    public static void main(String[] args) {
        // Считываем конфигурационный файл
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File("./config/config.ini")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        MonitorThread thread = new MonitorThread(props);
        thread.start();
    }
}
