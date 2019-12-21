package kid1999.upload.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import kid1999.upload.dto.Result;
import kid1999.upload.model.User;
import kid1999.upload.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class Index {

	@Autowired
	private userService userService;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	DefaultKaptcha defaultKaptcha;

	@Value("${cookieMaxAge}")
	private int cookieMaxAge;

	@Value("${sessionMaxAge}")
	private Long sessionMaxAge;

	// 首页
	@GetMapping("/")
	public String index(HttpServletRequest request) throws UnsupportedEncodingException {
		log.info("主页");
		Cookie[] cookies = request.getCookies();
		if(cookies != null){
			for (Cookie cookie : cookies) {
				String name = URLDecoder.decode(cookie.getName(), "gbk");
				String uuid = URLDecoder.decode(cookie.getValue(), "gbk");
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
		log.info("尝试登录");
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
	Result login(HttpServletRequest request,
				 HttpServletResponse response,
				 @RequestParam(value = "name") String name,
				 @RequestParam(value = "password") String password,
				 @RequestParam(value = "keepLogin",required = false) String keepLogin,
				 @RequestParam(value = "code") String code){
		log.info("登录验证");
		User user = new User();
		user.setName(name);
		user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
		if (checkVerificationCode(code,request)){
			if(userService.findUserByName(name) != null){
				if ((user = userService.login(user)) != null){
					String uuid = UUID.randomUUID().toString();
					request.getSession().setAttribute("user",user);
					Cookie cookie = null;
					if(keepLogin != null){
						redisTemplate.opsForValue().set(uuid,user,sessionMaxAge*100, TimeUnit.SECONDS);
						cookie = new Cookie(URLEncoder.encode(name), URLEncoder.encode(uuid));
						cookie.setMaxAge(cookieMaxAge*100);
					}else{
						redisTemplate.opsForValue().set(uuid,user,sessionMaxAge, TimeUnit.SECONDS);
						cookie = new Cookie(URLEncoder.encode(name), URLEncoder.encode(uuid));
						cookie.setMaxAge(cookieMaxAge);
					}
					response.addCookie(cookie);
					return Result.success("登录成功!");
				}else{
					return Result.fail(400,"账号密码错误，请重新登录!");
				}}
			else{
				return Result.fail(400,"用户名不存在!");
			}}
		else {
			return Result.fail(400,"验证码错误，或已失效!");
		}
	}

	@PostMapping("/register")
	@ResponseBody
	Result register(HttpServletRequest request,
					@RequestParam(value = "name") String name,
					@RequestParam(value = "password1") String password1,
					@RequestParam(value = "password2") String password2,
					@RequestParam(value = "email") String email,
					@RequestParam(value = "code") String code){
		log.info("注册");
		if(checkVerificationCode(code,request)){
			if(!password1.equals(password2)){
				return Result.fail(400,"密码重复!");
			}
			if(userService.findUserByName(name) != null){
				return Result.fail(400,"用户名已存在!");
			}
			if(userService.findUserByEmail(email) != null){
				return Result.fail(400,"邮箱已注册!");
			}
			User user = new User();
			user.setName(name);
			String md5PassWord = DigestUtils.md5DigestAsHex(password1.getBytes());
			user.setPassword(md5PassWord);
			user.setEmail(email);
			userService.addUser(user);
			return Result.success("注册成功！");
		}else{
			return Result.fail(400,"验证码错误，或已失效!");
		}
	}


	@GetMapping("/logout")
	public String logout(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		log.info("退出登录");
		User user = (User) request.getSession().getAttribute("user");
		String name = user.getName();
		request.getSession().removeAttribute("user");//清空session信息
		request.getSession().invalidate();//清除 session 中的所有信息
		//退出登录的时候清空cookie信息,cookie需要通过HttpServletRequest，HttpServletResponse获取
		Cookie[] cookies = request.getCookies();
		for(Cookie c:cookies){
			String cookieName = URLDecoder.decode(c.getName(), "gbk");
			if(name.equals(cookieName)){
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


	/** 验证码验证
	 */
	public Boolean checkVerificationCode(String vrifyCode, HttpServletRequest request) {
		String verificationCodeIn = (String) request.getSession().getAttribute("vrifyCode");
		request.getSession().removeAttribute("vrifyCode");
		if (StringUtils.isEmpty(verificationCodeIn) || !verificationCodeIn.equals(vrifyCode)) {
			return false;
		}
		return true;
	}

}
