package kid1999.upload.controller;

import cn.hutool.core.util.RandomUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import kid1999.upload.dto.Result;
import kid1999.upload.dto.SystemConfig;
import kid1999.upload.model.User;
import kid1999.upload.service.userService;
import kid1999.upload.utils.MailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
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

/**
 * @author kid1999
 * @title: UserController
 * @date 2019/12/22 16:40
 */

@Controller
@Slf4j
public class UserController {

	@Autowired
	private userService userService;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	DefaultKaptcha defaultKaptcha;

	@Autowired
	private MailUtil mailUtil;

	@Autowired
	private SystemConfig systemConfig;

	@Value("${user.capacity}")
	private double freeCapacity;

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

	// 核对信息
	@GetMapping("/checktable")
	String checkTable(){
		return "user/checktable";
	}


	// 修改密码
	@GetMapping("/changepasswd")
	String changePasswd(){
		return "user/changepasswd";
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
						redisTemplate.opsForValue().set(uuid,user,systemConfig.getSession_maxAge(), TimeUnit.SECONDS);
						cookie = new Cookie(URLEncoder.encode(name), URLEncoder.encode(uuid));
						cookie.setMaxAge(systemConfig.getCookie_maxAge());
					}else{
						redisTemplate.opsForValue().set(uuid,user,systemConfig.getSession_minAge(), TimeUnit.SECONDS);
						cookie = new Cookie(URLEncoder.encode(name), URLEncoder.encode(uuid));
						cookie.setMaxAge(systemConfig.getCookie_minAge());
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

	// 注册验证
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
			if(name.length() < 2 || name.length() > 20){
				return Result.fail(400,"用户名不规范！");
			}
			if(!password1.equals(password2)){
				return Result.fail(400,"前后密码不一致!");
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
			user.setRole(1);
			user.setCapacity(freeCapacity);
			userService.addUser(user);
			return Result.success("注册成功！");
		}else{
			return Result.fail(400,"验证码错误，或已失效!");
		}
	}

	// 填写表单 （核对账号，邮箱）
	@PostMapping("/checktable")
	@ResponseBody
	Result checkTable(HttpServletRequest request,
	                  @RequestParam(value = "name") String name,
	                  @RequestParam(value = "email") String email,
	                  @RequestParam(value = "code") String code){
		log.info("核对账户，邮箱");
		if(checkVerificationCode(code,request)){
			if(name.equals("") || email.equals("")){
				return Result.fail(400,"用户名或邮箱不能为空");
			}else{
				User user = userService.findUserByName(name);
				if( user == null){
					return Result.fail(400,"用户名不存在");
				}
				if(!user.getEmail().equals(email)){
					return Result.fail(400,"用户，邮箱不匹配");
				}
				String mailCode = RandomUtil.randomString(6);
				mailUtil.sendMailCode(email,"你的验证码",mailCode);
				System.out.println(mailCode);
				request.getSession().setAttribute(name,mailCode);
				return Result.success("请填写邮箱验证码");
			}
		}else{
			return Result.fail(400,"验证码不对");
		}
	}


	// 核对验证码 （核对，验证码）
	@PostMapping("/changepasswd")
	@ResponseBody
	Result changePass(HttpServletRequest request,
	                  @RequestParam(value = "name") String name,
	                  @RequestParam(value = "mailCode") String mailCode,
	                  @RequestParam(value = "password1") String password1,
	                  @RequestParam(value = "password2") String password2){
		log.info("核对邮箱验证码，修改密码");
		if(name.equals("") || mailCode.equals("")){
			return Result.fail(400,"用户名或验证码不能为空");
		}else{
			User user = userService.findUserByName(name);
			if( user == null){
				return Result.fail(400,"用户名不存在");
			}
			if(!password1.equals(password2)){
				return Result.fail(400,"前后密码不一致");
			}
			if(request.getSession().getAttribute(name).equals(mailCode)){
				User newUser = new User();
				newUser.setName(name);
				newUser.setPassword(DigestUtils.md5DigestAsHex(password1.getBytes()));
				userService.updateUser(newUser);
				return Result.success("修改成功！");
			}else{
				return Result.fail(400,"验证码错误！");
			}
		}
	}



	// 退出登录
	@GetMapping("/logout")
	String logout(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
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


	// 验证码验证
	public Boolean checkVerificationCode(String vrifyCode, HttpServletRequest request) {
		log.info("验证码验证");
		String verificationCodeIn = (String) request.getSession().getAttribute("vrifyCode");
		request.getSession().removeAttribute("vrifyCode");
		if (StringUtils.isEmpty(verificationCodeIn) || !verificationCodeIn.equals(vrifyCode)) {
			return false;
		}
		return true;
	}

}
