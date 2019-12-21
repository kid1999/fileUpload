package kid1999.upload.dto;

import kid1999.upload.model.HomeWork;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @desc:  回传用户项目列表的包装类
 * @auther: kid1999
 * @date: 2019/12/21 21:08
 **/


@Data
@AllArgsConstructor
public class Projects {
	private HomeWork homeWork;
	private int count;
}
