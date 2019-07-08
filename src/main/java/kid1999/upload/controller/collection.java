package kid1999.upload.controller;

import kid1999.upload.dto.Students;
import kid1999.upload.model.Student;
import kid1999.upload.model.User;
import kid1999.upload.service.homeworkService;
import kid1999.upload.service.studentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class collection {

  @Autowired
  private homeworkService homeworkService;

  @Autowired
  private studentService studentService;


  @GetMapping("/deatils")
  String getcollection(HttpServletRequest request,
                       @RequestParam(value = "worktitle") String worktitle,
                       @RequestParam(value = "userid") int userid,
                       Model model){

    Object session =  request.getSession().getAttribute("user");
    if(session == null) return "redirect:index";
    User user = (User) session;
    //　匹配成功，这是页面管理员
    if(user.getId() == userid){
      List<Student> students = studentService.getStudentsByTitle(worktitle,userid);
      List<Students> studentDto = new ArrayList<>();
      for (Student student:students) {
        Students stu = new Students();
        stu.setName(student.getName());
        stu.setClassname(student.getClassname());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        stu.setTime(format.format(student.getTime()));
        studentDto.add(stu);
      }
      model.addAttribute("studentDto",studentDto);
      model.addAttribute("count",students.size());
      model.addAttribute("worktitle",worktitle);
      return "admindetails";
    }else{    // 这是访问用户

    }

    return "deatils";
  }

  @PostMapping("/deatils")
  String postcollection(){
    return "";
  }
}
