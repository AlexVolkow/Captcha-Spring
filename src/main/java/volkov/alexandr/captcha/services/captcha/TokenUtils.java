package volkov.alexandr.captcha.services.captcha;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TokenUtils {
    private static String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random(23084701432182342L);
    private static AtomicInteger lastToken = new AtomicInteger(0);

    public static String randomToken(int length) {
        final StringBuilder sb = new StringBuilder();
        for (int j = 0; j < length; j++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String nextToken() {
        return String.valueOf(lastToken.incrementAndGet());
    }
}
