package crawl.demo.utils;

import java.util.UUID;

public class Utils {

    public static String getRandomString() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

}
