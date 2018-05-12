package volkov.alexandr.captcha.services.register;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import volkov.alexandr.captcha.store.Store;

import java.util.UUID;

@Service
public class RegisterService {
    private final static Logger logger = LogManager.getLogger(RegisterService.class.getName());

    private Store store;

    @Autowired
    public RegisterService(Store store) {
        this.store = store;
    }

    public User registryUser() {
        UUID secretKey = KeyGenerator.getSecretKey();
        UUID publicKey = KeyGenerator.getPublicKey(secretKey);

        store.addUser(publicKey);
        logger.info("Create new user with public key " + publicKey);

        return new User(secretKey, publicKey);
    }

    public boolean isRegistered(UUID publicKey) {
        return store.contains(publicKey);
    }
}
