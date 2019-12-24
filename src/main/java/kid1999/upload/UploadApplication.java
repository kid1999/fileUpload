package kid1999.upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling  // 开启定时任务  发现注解@Scheduled的任务并后台执行
@EnableAsync    // 开启异步事务
@SpringBootApplication
public class UploadApplication {

  public static void main(String[] args) {
    SpringApplication.run(UploadApplication.class, args);
  }

}
