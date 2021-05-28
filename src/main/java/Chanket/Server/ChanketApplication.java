package Chanket.Server;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@ServletComponentScan({"Chanket.*"})
public class ChanketApplication {

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(ChanketApplication.class, args);
    }

}
