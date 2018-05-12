package services.captcha;

import config.TestConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import volkov.alexandr.captcha.CaptchaApplication;
import volkov.alexandr.captcha.services.captcha.Captcha;
import volkov.alexandr.captcha.services.captcha.CaptchaService;
import volkov.alexandr.captcha.services.register.KeyGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class CaptchaServiceTest {
    @Autowired
    CaptchaService captchaService;

    UUID user;

    @Autowired
    long ttl;

    @Before
    public void prepare() {
        user = KeyGenerator.getPublicKey(KeyGenerator.getSecretKey());
    }

    @Test
    public void newCaptcha() {
        Captcha captcha = captchaService.createCaptcha(user);
        assertTrue(captchaService.isValidToken(user, captcha.getToken()));
    }

    @Test
    public void uniqueToken() {
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < 10_000; i++) {
            Captcha captcha = captchaService.createCaptcha(user);
            tokens.add(captcha.getToken());
        }
        assertEquals(tokens.size(), 10_000);
    }

    @Test
    public void simpleRemove() {
        Captcha captcha = captchaService.createCaptcha(user);

        captchaService.removeCaptcha(user, captcha.getToken());
        assertFalse(captchaService.isValidToken(user, captcha.getToken()));
        assertNull(captchaService.getCaptcha(user, captcha.getToken()));
    }


    @Test
    public void oneCaptchaForUser() {
        Captcha c1 = captchaService.createCaptcha(user);
        Captcha c2 = captchaService.createCaptcha(user);

        assertTrue(captchaService.isValidToken(user, c2.getToken()));
        assertFalse(captchaService.isValidToken(user, c1.getToken()));
    }

    @Test
    public void testTtl() throws InterruptedException {
        Captcha captcha = captchaService.createCaptcha(user);

        Thread.sleep(ttl + 100);

        assertFalse(captchaService.isValidToken(user, captcha.getToken()));
    }

    @Test
    public void getCaptcha() {
        Captcha captcha = captchaService.createCaptcha(user);

        Captcha c = captchaService.getCaptcha(user, captcha.getToken());

        assertEquals(c, captcha);
    }
}