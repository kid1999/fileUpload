package kid1999.upload.controller;

import kid1999.upload.dto.Projects;
import kid1999.upload.model.HomeWork;
import kid1999.upload.model.Student;
import kid1999.upload.model.User;
import kid1999.upload.service.homeworkService;
import kid1999.upload.service.studentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
public class userPage {

	@Autowired
	private homeworkService homeworkService;

	@Autowired
	private studentService studentService;

	// user 展示页
	@GetMapping("/userpage")
	String userpage(HttpServletRequest request,
	                Model model){
		log.info("用户主页");
		User user = (User) request.getSession().getAttribute("user");
		List<List<Projects>> projects = homeworkService.findProjects(user.getId());
		model.addAttribute("DoingProject",projects.get(0));
		model.addAttribute("DoneProject",projects.get(1));
		model.addAttribute("students",studentService.getRemarksByUserid(user.getId()));
		return "homeworks/userpage";
	}

	// 创建项目
	@GetMapping("/createWork")
	String createWork(){
		return "homeworks/creatework";
	}


	@Transactional
	@PostMapping("/createWork")
	String createWork(HttpServletRequest request,
	                  Model model,
	                  @RequestParam(value = "title") String title,
	                  @RequestParam(value = "desc") String desc,
	                  @RequestParam(value = "type") String type,
	                  @RequestParam(value = "endtime") String endtime
	){
		log.info("创建项目");
		User user = (User) request.getSession().getAttribute("user");
		if(homeworkService.findHKByTitleAndUserID(title,user.getId()) != null){
			model.addAttribute("info","该项目已被创建！");
			model.addAttribute("referer",request.getRequestURI());
			return "system/error";
		}

		//  转换时间
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Date date = null;
		try {
			date = format.parse(endtime);
			if(date.before(now)){
				model.addAttribute("info","时间设置错误！");
				return "system/error";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		HomeWork homeWork = new HomeWork();
		homeWork.setTitle(title);
		homeWork.setInfomation(desc);
		homeWork.setType(type);
		homeWork.setCreatetime(new Timestamp(System.currentTimeMillis()));
		homeWork.setEndtime(new Timestamp(System.currentTimeMillis()));
		homeWork = homeworkService.addHomeWork(homeWork);    // 新建work并返回
		homeworkService.add(homeWork.getId(),user.getId());   // 新建user-work
		return "redirect:/userpage";
	}


	@PostMapping("/search")
	String search(@RequestParam("filename") String filename,
	              Model model){
		log.info("搜索");
		List<Student> students = studentService.findStuByfilename(filename);
		List<Student> studentDto = new ArrayList<>();
		for (Student student:students) {
			studentDto.add(student);
		}
		model.addAttribute("students",studentDto);
		model.addAttribute("count",students.size());
		return "system/searchresult";
	}
}
