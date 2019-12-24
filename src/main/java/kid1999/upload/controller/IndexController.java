package kid1999.upload.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import kid1999.upload.model.DayCount;
import kid1999.upload.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.List;

@Controller
@Slf4j
public class IndexController {

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private kid1999.upload.mapper.dayCountMapper dayCountMapper;

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


	/**
	 * 返回每日点击量
	 * @return
	 */
	@GetMapping("/dayCount")
	@ResponseBody
	public List<DayCount> dayCount(){

		// create data
//		LocalDate localDate = LocalDate.now();
//		for (int i = 0; i <90 ; i++) {
//			DayCount dayCount = new DayCount();
//			dayCount.setDay(localDate.minusDays(i));
//			dayCount.setCount(RandomUtil.randomInt(0,10000));
//			System.out.println(dayCount);
//			dayCountMapper.insert(dayCount);
//		}

		QueryWrapper wrapper = new QueryWrapper();
		wrapper.orderBy(true,true,"day").gt("day",LocalDate.now().minusDays(30));
		return dayCountMapper.selectList(wrapper);
	}


	// 异常 404
	@GetMapping("/error")
	String error(Model model){
		model.addAttribute("info","该项目已被创建！");
		model.addAttribute("referer","/");
		return "system/error";
	}

	@GetMapping("/test")
	String test(){
		return "test";
	}


}
