package kid1999.upload.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @desc:   系统配置类
 * @auther: kid1999
 * @date: 2019/12/26 23:56
 **/

@Data
@Component
@ConfigurationProperties(prefix = "systemconfig")
public class SystemConfig {
    private int cookie_maxAge;
    private int cookie_minAge;
    private int session_maxAge;
    private int session_minAge;
    private int code_maxAge;

}
