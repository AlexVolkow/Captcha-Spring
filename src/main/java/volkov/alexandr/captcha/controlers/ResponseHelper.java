package volkov.alexandr.captcha.controlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

public class ResponseHelper {
    public static ResponseEntity publicKeyNull() {
        return error("Public key is null", HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity notRegisteredUser() {
        return error("Invalid public key", HttpStatus.UNAUTHORIZED);
    }

    public static ResponseEntity invalidToken() {
        return error("Invalid captcha token", HttpStatus.NOT_ACCEPTABLE);
    }

    public static ResponseEntity error(String text, HttpStatus code) {
        return new ResponseEntity<>(Collections.singletonMap(Protocol.ERROR_CODE, text), code);
    }
}
