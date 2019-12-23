package kid1999.upload.controller;

import kid1999.upload.dto.Result;
import kid1999.upload.model.HomeWork;
import kid1999.upload.model.Student;
import kid1999.upload.service.homeworkService;
import kid1999.upload.service.studentService;
import kid1999.upload.utils.FastDFSClientUtils;
import kid1999.upload.utils.Layui;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @desc:   提供 json 数据接口
 * @auther: kid1999
 * @date: 2019/12/22 0:10
 **/

@RestController
@Slf4j
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private studentService studentService;

    @Autowired
    private FastDFSClientUtils fastDFSClientUtils;

    /**
     * 删除student记录
     */
    @DeleteMapping("/homework")
    Result deleteHomeWork(@RequestBody Student student){
        log.info("删除student记录");
        fastDFSClientUtils.deleteFile(student.getFileurl());
        studentService.deleteStudentById(student.getId());
        return Result.success("删除成功");
    }

    /**
     * 获取homework列表
     */
    @GetMapping("/homeworks")
    @ResponseBody
    Layui getHomeworks(@RequestParam(value = "workid") String workid,
                        @RequestParam(value = "userid") int userid,
                        @RequestParam(value = "limit",defaultValue = "10") int limit,
                        @RequestParam(value = "page",defaultValue = "1") int page
    ){
        log.info("获取homeworks");
        List<Student> students = studentService.getStudentsByWorkId(workid,page,limit);
        return Layui.data(1,students);
    }


}
