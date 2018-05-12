package config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(value = "volkov.alexandr.captcha")
public class TestConfiguration {
    @Bean
    public Long ttl() {
        return 1000L;
    }

    @Bean
    public boolean isProduction() {
        return false;
    }
}
