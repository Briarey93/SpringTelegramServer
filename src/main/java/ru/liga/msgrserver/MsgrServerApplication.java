package ru.liga.msgrserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@ConfigurationProperties("application.properties")
@EnableFeignClients
public class MsgrServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsgrServerApplication.class, args);
    }
}
