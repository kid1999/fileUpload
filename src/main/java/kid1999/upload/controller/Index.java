package kid1999.upload.controller;

import kid1999.upload.model.User;
import kid1999.upload.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class Index {

  @Autowired
  private userService userService;

  @Autowired
  private RedisTemplate redisTemplate;

  @Value("${cookieMaxAge}")
  private int cookieMaxAge;

  @Value("${sessionMaxAge}")
  private Long sessionMaxAge;

  // 首页
  @GetMapping("/")
  public String index(HttpServletRequest request){
    Cookie[] cookies = request.getCookies();
    if(cookies != null){
      for (Cookie cookie : cookies) {
        String name = cookie.getName();
        String uuid = cookie.getValue();
        if (redisTemplate.hasKey(uuid)){
          User user = (User) redisTemplate.opsForValue().get(uuid);
          if(user.getName().equals(name)){
            request.getSession().setAttribute("user",user);
         }
        }
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
    String md5PassWord = DigestUtils.md5DigestAsHex(password.getBytes());
    user.setPassword(md5PassWord);
    if ((user = userService.login(user)) != null){
      String uuid = UUID.randomUUID().toString();
      redisTemplate.opsForValue().set(uuid,user,sessionMaxAge, TimeUnit.SECONDS);
      request.getSession().setAttribute("user",user);
      Cookie cookie = new Cookie(name,uuid);
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
                  @RequestParam(value = "password1") String password1,
                  @RequestParam(value = "password2") String password2
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
    String md5PassWord = DigestUtils.md5DigestAsHex(password1.getBytes());
    user.setPassword(md5PassWord);
    userService.addUser(user);
    model.addAttribute("info","恭喜: " + user.getName() + " 注册成功！，请登录");
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
  public String logout(HttpServletRequest request,HttpServletResponse response){
    User user = (User) request.getSession().getAttribute("user");
    String name = user.getName();
    request.getSession().removeAttribute("user");//清空session信息
    request.getSession().invalidate();//清除 session 中的所有信息
    //退出登录的时候清空cookie信息,cookie需要通过HttpServletRequest，HttpServletResponse获取
    Cookie[] cookies = request.getCookies();
    for(Cookie c:cookies){
      if(name.equals(c.getName())){
        c.setMaxAge(0);
        response.addCookie(c);
      }
    }
    return "redirect:/";
  }

  @GetMapping("/error")
  String error(Model model){
    model.addAttribute("info","该项目已被创建！");
    model.addAttribute("referer","/");
    return "error";
  }

}
