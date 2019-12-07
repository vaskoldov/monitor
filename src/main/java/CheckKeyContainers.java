import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

/**
 * Класс CheckKeyContainers содержит методы для проверки состояния контейнеров с подписями ЭП-ОВ и ЭП-СП (ФНС и ЕГРН)
 */
public class CheckKeyContainers {
    /**
     * Метод обращается к хранилищу, указанному в префиксе псевдонима ключа,
     * и проверяет наличие в нем указанного ключа.
     * Возвращается true, если указанный ключ найден в хранилище.
     * Возвращается false в противном случае, а также в случае каких-либо исключительных ситуаций.
     * @param keyAlias Псевдоним ключа
     * @return true, если контейнер доступун и указанный в параметре ключ в нем содержится. Иначе false.
     */
    public boolean isSignAvailable(String keyAlias) {
        // Из параметра keyAlias получаем имя хранилища и псевдоним ключа
        String[] keyParts = parseKeyAlias(keyAlias);
        try {
            // Подключаемся к хранилищу
            KeyStore ks = KeyStore.getInstance(keyParts[0]);
            ks.load(null);
            // Проверяем наличие заданного ключа
            if (!ks.containsAlias(keyParts[1])) {
                // и если его нет, возвращаем false
                return false;
            }
            // Иначе возвращаем true
            return true;
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            // При любом исключении возвращаем false
            return false;
        }
    }
    public String signValidTill(String keyAlias) {
        String[] keyParts = parseKeyAlias(keyAlias);
        try {
            // Подключаемся к хранилищу
            KeyStore ks = KeyStore.getInstance(keyParts[0]);
            ks.load(null);
            // Читаем указанный сертификат
            X509Certificate cert = (X509Certificate) ks.getCertificate(keyParts[1]);
            // Форматируем дату в нужный нам формат
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(cert.getNotAfter());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            return "unavailable";
        }
    }

    private String[] parseKeyAlias(String keyAlias) {
        // Разбираем параметр на имя хранилища и собственно псевдоним ключа
        String storageName = "";
        String keyName = "";
        int separatorPos = keyAlias.lastIndexOf("\\");
        if (separatorPos != -1) {
            storageName = keyAlias.substring(0, separatorPos);
            keyName = keyAlias.substring(separatorPos+1);
        } else {
            storageName = "HDImageStore";
            keyName = keyAlias;
        }
        String[] result = {storageName, keyName};
        return result;
    }
}
