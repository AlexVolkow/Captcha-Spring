package services.register;

import config.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import volkov.alexandr.captcha.CaptchaApplication;
import volkov.alexandr.captcha.services.register.RegisterService;
import volkov.alexandr.captcha.services.register.User;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class RegisterServiceTest {
    @Autowired
    RegisterService registerService;

    @Test
    public void testNormalWay() {
        User user = registerService.registryUser();

        assertTrue(registerService.isRegistered(user.getPublicKey()));
    }

    @Test
    public void noUser() {
        assertFalse(registerService.isRegistered(UUID.randomUUID()));
    }

    @Test
    public void stressTest() {
        Set<UUID> ids = new HashSet<>();
        for (int i = 0; i < 10_000; i++) {
            ids.add(registerService.registryUser().getPublicKey());
        }

        for (UUID user : ids) {
            assertTrue(registerService.isRegistered(user));
        }
    }
}