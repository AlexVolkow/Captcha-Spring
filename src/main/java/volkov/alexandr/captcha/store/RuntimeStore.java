package volkov.alexandr.captcha.store;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuntimeStore implements Store {
    private Set<UUID> store = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void addUser(UUID key) {
        store.add(key);
    }

    @Override
    public boolean contains(UUID key) {
        return store.contains(key);
    }

    @Override
    public void removeUser(UUID key) {
        store.remove(key);
    }
}
