import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Класс предназначен для создания файла состояния системы
 * и записи в него результатов мониторинга.
 */
public class StatusFileWriter {
    public static void writeResults(String fileName, String results) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        fw.write(results);
        fw.close();
    }
}
