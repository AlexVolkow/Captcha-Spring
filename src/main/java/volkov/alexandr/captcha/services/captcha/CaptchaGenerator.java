package volkov.alexandr.captcha.services.captcha;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CaptchaGenerator {
    private final static Logger logger = LogManager.getLogger(CaptchaGenerator.class.getName());

    private static final Color[] colors = {Color.red, Color.black, Color.blue};
    public static final String BACKGROUND = "background.jpg";

    private Map<Captcha, RenderedImage> images = new ConcurrentHashMap<>();

    public RenderedImage getImage(Captcha captcha) {
        return images.computeIfAbsent(captcha, this::renderImage);
    }

    private RenderedImage renderImage(Captcha captcha) {
        String value = captcha.getAnswer();

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("not null or empty value expected");
        }

        BufferedImage image;
        try {
            ClassPathResource res = new ClassPathResource(BACKGROUND);
            image = ImageIO.read(res.getInputStream());
        } catch (IOException e) {
            logger.error("An error occurs during reading file " + BACKGROUND);
            return null;
        }

        Graphics g = image.getGraphics();
        g.setFont(g.getFont().deriveFont(30f));
        char[] chars = value.toCharArray();
        int x = 5;
        int y = 50;
        for (char aChar : chars) {
            x = x + 30;
            g.setColor(colors[(int) (Math.random() * colors.length)]);
            g.drawString(String.valueOf(aChar), x, y);
        }
        g.dispose();

        logger.info("Create image for captcha " + captcha.getToken());
        return image;
    }

    public void removeCaptcha(Captcha captcha) {
        images.remove(captcha);
        logger.info("Remove captcha from cache " + captcha.getToken());
    }
}
