package kid1999.upload.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import kid1999.upload.dto.Project;
import kid1999.upload.dto.Result;
import kid1999.upload.mapper.dayCountMapper;
import kid1999.upload.model.DayCount;
import kid1999.upload.model.HomeWork;
import kid1999.upload.model.Student;
import kid1999.upload.model.User;
import kid1999.upload.service.homeworkService;
import kid1999.upload.service.studentService;
import kid1999.upload.utils.FastDFSClientUtils;
import kid1999.upload.utils.Layui;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

/**
 * @desc:   提供 json 数据接口 (无权限)
 * @auther: kid1999
 * @date: 2019/12/22 0:10
 **/

@RestController
@Slf4j
public class ApiController {

    @Autowired
    private studentService studentService;

    @Autowired
    private FastDFSClientUtils fastDFSClientUtils;

    @Autowired
    private dayCountMapper dayCountMapper;

    @Autowired
    private homeworkService homeworkService;

    /**
     * 删除student记录
     */
    @DeleteMapping("/user/student")
    Result deleteHomeWork(@RequestBody Student student){
        log.info("删除student记录");
        fastDFSClientUtils.deleteFile(student.getFileurl());
        studentService.deleteStudentById(student.getId());
        homeworkService.minusHomeWorkCount(student.getWorkid());        // - 1个人
        return Result.success("删除成功");
    }

    /**
     * 修改student记录
     * @param student
     * @return
     */
    @PutMapping("/user/student")
    Result changeStudent(@RequestBody Student student){
        log.info("修改student记录");
        try {
            System.out.println(student);
            studentService.updateStudentById(student);
            return Result.success("修改成功！");
        }catch (Exception e){
            log.error(e.getMessage());
            return Result.fail(400,"请求出错！");
        }
    }

    /**
     * 获取student列表
     */
    @GetMapping("/user/student")
    @ResponseBody
    Layui getHomeWorks(HttpServletRequest request,
                       @RequestParam(value = "workid") String workid,
                        @RequestParam(value = "userid") int userId,
                        @RequestParam(value = "limit",defaultValue = "10") int limit,
                        @RequestParam(value = "page",defaultValue = "1") int page
    ){
        log.info("获取students");
	    User user = (User) request.getSession().getAttribute("user");
	    if(user.getId() != userId){
	    	return Layui.data(0,null);
	    }
        List<Student> students = studentService.getStudentsByWorkId(workid,page,limit);
        return Layui.data(students.size(),students);
    }




    @GetMapping("/user/doingHomeWork")
    Layui doingHomeWorks(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        List<HomeWork> homeWorks = homeworkService.findWorksByUserId(user.getId());
        System.out.println(homeWorks);
        return Layui.data(homeWorks.size(),homeWorks);
    }

    @GetMapping("/user/doneHomeWork")
    Layui doneHomeWorks(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        List<List<Project>> projects = homeworkService.findProjects(user.getId());
        return Layui.data(projects.get(1).size(),projects.get(1));
    }


    @PutMapping("/user/homework")
    Result updateHomeWork(@RequestBody HomeWork homeWork){
    	homeworkService.updateHomeWork(homeWork);
    	return Result.success("更新成功！");
    }

    @DeleteMapping("/user/homework")
    Result deleteHomeWork(@RequestBody HomeWork homeWork){
	    homeworkService.deleteHomeWorkById(homeWork);
	    return Result.success("删除成功！");
    }




    /**
     * 返回每日点击量
     * @return
     */
    @GetMapping("/api/dayCount")
    @ResponseBody
    public List<DayCount> dayCount(){
        log.info("返回用户剩余空间");
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.orderBy(true,true,"day").gt("day", LocalDate.now().minusDays(30));
        return dayCountMapper.selectList(wrapper);
    }



    /**
     * 搜索提交记录
     */
    @PostMapping("/api/search")
    @ResponseBody
    Result search(@RequestParam("name") String name,
                  @RequestParam("workId") int workId){
        log.info("搜索");
        Student student = studentService.getStudentBySname(workId,name);
        if(student == null){
            return Result.fail(400,"sorry,该同学尚未提交作业，请检查后再试！");
        }else{
            return Result.success("你已经提交过了！");
        }
    }


}
