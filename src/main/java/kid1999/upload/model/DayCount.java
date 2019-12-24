package kid1999.upload.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * @author kid1999
 * @title: DayCount
 * @date 2019/12/24 13:27
 */

@Data
@TableName("dayCount")
public class DayCount {
	@TableId(type = IdType.AUTO)    // 自增
	private int id;
	@TableId(type = IdType.NONE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate day;
	private int count;
}
