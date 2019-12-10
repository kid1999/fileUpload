package kid1999.upload.controller;

import kid1999.upload.dto.Students;
import kid1999.upload.mapper.homeworkMapper;
import kid1999.upload.model.HomeWork;
import kid1999.upload.model.Student;
import kid1999.upload.model.User;
import kid1999.upload.service.homeworkService;
import kid1999.upload.service.studentService;
import kid1999.upload.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class userPage {

  @Autowired
  private homeworkService homeworkService;

  @Autowired
  private userService userService;

  @Autowired
  private studentService studentService;

  // user 展示页
  @GetMapping("/userpage")
  String userpage(HttpServletRequest request,
                  Model model){
    User user = (User) request.getSession().getAttribute("user");
    model.addAttribute("DoneProject",homeworkService.findDoneProjects(user.getId()));
    model.addAttribute("DoingProject",homeworkService.findDoingProjects(user.getId()));
    model.addAttribute("students",studentService.getRemarksByUserid(user.getId()));
    return "userpage";
  }

  // 创建项目
  @GetMapping("/createWork")
  String createWork(){
    return "creatework";
  }


  @PostMapping("/createWork")
  String createWork(HttpServletRequest request,
                    Model model,
                    @RequestParam(value = "title") String title,
                    @RequestParam(value = "desc") String desc,
                    @RequestParam(value = "type") String type,
                    @RequestParam(value = "endtime") String endtime
                    ){
    User user = (User) request.getSession().getAttribute("user");
    if(homeworkService.findHKByTitleAndUserID(title,user.getId()) != null){
      model.addAttribute("info","该项目已被创建！");
      model.addAttribute("referer",request.getRequestURI());
      return "error";
    }

    //  转换时间
    Date now = new Date();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    Date date = new Date();
    try {
      date = format.parse(endtime);
      if(date.before(now)){
        model.addAttribute("info","时间设置错误！");
        return "error";
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }

    HomeWork homeWork = new HomeWork();
    homeWork.setTitle(title);
    homeWork.setInfomation(desc);
    homeWork.setType(type);
    homeWork.setAddr(userService.makePath(user.getId(),title));  // 设置文件路径
    homeWork.setCreatetime(now.getTime());
    homeWork.setEndtime(date.getTime());
    homeWork = homeworkService.addHomeWork(homeWork);    // 新建work并返回
    homeworkService.add(homeWork.getId(),user.getId());   // 新建user-work
    return "redirect:userpage";
  }


  @GetMapping("/showWork")
  String showWork(){
    return "";
  }

  @PostMapping("/search")
  String search(@RequestParam("filename") String filename,
                Model model){
    List<Student> students = studentService.findStuByfilename(filename);
    List<Students> studentDto = new ArrayList<>();
    for (Student student:students) {
      Students stu = new Students();
      stu.setName(student.getName());
      stu.setClassname(student.getClassname());
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      stu.setTime(format.format(student.getUptime()));
      stu.setFilename(student.getFilename());
      studentDto.add(stu);
    }
    model.addAttribute("students",studentDto);
    model.addAttribute("count",students.size());
    return "searchresult";
  }
}
