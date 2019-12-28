package kid1999.upload.config;

import kid1999.upload.dto.Result;
import kid1999.upload.mapper.remarkMapper;
import kid1999.upload.model.HomeWork;
import kid1999.upload.model.Remark;
import kid1999.upload.service.homeworkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @author kid1999
 * @title: RemarkController
 * @date 2019/12/27 21:18
 */

@Slf4j
@RestController
public class RemarkController {

	@Autowired
	private remarkMapper remarkMapper;

	@Autowired
	private homeworkService homeworkService;

	@PostMapping("createRemark")
	Result createRemark(@RequestBody Remark remark){
		log.info("新建评论");
		try {
			if(remark == null || remark.getEmail().equals("") || remark.getName().equals("") || remark.getRemark().equals("")){
				return Result.fail(400,"留言信息不能为空！");
			}
			HomeWork homeWork = homeworkService.findHomeWorkById(remark.getWorkId());
			remark.setCreateTime(LocalDateTime.now());
			remark.setUserId(homeWork.getUserId());
			remarkMapper.insert(remark);
			return Result.success("留言已送达！");
		}catch (Exception e){
			return Result.fail(400,"出现异常错误！");
		}
	}


}
