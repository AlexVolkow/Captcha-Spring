package controlers;

import config.TestConfiguration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import volkov.alexandr.captcha.controlers.CaptchaController;
import volkov.alexandr.captcha.controlers.Protocol;
import volkov.alexandr.captcha.services.captcha.Captcha;
import volkov.alexandr.captcha.services.captcha.CaptchaService;
import volkov.alexandr.captcha.services.captcha.VerifyService;
import volkov.alexandr.captcha.services.register.RegisterService;
import volkov.alexandr.captcha.services.register.User;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@WebMvcTest(CaptchaController.class)
public class CaptchaControllerTest {

    @Autowired
    private CaptchaController captchaController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private VerifyService verifyService;

    private User user;

    private JSONParser parser = new JSONParser();

    @Before
    public void prepare() {
        user = registerService.registryUser();
    }

    @Test
    public void notRegisteredUser() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/captcha/new")
                        .param(Protocol.PUBLIC_KEY, UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse();


        JSONObject obj = (JSONObject) parser.parse(response.getContentAsString());

        String error = (String) obj.get(Protocol.ERROR_CODE);

        assertNotNull(error);
    }

    @Test
    public void registeredUser() throws Exception {
        mockMvc.perform(get("/captcha/new")
                .param(Protocol.PUBLIC_KEY, user.getPublicKey().toString()))
                .andExpect(status().isCreated());
    }

    @Test
    public void validToken() throws Exception {
        Captcha captcha = captchaService.createCaptcha(user.getPublicKey());

        mockMvc.perform(get("/captcha/image")
                .param(Protocol.PUBLIC_KEY, user.getPublicKey().toString())
                .param(Protocol.TOKEN, captcha.getToken()))
                .andExpect(status().isOk());
    }

    @Test
    public void invalidToken() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/captcha/image")
                        .param(Protocol.PUBLIC_KEY, user.getPublicKey().toString())
                        .param(Protocol.TOKEN, "42"))
                .andExpect(status().isNotAcceptable())
                .andReturn().getResponse();

        JSONObject obj = (JSONObject) parser.parse(response.getContentAsString());

        String error = (String) obj.get(Protocol.ERROR_CODE);

        assertNotNull(error);
    }


    @Test
    public void newCaptchaTest() throws Exception {
        captchaController.setProduction(false);

        MockHttpServletResponse response = mockMvc
                .perform(get("/captcha/new")
                        .param(Protocol.PUBLIC_KEY, user.getPublicKey().toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        JSONObject obj = (JSONObject) parser.parse(response.getContentAsString());

        String token = (String) obj.get(Protocol.TOKEN);
        String answer = (String) obj.get(Protocol.ANSWER);

        Captcha captcha = captchaService.getCaptcha(user.getPublicKey(), token);
        assertEquals(captcha.getAnswer(), answer);
    }

    @Test
    public void newCaptchaProduction() throws Exception {
        captchaController.setProduction(true);

        MockHttpServletResponse response = mockMvc
                .perform(get("/captcha/new")
                        .param(Protocol.PUBLIC_KEY, user.getPublicKey().toString()))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        JSONObject obj = (JSONObject) parser.parse(response.getContentAsString());

        String token = (String) obj.get(Protocol.TOKEN);

        assertTrue(captchaService.isValidToken(user.getPublicKey(), token));
    }

    @Test
    public void rightAnswer() throws Exception {
        Captcha captcha = captchaService.createCaptcha(user.getPublicKey());

        MockHttpServletResponse response = mockMvc
                .perform(post("/captcha/solve")
                        .param(Protocol.PUBLIC_KEY, user.getPublicKey().toString())
                        .param(Protocol.TOKEN, captcha.getToken())
                        .param(Protocol.ANSWER, captcha.getAnswer()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        JSONObject obj = (JSONObject) parser.parse(response.getContentAsString());

        String token = (String) obj.get(Protocol.RESPONSE);
        assertTrue(verifyService.verifyToken(user.getSecretKey().toString(), token));
    }

    @Test
    public void wrongAnswer() throws Exception {
        Captcha captcha = captchaService.createCaptcha(user.getPublicKey());

        MockHttpServletResponse response = mockMvc
                .perform(post("/captcha/solve")
                        .param(Protocol.PUBLIC_KEY, user.getPublicKey().toString())
                        .param(Protocol.TOKEN, captcha.getToken())
                        .param(Protocol.ANSWER, "#$"))
                .andExpect(status().isForbidden())
                .andReturn().getResponse();

        JSONObject obj = (JSONObject) parser.parse(response.getContentAsString());
        String error = (String) obj.get(Protocol.ERROR_CODE);

        assertNotNull(error);
    }

    @Test
    public void successVerify() throws Exception {
        Captcha captcha = captchaService.createCaptcha(user.getPublicKey());

        String verifyToken = verifyService.verifyCaptcha(user.getPublicKey(),
                captcha.getAnswer(), captcha.getToken());

        MockHttpServletResponse response = mockMvc
                .perform(get("/captcha/verify")
                        .param(Protocol.SECRET_KEY, user.getSecretKey().toString())
                        .param(Protocol.RESPONSE, verifyToken))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        JSONObject obj = (JSONObject) parser.parse(response.getContentAsString());

        boolean status = (boolean) obj.get(Protocol.STATUS);
        assertTrue(status);
    }

    @Test
    public void failedVerify() throws Exception {
        Captcha captcha = captchaService.createCaptcha(user.getPublicKey());

        String verifyToken = verifyService.verifyCaptcha(user.getPublicKey(),
                captcha.getAnswer(), captcha.getToken());


        MockHttpServletResponse response = mockMvc
                .perform(get("/captcha/verify")
                        .param(Protocol.SECRET_KEY, user.getSecretKey().toString())
                        .param(Protocol.RESPONSE, verifyToken + "$"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        JSONObject obj = (JSONObject) parser.parse(response.getContentAsString());

        String error = (String) obj.get(Protocol.ERROR_CODE);
        boolean status = (boolean) obj.get(Protocol.STATUS);
        assertFalse(status);
        assertNotNull(error);
    }
}
