package sba301.fe.edu.vn.besba;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BeSbaApplication {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure()
                .filename("key.env")
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        SpringApplication.run(BeSbaApplication.class, args);
    }
}