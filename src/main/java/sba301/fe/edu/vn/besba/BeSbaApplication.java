package sba301.fe.edu.vn.besba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BeSbaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeSbaApplication.class, args);
    }

}
