package kid1999.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @desc:
 * @auther: kid1999
 * @date: 2019/12/19 20:20
 **/
@Data
@AllArgsConstructor
public class ZipModel {
    private String fileName;
    private String filePath;
}
