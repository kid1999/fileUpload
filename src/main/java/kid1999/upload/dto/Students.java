package kid1999.upload.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Students {
	private int id;
	private String name;
	private String classname;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date time;
	private String filename;
	private String remarks;
	private String fileurl;
}
