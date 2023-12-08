package com.goltsov.test_task.test_task.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile(SpringTestConfig.TEST_PROFILE)
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.goltsov.test_task.test_task")
@PropertySource(value = "classpath:/config/application.yml")
public class SpringTestConfig {

    public static final String TEST_PROFILE = "test";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }
}
