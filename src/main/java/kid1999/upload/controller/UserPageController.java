package kid1999.upload.controller;

import kid1999.upload.dto.Result;
import kid1999.upload.mapper.remarkMapper;
import kid1999.upload.model.HomeWork;
import kid1999.upload.model.User;
import kid1999.upload.service.homeworkService;
import kid1999.upload.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
public class UserPageController {

	@Autowired
	private homeworkService homeworkService;


	@Autowired
	private userService userService;

	@Autowired
	private remarkMapper remarkMapper;

	// user 主页
	@GetMapping("/userpage")
	String userpage(){
		log.info("用户主页");
		return "homeworks/userpage";
	}

	/**
	 * 总空间使用量概述
	 * @param userid
	 * @return
	 */
	@GetMapping("/spaceUseAll")
	@ResponseBody
	Result getSpaceUseAll(@RequestParam("userid") int userid){
		log.info("获取总空间使用量概述");
		User user = userService.findUserById(userid);
		List<HomeWork> homeWorks = homeworkService.findWorksByUserId(userid);
		double used = 0;
		for (HomeWork homeWork:homeWorks){
			double count = homeworkService.CountHomeWorkCapacityById(homeWork.getId());
			used += count;
			homeWork.setCapacity(count);
			homeworkService.updateHomeWork(homeWork);
		}
		Map<String,Double> map = new HashMap<>();
		map.put("total",user.getCapacity()/1024);
		map.put("used",used==0 ? 0 : used/1024 );
		return Result.addData(Result.success("获取剩余空间成功"),map);
	}

	/**
	 * 项目空间使用情况
	 * @param userid
	 * @return
	 */
	@GetMapping("/spaceUseOne")
	@ResponseBody
	Result getSpaceUseOne(@RequestParam("userid") int userid){
		log.info("获取项目空间使用情况");
		User user = userService.findUserById(userid);
		List<HomeWork> homeWorks = homeworkService.findWorksByUserId(userid);
		Map<String,Double> map = new HashMap<>();
		for (HomeWork homeWork:homeWorks){
			map.put(homeWork.getTitle(),homeWork.getCapacity());
		}
		return Result.addData(Result.success("获取内存使用情况成功"),map);
	}


	@GetMapping("/homeWorkCollecion")
	@ResponseBody
	Result getHomeWorkCollecion(@RequestParam("userid") int userid){
		log.info("获取项目收集情况");
		User user = userService.findUserById(userid);
		List<HomeWork> homeWorks = homeworkService.findWorksByUserId(userid);
		Map<String,Integer> map = new HashMap<>();
		for (HomeWork homeWork:homeWorks){
			map.put(homeWork.getTitle(),homeWork.getTotal());
		}
		return Result.addData(Result.success("获取项目收集情况成功"),map);
	}




	// 创建项目
	@GetMapping("/createWork")
	String createWork(){
		return "homeworks/creatework";
	}


	// 评论留言

	@GetMapping("/userremark")
	String userremark(){
		return "system/userremark";
	}

	// 空间使用情况
	@GetMapping("/spaceuse")
	String spaceuse(){
		return "system/spaceuse";
	}



	/**
	 * 创建 留言
	 * @param request
	 * @param homeWork
	 * @return
	 */
	@Transactional
	@PostMapping("/createWork")
	@ResponseBody
	Result createWork(HttpServletRequest request,
	                  @RequestBody HomeWork homeWork){
		log.info("创建项目");
		System.out.println(homeWork);
		if(homeWork.getTitle().length() < 2 || homeWork.getTitle().length() > 50){
			return Result.fail(400,"项目命名不规范！");
		}
		User user = (User) request.getSession().getAttribute("user");
		if(homeworkService.findHKByTitleAndUserID(homeWork.getTitle(),user.getId()) != null) {
			return Result.fail(400,"该项目已被创建！");
		}
		if(homeWork.getEndtime().before(new Timestamp(System.currentTimeMillis()))){
			return Result.fail(400,"截止时间不能早于当前！");
		}
		if(homeWork.getEncryption() == 1){
			String invitaionCode = UUID.randomUUID().toString().substring(0,8);
			homeWork.setInvitationCode(invitaionCode);
		}
		homeWork.setCapacity(0.0);
		homeWork.setCreatetime(new Timestamp(System.currentTimeMillis()));
		homeWork.setUserId(user.getId());
		homeworkService.addHomeWork(homeWork);    // 新建work并返回
		return Result.success("创建成功！");
	}




}
