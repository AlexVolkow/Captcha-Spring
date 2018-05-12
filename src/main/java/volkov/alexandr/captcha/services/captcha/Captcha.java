package volkov.alexandr.captcha.services.captcha;

import java.util.Date;
import java.util.Objects;

public class Captcha {
    private final String token;
    private final String answer;
    private final long creationTime = new Date().getTime();

    public Captcha(String token, String answer) {
        this.token = token;
        this.answer = answer;
    }

    public String getToken() {
        return token;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isValid(long ttl) {
        long currTime = new Date().getTime();
        return (creationTime + ttl) > currTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Captcha captcha = (Captcha) o;
        return Objects.equals(token, captcha.token) &&
                Objects.equals(answer, captcha.answer);
    }

    @Override
    public int hashCode() {

        return Objects.hash(token, answer);
    }

    @Override
    public String toString() {
        return "Captcha{" +
                "token='" + token + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
