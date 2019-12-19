package kid1999.upload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @desc:
 * @auther: kid1999
 * @date: 2019/12/19 19:23
 **/
@Component
public class fastDfsConfig {
    @Value("${fdfs.resHost}")
    private String resHost;

    @Value("${fdfs.storagePort}")
    private String storagePort;

    public String getResHost() {
        return resHost;
    }

    public void setResHost(String resHost) {
        this.resHost = resHost;
    }

    public String getStoragePort() {
        return storagePort;
    }

    public void setStoragePort(String storagePort) {
        this.storagePort = storagePort;
    }
}
