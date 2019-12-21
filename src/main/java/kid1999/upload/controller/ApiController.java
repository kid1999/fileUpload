package kid1999.upload.controller;

import kid1999.upload.dto.Result;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @desc:   提供 json 数据接口
 * @auther: kid1999
 * @date: 2019/12/22 0:10
 **/
@RestController
public class ApiController {

    @DeleteMapping("/deleteHomeWork")
    Result deleteHomeWork(@RequestParam("id") int studentId){
        System.out.println(studentId);
        return Result.success("删除成功");
    }
}
