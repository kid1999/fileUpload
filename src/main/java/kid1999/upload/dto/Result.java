package kid1999.upload.dto;

import lombok.Data;

/**
 * @author kid1999
 * @title: Result
 * @date 2019/12/9 16:26
 */

@Data
public class Result {
	private int code;
	private String info;

	public static Result success(String info){
		Result result = new Result();
		result.setCode(200);
		result.setInfo(info);
		return result;
	}

	public static Result fail(int code,String info){
		Result result = new Result();
		result.setCode(code);
		result.setInfo(info);
		return result;
	}

}
