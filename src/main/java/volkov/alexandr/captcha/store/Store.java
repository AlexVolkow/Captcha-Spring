package volkov.alexandr.captcha.store;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface Store {
    void addUser(UUID key);

    boolean contains(UUID key);

    void removeUser(UUID key);
}
