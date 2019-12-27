package kid1999.upload.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import kid1999.upload.dto.Result;
import kid1999.upload.mapper.dayCountMapper;
import kid1999.upload.model.DayCount;
import kid1999.upload.model.Student;
import kid1999.upload.service.studentService;
import kid1999.upload.utils.FastDFSClientUtils;
import kid1999.upload.utils.Layui;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @desc:   提供 json 数据接口 (无权限)
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

    @Autowired
    private dayCountMapper dayCountMapper;

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



    /**
     * 返回每日点击量
     * @return
     */
    @GetMapping("/dayCount")
    @ResponseBody
    public List<DayCount> dayCount(){
        log.info("返回用户剩余空间");
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.orderBy(true,true,"day").gt("day", LocalDate.now().minusDays(30));
        return dayCountMapper.selectList(wrapper);
    }


}
