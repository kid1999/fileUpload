package kid1999.upload.controller;

import kid1999.upload.dto.Result;
import kid1999.upload.model.User;
import kid1999.upload.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
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
		return "user/login";
	}

	// 注册
	@GetMapping("/register")
	String register(){
		return "user/register";
	}

	// 登录验证
	@PostMapping("/login")
	@ResponseBody
	Result login(Model model,
	             HttpServletRequest request,
	             HttpServletResponse response,
	             @RequestParam(value = "name") String name,
	             @RequestParam(value = "password") String password){

		if(userService.findUserByName(name) == null){
			return Result.fail(400,"用户名不存在!");
		}
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
			return Result.success("登录成功！");
		}else{
			model.addAttribute("info","账号密码错误，请重新登录！");
			return Result.fail(400,"账号密码错误，请重新登录！");
		}
	}

	@PostMapping("/register")
	@ResponseBody
	Result register(Model model,
	                @RequestParam(value = "name") String name,
	                @RequestParam(value = "password1") String password1,
	                @RequestParam(value = "password2") String password2
	){

		if(!password1.equals(password2)){
			return Result.fail(400,"密码重复!");
		}
		if(userService.findUserByName(name) != null){
			return Result.fail(400,"用户名已存在!");
		}
		User user = new User();
		user.setName(name);
		String md5PassWord = DigestUtils.md5DigestAsHex(password1.getBytes());
		user.setPassword(md5PassWord);
		userService.addUser(user);
		model.addAttribute("info","恭喜: " + user.getName() + " 注册成功！，请登录");
		return Result.success("注册成功！");
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
		return "system/error";
	}

}
