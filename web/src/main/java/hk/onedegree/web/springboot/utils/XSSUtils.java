package hk.onedegree.web.springboot.utils;


import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.owasp.esapi.ESAPI;

public class XSSUtils {

    private XSSUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String stripXSS(String value) {
        if (value == null) {
            return value;
        }
        value = ESAPI.encoder()
                .canonicalize(value)
                .replaceAll("\0", "");
        return Jsoup.clean(value, Whitelist.none());
    }
}
