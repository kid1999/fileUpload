package kid1999.upload.config;

import kid1999.upload.model.DayLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * @author kid1999
 * @title: DayCountInterceptor
 * @date 2019/12/24 11:29
 */

@Component
public class DayCountInterceptor implements HandlerInterceptor {
	@Autowired
	private kid1999.upload.mapper.dayLogsMapper dayLogsMapper;


	/**
	 * 访客记录
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		DayLogs dayLogs = new DayLogs();
		dayLogs.setUrl(request.getRequestURI());
		dayLogs.setIp(request.getRemoteAddr());
		dayLogs.setDayTime(LocalDateTime.now());
		dayLogsMapper.insert(dayLogs);
		return true;
	}
}
