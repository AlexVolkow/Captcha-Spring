package volkov.alexandr.captcha.controlers;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import volkov.alexandr.captcha.services.captcha.*;
import volkov.alexandr.captcha.services.register.RegisteredUser;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    private static final Logger logger = LogManager.getLogger(CaptchaController.class);

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private VerifyService verifyService;

    @Autowired
    private CaptchaGenerator generator;

    private boolean isProduction;

    public boolean isProduction() {
        return isProduction;
    }

    @Autowired
    public void setProduction(boolean production) {
        isProduction = production;
    }

    @RegisteredUser
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> newCaptcha(@RequestParam(Protocol.PUBLIC_KEY) String publicKey) {
        UUID user = UUID.fromString(publicKey);
        Captcha captcha = captchaService.createCaptcha(user);

        JSONObject json = new JSONObject();
        json.put(Protocol.TOKEN, captcha.getToken());

        if (!isProduction) {
            json.put(Protocol.ANSWER, captcha.getAnswer());
        }

        return new ResponseEntity<>(json, HttpStatus.CREATED);
    }

    @RegisteredUser
    @VerifyToken
    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public ResponseEntity getImage(@RequestParam(Protocol.PUBLIC_KEY) String publicKey,
                                   @RequestParam(Protocol.TOKEN) String token, HttpServletResponse resp) {
        Captcha captcha = captchaService.getCaptcha(publicKey, token);
        if (captcha == null) {
            return ResponseHelper.error("Unregistered captcha", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        RenderedImage captchaImage = generator.getImage(captcha);
        if (captchaImage == null) {
            return ResponseHelper.error("Error creating captcha", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try (OutputStream out = resp.getOutputStream()) {
            resp.setContentType(MediaType.IMAGE_PNG_VALUE);
            ImageIO.write(captchaImage, "png", out);
        } catch (IOException e) {
            logger.error("An error occurred while write captcha " + captcha);
            return ResponseHelper.error("An error occurred while write captcha", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    @RegisteredUser
    @VerifyToken
    @RequestMapping(value = "/solve", method = RequestMethod.POST)
    public ResponseEntity solveCaptcha(@RequestParam(Protocol.PUBLIC_KEY) String publicKey,
                                       @RequestParam(Protocol.TOKEN) String token,
                                       @RequestParam(Protocol.ANSWER) String answer) {
        UUID key = UUID.fromString(publicKey);
        String verifyToken = verifyService.verifyCaptcha(key, answer, token);
        if (verifyToken == null) {
            return ResponseHelper.error("Wrong answer", HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(Collections.singletonMap(Protocol.RESPONSE, verifyToken),
                    HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> verifyResponse(@RequestParam(Protocol.SECRET_KEY) String secret,
                                                     @RequestParam(Protocol.RESPONSE) String response) {
        boolean status = false;
        String error = "null";
        try {
            status = verifyService.verifyToken(secret, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            error = e.getMessage();
        }

        JSONObject json = new JSONObject();
        json.put(Protocol.STATUS, status);
        json.put(Protocol.ERROR_CODE, error);

        return new ResponseEntity<>(json, HttpStatus.OK);
    }
}
