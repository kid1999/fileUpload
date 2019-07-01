package kid1999.upload.controller;

import kid1999.upload.mapper.homeworkMapper;
import kid1999.upload.mapper.userMapper;
import kid1999.upload.mapper.userworkMapper;
import kid1999.upload.model.HomeWork;
import kid1999.upload.model.User;
import kid1999.upload.service.homeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class userPage {

  @Autowired
  private userMapper userMapper;

  @Autowired
  private homeworkMapper homeworkMapper;

  @Autowired
  private userworkMapper userworkMapper;

  @Autowired
  private homeworkService homeworkService;

  @Value("filePath")
  private String filePath;

  @GetMapping("/userpage")
  String userpage(){
    return "userpage";
  }

  @GetMapping("/createWork")
  String createWork(){
    return "/createWork";
  }

  @PostMapping("/createWork")
  String createWork(HttpServletRequest request,
                    Model model,
                    @RequestParam(value = "title") String title,
                    @RequestParam(value = "desc") String desc,
                    @RequestParam(value = "type") String type,
                    @RequestParam(value = "endtime") String endtime
                    ){
    if(homeworkMapper.findHK(title) != null){
      model.addAttribute("info","该项目被已创建！");
      return "error";
    }

    // 获取 session 中的user
    User user = (User)request.getSession().getAttribute("user");
    System.out.println(filePath);
    HomeWork homeWork = new HomeWork();
    homeWork.setTitle(title);
    homeWork.setInfomation(desc);
    homeWork.setType(type);
    homeWork.setAddr(filePath);  // 设置文件路径
    homeWork.setCreatetime(new Date().getTime());
    //  转换时间
    try {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
      Date date = format.parse(endtime);
      homeWork.setEndtime(date.getTime());
      homeWork = homeworkService.addAndfind(homeWork);    // 新建work并返回
      userworkMapper.add(homeWork.getId(),user.getId());   // 新建user-work
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return "collection";
  }


  @GetMapping("/showWork")
  String showWork(){
    return "";
  }
}
