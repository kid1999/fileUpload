package kid1999.upload.controller;

import kid1999.upload.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Controller
@Slf4j
public class IndexController {

	@Autowired
	private RedisTemplate redisTemplate;

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

	// 异常 404
	@GetMapping("/error")
	String error(Model model){
		model.addAttribute("info","该项目已被创建！");
		model.addAttribute("referer","/");
		return "system/error";
	}



}
