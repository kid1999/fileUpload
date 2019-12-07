package kid1999.upload.controller;

import com.sun.deploy.net.HttpResponse;
import kid1999.upload.mapper.userMapper;
import kid1999.upload.model.User;
import kid1999.upload.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class Index {

  @Autowired
  private userService userService;

  @Autowired
  private RedisTemplate redisTemplate;

  @Value("${cookieMaxAge}")
  private int cookieMaxAge;

  // 首页
  @GetMapping("/")
  public String index(HttpServletRequest request){
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      String name = cookie.getName();
      if (redisTemplate.hasKey(name) && redisTemplate.opsForValue().get(name).equals(cookie.getValue())){
        request.getSession().setAttribute("user",userService.findUser(name));
      }
    }
    return "index";
  }

  // 登录
  @GetMapping("/login")
  String login(){
    return "login";
  }

  // 注册
  @GetMapping("/register")
  String register(){
    return "register";
  }

  // 登录验证
  @PostMapping("/login")
  String login(Model model,
               HttpServletRequest request,
               HttpServletResponse response,
               @RequestParam(value = "name") String name,
               @RequestParam(value = "password") String password){
    User user = new User();
    user.setName(name);
    user.setPassword(password);
    if ((user = userService.login(user)) != null){
      model.addAttribute("user",user);
      request.getSession().setAttribute("user",user);
      UUID uuid = UUID.randomUUID();
      redisTemplate.opsForValue().set(name,uuid.toString());
      Cookie cookie = new Cookie(name,uuid.toString());
      cookie.setMaxAge(cookieMaxAge);
      response.addCookie(cookie);
      return "redirect:userpage";
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
    if(userService.findUser(name) != null){
      model.addAttribute("info","用户名已存在！");
      return "register";
    }
    User user = new User();
    user.setName(name);
    user.setPassword(password1);
    userService.addUser(user);
    model.addAttribute("info","恭喜: " + user + " 注册成功！，请登录");
    return "login";
  }


  @RequestMapping("/registerAjax")
  public @ResponseBody String registerAjax(@RequestBody User user){
    System.out.println(user);
    if(userService.findUser(user.getName())!= null){
      System.out.println("no");
      return "{\"info\":\"用户名重复\"}";
    }else{
      System.out.println("yes");
      return "{\"info\":\"用户名可用\"}";
    }
  }

  @GetMapping("/logout")
  public String logout(HttpServletRequest request){
    request.getSession().removeAttribute("user");
    return "redirect:/";
  }

  @GetMapping("/error")
  String error(Model model){
    model.addAttribute("info","该项目已被创建！");
    model.addAttribute("referer","/");
    return "error";
  }

}
