package services.captcha;

import config.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import volkov.alexandr.captcha.services.captcha.Captcha;
import volkov.alexandr.captcha.services.captcha.CaptchaGenerator;
import volkov.alexandr.captcha.services.captcha.CaptchaService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static volkov.alexandr.captcha.services.captcha.CaptchaGenerator.BACKGROUND;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class CaptchaGeneratorTest {

    @Autowired
    CaptchaGenerator generator;

    @Autowired
    CaptchaService captchaService;

    @Test
    public void generateCaptcha() throws IOException {
        Captcha captcha = captchaService.createCaptcha(UUID.randomUUID());

        RenderedImage image = generator.getImage(captcha);

        ClassPathResource res = new ClassPathResource(BACKGROUND);
        BufferedImage back = ImageIO.read(res.getFile());

        assertEquals(image.getHeight(), back.getHeight());
        assertEquals(image.getWidth(), back.getWidth());
    }
}