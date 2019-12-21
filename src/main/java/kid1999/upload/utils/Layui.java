package kid1999.upload.utils;

import java.util.HashMap;
import java.util.List;

/**
 * @desc:  LayUI 的工具类
 * @auther: kid1999
 * @date: 2019/12/21 22:05
 **/
public class Layui extends HashMap<String, Object> {
    public static Layui data(Integer count, List<?> data){
        Layui r = new Layui();
        r.put("code", 0);
        r.put("msg", "");
        r.put("count", count);
        r.put("data", data);
        return r;
    }
}

