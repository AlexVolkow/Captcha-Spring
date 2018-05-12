package volkov.alexandr.captcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class CaptchaApplication {
    @Bean
    public Long ttl() {
        return (long) (Integer.parseInt(System.getProperty("ttl")) * 1000);
    }

    @Bean
    public boolean isProduction() {
        return System.getProperty("production") != null;
    }

    public static void main(String[] args) {
        SpringApplication.run(CaptchaApplication.class);
    }
}
