package kid1999.upload.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import kid1999.upload.mapper.dayCountMapper;
import kid1999.upload.mapper.dayLogsMapper;
import kid1999.upload.model.DayCount;
import kid1999.upload.model.DayLogs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author kid1999
 * @title: ScheduledTask
 * @date 2019/12/24 15:00
 */

@Component
@Slf4j
public class ScheduledTask {
	@Autowired
	private dayLogsMapper dayLogsMapper;

	@Autowired
	private dayCountMapper dayCountMapper;

	/**
	 * 定时汇总日活跃量
	 * 设置定时任务： 秒：分：时：每天
	 */
	@Scheduled(cron = "0 58 16 ? * * ")
	public void mergeDayLogsToDayCount() {
		try{
			// 获取当日点击数
			LocalDate localDate = LocalDate.now();
			QueryWrapper<DayLogs> wrapper =new QueryWrapper<>();
			wrapper.apply("date_format(day_time,'%Y-%m-%d') = '"+localDate+"'");
			int count = dayLogsMapper.selectCount(wrapper);

			// 写入统计表
			DayCount dayCount = new DayCount();
			dayCount.setDay(LocalDate.now());
			dayCount.setCount(count);

			// 有更新 无新加
			QueryWrapper DayCountWrapper = new QueryWrapper();
			DayCountWrapper.eq("day",LocalDate.now());
			if(dayCountMapper.selectOne(DayCountWrapper) != null){
				dayCountMapper.update(dayCount,DayCountWrapper);
			}else{
				dayCountMapper.insert(dayCount);
			}

			log.info("日活跃量更新：" + dayCount + " " + localDate);
		}catch (Exception e){
			log.error("日活跃量更新失败: " + e.getMessage() + e.getLocalizedMessage());
		}
	}

}
