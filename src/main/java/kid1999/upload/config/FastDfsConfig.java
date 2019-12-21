package kid1999.upload.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @desc:  fastDFS配置类
 * @auther: kid1999
 * @date: 2019/12/19 19:23
 **/
@Component
@Data
public class FastDfsConfig {
    @Value("${fdfs.resHost}")
    private String resHost;

    @Value("${fdfs.storagePort}")
    private String storagePort;

}
