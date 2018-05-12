package volkov.alexandr.captcha.services.register;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;

@Component
public class KeyGenerator {
    public static UUID getSecretKey() {
        return UUID.randomUUID();
    }

    public static UUID getPublicKey(UUID secretKey) {
        int h = secretKey.hashCode();
        int hash = (h) ^ (h >>> 16);
        return UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).putInt(hash).array());
    }

    public static UUID getPublicKey(String secretKey) {
        return getPublicKey(UUID.fromString(secretKey));
    }
}
