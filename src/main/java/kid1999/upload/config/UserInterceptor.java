package kid1999.upload.config;

import kid1999.upload.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @desc:  系统拦截器设置
 * @auther: kid1999
 * @date: 2019/12/21 11:59
 **/

@Component
public class UserInterceptor implements HandlerInterceptor {

	@Autowired
	private RedisTemplate redisTemplate;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		// 身份验证
		Cookie[] cookies = request.getCookies();
		// 通过 cookie 认证
		if(cookies != null){
			for (Cookie cookie : cookies) {
				String name = cookie.getName();
				String uuid = cookie.getValue();
				if (redisTemplate.hasKey(uuid)){
					User user = (User) redisTemplate.opsForValue().get(uuid);
					if(user.getName().equals(name)){
						return true;
					}
				}
			}
		}
		// 通过 session 认证
		if(request.getSession().getAttribute("user") != null){
			return true;
		}else{
			request.setAttribute("info","用户未登录/访问权限不够！！");
			request.getRequestDispatcher("/").forward(request,response);
			return false ;
		}
	}


}
