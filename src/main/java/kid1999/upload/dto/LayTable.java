package kid1999.upload.dto;

import lombok.Data;

import java.util.List;

/**
 * @desc:  回传LayTable的包装类
 * @auther: kid1999
 * @date: 2019/12/21 21:08
 **/
@Data
public class LayTable {
    private int status;
    private String message;
    private int total;
    private List<Object> data;      // 承载各种Obj
}
