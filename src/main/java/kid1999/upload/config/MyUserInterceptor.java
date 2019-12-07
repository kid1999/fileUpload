package kid1999.upload.config;

import kid1999.upload.mapper.userMapper;
import kid1999.upload.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 拦截器规则
@Component
public class MyUserInterceptor implements HandlerInterceptor {

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private userMapper userMapper;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Cookie[] cookies = request.getCookies();
		// 通过 cookie 认证
		for (Cookie cookie : cookies) {
			String name = cookie.getName();
			if (redisTemplate.hasKey(name) && redisTemplate.opsForValue().get(name).equals(cookie.getValue())){
				request.getSession().setAttribute("user",userMapper.findUser(name));
				return true;
			}
		}
		// 通过 session 认证
		if(request.getSession().getAttribute("user") != null){
			return true;
		}else{
			request.setAttribute("info","用户未登录/访问权限不够！！");
			request.getRequestDispatcher("/").forward(request,response);
			return false  ;
		}
	}
}
