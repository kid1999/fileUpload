package kid1999.upload.controller;

import kid1999.upload.mapper.userMapper;
import kid1999.upload.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class index {

  @Autowired
  private userMapper userMapper;


  @GetMapping("/")
  public String index(){
    return "index";
  }

  @GetMapping("/login")
  String login(){
    return "login";
  }

  @GetMapping("/register")
  String register(){
    return "register";
  }

  @PostMapping("/login")
  String login(Model model,
               HttpServletRequest request,
               @RequestParam(value = "name") String name,
               @RequestParam(value = "password") String password){
    User user = new User();
    user.setName(name);
    user.setPassword(password);
    if ((user = userMapper.login(user)) != null){
      model.addAttribute("user",user);
      request.getSession().setAttribute("user",user);
      return "userpage";
    }else{
      model.addAttribute("info","账号密码错误，请重新登录！");
      return "login";
    }
  }

  @PostMapping("/register")
  String register(Model model,
                  @RequestParam(value = "name") String name,
                  @RequestParam(value = "name") String password1,
                  @RequestParam(value = "name") String password2
                  ){

    if(!password1.equals(password2)){
      model.addAttribute("info","密码重复！");
      return "register";
    }
    if(userMapper.findUser(name) != null){
      model.addAttribute("info","用户名已存在！");
      return "register";
    }
    User user = new User();
    user.setName(name);
    user.setPassword(password1);
    userMapper.addUser(user);
    model.addAttribute("info","恭喜: " + user + " 注册成功！，请登录");
    return "login";
  }


  @RequestMapping("/registerAjax")
  public @ResponseBody String registerAjax(@RequestBody User user){
    System.out.println(user);
    if(userMapper.findUser(user.getName())!= null){
      System.out.println("no");
      return "{\"info\":\"用户名重复\"}";
    }else{
      System.out.println("yes");
      return "{\"info\":\"用户名可用\"}";
    }
  }

}
