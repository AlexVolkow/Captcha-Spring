package volkov.alexandr.captcha.services.register;

import com.fasterxml.jackson.annotation.JsonProperty;
import volkov.alexandr.captcha.controlers.Protocol;

import java.util.Objects;
import java.util.UUID;

public class User {
    private final UUID secretKey;
    private final UUID publicKey;

    public User(UUID secretKey, UUID publicKey) {
        this.secretKey = secretKey;
        this.publicKey = publicKey;
    }

    @JsonProperty(Protocol.SECRET_KEY)
    public UUID getSecretKey() {
        return secretKey;
    }

    @JsonProperty(Protocol.PUBLIC_KEY)
    public UUID getPublicKey() {
        return publicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(secretKey, user.secretKey) &&
                Objects.equals(publicKey, user.publicKey);
    }

    @Override
    public int hashCode() {

        return Objects.hash(secretKey, publicKey);
    }

    @Override
    public String toString() {
        return "User{" +
                "secretKey=" + secretKey +
                ", publicKey=" + publicKey +
                '}';
    }
}
