package volkov.alexandr.captcha;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import volkov.alexandr.captcha.services.captcha.CaptchaService;
import volkov.alexandr.captcha.services.register.RegisterService;

import java.util.UUID;

import static volkov.alexandr.captcha.controlers.ResponseHelper.*;

@Aspect
@Component
public class SecurityAspect {
    @Autowired
    private RegisterService registerService;

    @Autowired
    private CaptchaService captchaService;

    @Pointcut("execution(@volkov.alexandr.captcha.services.register.RegisteredUser * *(..)) && args(publicKey, ..))")
    private void methodWithUser(String publicKey) {
    }

    @Pointcut("execution(@volkov.alexandr.captcha.services.register.RegisteredUser * *(..)) && args(publicKey, token, ..))")
    private void methodWithToken(String publicKey, String token) {
    }

    @Around("methodWithUser(publicKey)")
    private Object isRegisteredUser(ProceedingJoinPoint pjp, String publicKey) throws Throwable {
        if (publicKey == null) {
            return publicKeyNull();
        }

        UUID pKey = UUID.fromString(publicKey);
        if (!registerService.isRegistered(pKey)) {
            return notRegisteredUser();
        }

        return pjp.proceed();
    }

    @Around("methodWithToken(publicKey, token)")
    private Object isValidToken(ProceedingJoinPoint pjp, String publicKey, String token) throws Throwable {
        if (!captchaService.isValidToken(UUID.fromString(publicKey), token)) {
            return invalidToken();
        }
        return pjp.proceed();
    }
}
