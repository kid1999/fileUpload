package kid1999.upload.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import kid1999.upload.model.DayLogs;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author kid1999
 * @title: 日流量统计
 * @date 2019/12/24 9:33
 */

@Mapper
public interface dayLogsMapper extends BaseMapper<DayLogs> {

}
