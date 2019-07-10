package kid1999.upload.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 拦截器规则
@Component
public class MyUserInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(request.getSession().getAttribute("user") != null){
			return true;
		}else{
			request.setAttribute("info","用户未登录/访问权限不够！！");
			request.getRequestDispatcher("/").forward(request,response);
			return false  ;
		}
	}
}
