package kid1999.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @desc: 打包下载文件的包装类
 * @auther: kid1999
 * @date: 2019/12/19 20:20
 **/
@Data
@AllArgsConstructor
public class ZipModel {
    private String fileName;
    private String filePath;
}
