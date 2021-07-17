package my.spring.sample.mvc.utils;

import com.google.common.base.Strings;
import org.bson.types.ObjectId;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class StringUtils {

    public static String escape(String str) {
        return str.replaceAll("([-/\\\\^$*+?.()|{}\\[\\]])", "\\\\$1");
    }

    public static String genObjectId() {
        return ObjectId.get().toString();
    }

    public static String genRandom6Digit() {
        String chars = "0123456789";
        Random rd = new Random();
        String str = "";
        for(int i = 0; i<6; i++) {
            char letter = chars.charAt(rd.nextInt(chars.length()));
            str += letter;
        }
        return str;
    }

    public static String extractSubdomain(String url) {
        if(Strings.isNullOrEmpty(url) ||
                (url.startsWith("http://") == false && url.startsWith("https://") == false)) {
            return null;
        }
        String domain = url.split("://")[1];
        String[] arr = domain.split("\\.");
        if(arr.length > 1)
            return arr[0];
        else
            return null;
    }

    private static final String PASSWORD_SOURCE =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()_+=-/?><";

    public static int randomNumberInRange(int x, int y) {
        if(x > y)
            throw new RuntimeException("'x' must be smaller than 'y'.");
        Random random = new Random();
        int min = x;
        int diff = y - x;
        return random.nextInt(diff) + min;
    }

    public static String genRandomPassword() {
        int len = randomNumberInRange(20, 40);
        System.out.println("len: " + len);
        Random rd = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<len; i++) {
            char letter = PASSWORD_SOURCE.charAt(rd.nextInt(PASSWORD_SOURCE.length()));
            sb.append(letter);
        }
        return sb.toString();
    }

    private static final String Alphanumeric =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String genRandomAlphanumericStr(int len) {
        Random rd = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<len; i++) {
            char letter = Alphanumeric.charAt(rd.nextInt(Alphanumeric.length()));
            sb.append(letter);
        }
        return sb.toString();
    }

    private static NumberFormat krwCurrFormatter = null;
    private static NumberFormat usdCurrFormatter = null;

    public static String toCurrencyStr(long cost, String currency) {
        String strCost = String.valueOf(cost);
        String res = null;
        switch(currency) {
            case "KRW":
                if(krwCurrFormatter == null) {
                    Locale krLocale = new Locale("ko", "KR");
                    krwCurrFormatter = NumberFormat.getCurrencyInstance(krLocale);
                }
                res = krwCurrFormatter.format(cost);
                break;
            case "USD":
                double d = cost / 100;
                if(usdCurrFormatter != null) {
                    Locale usLocale = new Locale("en", "US");
                    usdCurrFormatter = NumberFormat.getCurrencyInstance(usLocale);
                }
                res = usdCurrFormatter.format(d);
                break;
        }
        return res;
    }
}
