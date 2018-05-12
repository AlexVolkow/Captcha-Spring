package volkov.alexandr.captcha.services.captcha;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import volkov.alexandr.captcha.services.register.KeyGenerator;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerifyService {
    private final static Logger logger = LogManager.getLogger(VerifyService.class.getName());

    private CaptchaService captchaService;
    private static final int TOKEN_LENGTH = 16;
    private Map<UUID, String> verifyTokens = new ConcurrentHashMap<>();

    @Autowired
    public VerifyService(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    public String verifyCaptcha(UUID user, String answer, String token) {
        Captcha captcha = captchaService.getCaptcha(user, token);
        if (captcha == null) {
            return null;
        }
        String res = null;
        if (captcha.getAnswer().equals(answer)) {
            String verifyToken = TokenUtils.randomToken(TOKEN_LENGTH);
            verifyTokens.put(user, verifyToken);
            res = verifyToken;
            logger.info(String.format("User %s successfully entered captcha %s", user, token));
        }
        logger.info(String.format("Verify captcha %s for user %s", token, user));
        captchaService.removeCaptcha(user, token);
        return res;
    }

    public boolean verifyToken(String secretKey, String token) {
        UUID publicKey = KeyGenerator.getPublicKey(secretKey);
        if (!verifyTokens.containsKey(publicKey)) {
            logger.error("For " + publicKey + " no verify token");
            throw new IllegalArgumentException("For this key no verify token");
        }
        boolean status = verifyTokens.get(publicKey).equals(token);
        logger.info(String.format("Verify token %s with status %s", token, status));
        return status;
    }
}
