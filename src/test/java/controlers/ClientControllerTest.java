package controlers;

import config.TestConfiguration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import volkov.alexandr.captcha.controlers.CaptchaController;
import volkov.alexandr.captcha.controlers.ClientController;
import volkov.alexandr.captcha.controlers.Protocol;
import volkov.alexandr.captcha.services.register.KeyGenerator;
import volkov.alexandr.captcha.store.Store;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@WebMvcTest(CaptchaController.class)
public class ClientControllerTest {

    @Autowired
    private Store store;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRegistration() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/client/register"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(response.getContentAsString());

        String publicKey = (String) obj.get(Protocol.PUBLIC_KEY);
        String secretKey = (String) obj.get(Protocol.SECRET_KEY);

        assertEquals(KeyGenerator.getPublicKey(secretKey).toString(), publicKey);
        assertTrue(store.contains(UUID.fromString(publicKey)));
    }
}