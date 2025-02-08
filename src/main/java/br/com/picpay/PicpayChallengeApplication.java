package br.com.picpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class PicpayChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicpayChallengeApplication.class, args);
        System.setProperty("spring.devtools.restart.enabled", "false");
    }

}
