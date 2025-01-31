package br.com.picpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PicpayChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicpayChallengeApplication.class, args);
    }

}
