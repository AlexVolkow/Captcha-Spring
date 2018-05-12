package services.register;

import config.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import volkov.alexandr.captcha.CaptchaApplication;
import volkov.alexandr.captcha.services.register.KeyGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class KeyGeneratorTest {

    @Test
    public void keyGeneratorTest() {
        UUID s1 = KeyGenerator.getSecretKey();
        UUID s2 = KeyGenerator.getSecretKey();

        assertNotEquals(s1, s2);
    }

    @Test
    public void uniqueKey() {
        Set<UUID> ids = new HashSet<>();
        for (int i = 0; i < 10_000; i++) {
            ids.add(KeyGenerator.getSecretKey());
        }
        assertEquals(ids.size(), 10_000);
    }

    @Test
    public void testPublicKey() {
        UUID s1 = KeyGenerator.getSecretKey();
        UUID s2 = KeyGenerator.getSecretKey();

        assertNotEquals(KeyGenerator.getPublicKey(s1), KeyGenerator.getPublicKey(s2));
    }
}