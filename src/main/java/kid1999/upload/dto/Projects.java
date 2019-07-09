package kid1999.upload.dto;

import lombok.Data;

@Data
public class Projects {
	private int id;
	private String title;
	private String createtime;
	private String endtime;
	private Integer count;  // 收集数量
	private String infomation;
	private String type;
	private String filepath;
}
