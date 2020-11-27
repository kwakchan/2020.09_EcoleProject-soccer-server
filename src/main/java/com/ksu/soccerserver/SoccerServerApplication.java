package com.ksu.soccerserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
public class SoccerServerApplication {

    @PostConstruct
    public void start() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        log.info("서버 start : " + new Date());
    }

    public static void main(String[] args) {
        SpringApplication.run(SoccerServerApplication.class, args);
    }

}
