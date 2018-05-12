package volkov.alexandr.captcha.controlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import volkov.alexandr.captcha.services.register.RegisterService;
import volkov.alexandr.captcha.services.register.User;

@RestController
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private RegisterService registerService;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    @ResponseBody
    public User registryUser() {
        return registerService.registryUser();
    }
}
