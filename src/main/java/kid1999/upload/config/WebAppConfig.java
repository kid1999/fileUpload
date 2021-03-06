package kid1999.upload.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;


/**
 * @desc:  拦截器配置
 * @auther: kid1999
 * @date: 2019/12/21 11:59
 **/

@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {

	//自定义的拦截器
	@Resource
	private UserInterceptor UserInterceptor;

	@Resource
	private DayCountInterceptor dayCountInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//注册自定义拦截器，添加拦截路径和排除拦截路径
		registry.addInterceptor(dayCountInterceptor).addPathPatterns("/**").excludePathPatterns("/static/**","/api/**");
		registry.addInterceptor(UserInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/",
						"/api/**","/test",
						"/register","/login",
						"/upload","/error",
						"/success","/repeat",
						"/search","/upfile",
						"/checktable","/changepasswd",
						"/static/**");
	}

	//注册自定义拦截器，添加拦截路径和排除拦截路径

	// 静态资源配置
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
	}
}

