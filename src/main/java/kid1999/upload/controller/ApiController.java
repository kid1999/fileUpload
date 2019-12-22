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
     * @return
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
     * @param worktitle
     * @param userid
     * @param pageSize
     * @param page
     * @return
     */
    @GetMapping("/homeworks")
    @ResponseBody
    Layui getcollection(@RequestParam(value = "worktitle") String worktitle,
                        @RequestParam(value = "userid") int userid,
                        @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                        @RequestParam(value = "page",defaultValue = "1") int page
    ){
        log.info("项目详情页");
        List<Student> students = studentService.getStudentsByTitle(worktitle,userid);
        return Layui.data(1,students);
    }


}
