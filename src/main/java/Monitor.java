import java.io.*;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Monitor {
    private static Logger LOG = LoggerFactory.getLogger(Monitor.class.getName());
    public static void main(String[] args) {
        LOG.info("Активация мониторинга");
        // Считываем конфигурационный файл
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File("./config/config.ini")));
        } catch (IOException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }

        MonitorThread thread = new MonitorThread(props);
        LOG.info("Мониторинг запущен");
        thread.start();
    }
}
