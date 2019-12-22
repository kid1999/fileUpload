package kid1999.upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync    // 开启异步事务
@SpringBootApplication
public class UploadApplication {

  public static void main(String[] args) {
    SpringApplication.run(UploadApplication.class, args);
  }

}
