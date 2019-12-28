package kid1999.upload.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import kid1999.upload.dto.Project;
import kid1999.upload.dto.Result;
import kid1999.upload.mapper.remarkMapper;
import kid1999.upload.model.HomeWork;
import kid1999.upload.model.Remark;
import kid1999.upload.model.User;
import kid1999.upload.service.homeworkService;
import kid1999.upload.service.userService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
public class userPage {

	@Autowired
	private homeworkService homeworkService;


	@Autowired
	private userService userService;

	@Autowired
	private remarkMapper remarkMapper;

	// user 主页
	@GetMapping("/userpage")
	String userpage(HttpServletRequest request,
	                Model model){
		log.info("用户主页");
		try{
			User user = (User) request.getSession().getAttribute("user");
			List<List<Project>> projects = homeworkService.findProjects(user.getId());
			model.addAttribute("DoingProject",projects.get(0));
			model.addAttribute("DoneProject",projects.get(1));
			model.addAttribute("students",null);
			model.addAttribute("remarks",getRemarks(user.getId()));
			return "homeworks/userpage";
		}catch (Exception e){
			return "error/403";
		}
	}

	@GetMapping("/spaceUsage")
	@ResponseBody
	Result getSpaceUsage(@RequestParam("userid") int userid){
		User user = userService.findUserById(userid);
		List<HomeWork> homeWorks = homeworkService.findWorksByUserId(userid);
		double used = 0;
		for (HomeWork homeWork:homeWorks){
			double count = homeworkService.CountHomeWorkCapacityById(homeWork.getId());
			System.out.println(count + " " + homeWork.getId());
			used += count;
			homeWork.setCapacity(count);
			homeworkService.updateHomeWork(homeWork);
		}
		Map<String,Double> map = new HashMap<>();
		map.put("total",user.getCapacity()/1024);
		map.put("used",used==0 ? 0 : used/1024 );
		return Result.addData(Result.success("获取剩余空间成功"),map);
	}


	// 创建项目
	@GetMapping("/createWork")
	String createWork(){
		return "homeworks/creatework";
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

	/**
	 * 获取 remarks
	 * @param userId
	 * @return
	 */
	List<Remark> getRemarks(int userId){
		log.info("获取remarks");
		try {
			QueryWrapper<Remark> wrapper = new QueryWrapper<>();
			wrapper.eq("user_id",userId).orderByDesc("create_time").last("limit 5");
			return remarkMapper.selectList(wrapper);
		}catch (Exception e){
			log.error(e.getMessage());
			return null;
		}
	}


}
