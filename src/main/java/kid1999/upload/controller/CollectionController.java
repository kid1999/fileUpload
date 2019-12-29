package kid1999.upload.controller;

import kid1999.upload.dto.Project;
import kid1999.upload.dto.Result;
import kid1999.upload.model.HomeWork;
import kid1999.upload.model.Student;
import kid1999.upload.model.User;
import kid1999.upload.service.homeworkService;
import kid1999.upload.service.studentService;
import kid1999.upload.utils.FastDFSClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@Controller
@Slf4j
public class CollectionController {

	@Autowired
	private homeworkService homeworkService;

	@Autowired
	private studentService studentService;

	@Autowired
	private FastDFSClientUtils fastDFSClientUtils;


	/**
	 * 某个项目详情页面
	 * @param model
	 * @param userId
	 * @param workId
	 * @return
	 */
	@GetMapping("/deatils")
	String Test(HttpServletRequest request,
				Model model,
				@RequestParam(value = "workid") int workId){
		HomeWork homeWork = homeworkService.findUserIdByWorkId(workId);
		User user = (User) request.getSession().getAttribute("user");
		if(homeWork.getUserId() == user.getId()){
			model.addAttribute("workid",workId);
			return "homeworks/homeworkDetails";
		}else{
			return "index";
		}
	}


	/**
	 * 文件上传页面
	 * @param request
	 * @param workid
	 * @param model
	 * @return
	 */
	@GetMapping("/upload")
	String postcollection(HttpServletRequest request,
	                       @RequestParam(value = "workid") int workid,
	                       Model model){
		HomeWork homeWork = homeworkService.findHomeWorkById(workid);
		Project project = homeworkService.getProjectByworkId(workid);
		String InvitationCode = String.valueOf(request.getSession().getAttribute("invitationCode"));
		if(homeWork.getEncryption() == 0 || InvitationCode.equals(homeWork.getInvitationCode())){
			model.addAttribute("project",project);
			return "homeworks/upload";
		}else{
			model.addAttribute("project",project);
			return "homeworks/checkCode";
		}
	}

	/**
	 * 校验邀请码
	 * @param request
	 * @param code
	 * @param workid
	 * @return
	 */
	@PostMapping("/checkCode")
	@ResponseBody
	Result checkCode(HttpServletRequest request,
	                 @RequestParam("code") String code,
	                 @RequestParam("workid")int workid){
		log.info("校验邀请码");
		HomeWork homeWork = homeworkService.findHomeWorkById(workid);
		if(homeWork.getInvitationCode().equals(code)){
			request.getSession().setAttribute("invitationCode",code);
			return Result.success("校验通过！");
		}else {
			return Result.fail(400,"邀请码错误！");
		}
	}


	/**
	 * 文件上传处理
	 * @param file
	 * @param workid
	 * @param type
	 * @param remarks
	 * @param sname
	 * @param classname
	 * @param studentId
	 * @param model
	 * @param request
	 * @return
	 */
	@PostMapping("/upfile")
	@ResponseBody
	Result singleFileUpload(@RequestParam("file") MultipartFile file,
	                        @RequestParam("workid") int workid,
	                        @RequestParam("type") String type,
	                        @RequestParam("remarks") String remarks,
	                        @RequestParam("name") String sname,
	                        @RequestParam("studentClass") String classname,
	                        @RequestParam("studentId") String studentId,
	                        Model model,
	                        HttpServletRequest request) {
		log.info("文件上传处理");
		HomeWork homeWork = homeworkService.findHomeWorkById(workid);
		Timestamp endtime = homeWork.getEndtime();
		if (file.isEmpty()){
			return Result.fail(400,"文件不允许为空");
		}
		if(endtime.before(new Timestamp(System.currentTimeMillis()))){
			return Result.fail(400,"本项目已停止接收文件上传！");
		}
		// 文件信息处理
		String fname = file.getOriginalFilename();
		String filename = "";
		//  姓名-项目名   学号-项目名   学号-姓名-项目名   班级-姓名-项目名   班级-姓名-学号-项目名
		// 处理保存的文件名
		switch (type){
			case "1" : filename = sname + "-" + fname;break;
			case "2" : filename = studentId + "-" + fname;break;
			case "3" : filename = studentId + "-" + sname + "-" + fname;break;
			case "4" : filename = classname + "-" + sname + "-" + fname;break;
			case "5" : filename = classname + "-" + sname + "-" + studentId + "-" + fname;break;
			case "6" : filename = sname + "." + fname.substring(fname.lastIndexOf(".") + 1);break;
			case "7" : filename = sname + "." + fname.substring(fname.lastIndexOf(".") + 1);break;
		}
		log.info(sname+ "--" + studentId + "--" + filename );
		// 构造student
		Student newStudent = new Student();
		newStudent.setName(sname);
		newStudent.setClassname(classname);
		newStudent.setRemarks(remarks);
		newStudent.setUptime(new Timestamp(System.currentTimeMillis()));
		newStudent.setWorkid(workid);
		newStudent.setFilename(filename);
		newStudent.setStudentId(studentId);
		double filesize = (double) file.getSize();
		newStudent.setFilesize(filesize/1024);
		// 先查看是否重复
		Student student = studentService.getStudentBySname(workid,sname);

		// 文件已提交过了
		if(student != null){
			try{
				newStudent.setId(student.getId());    // 把id带走
				String fileUrl = fastDFSClientUtils.updateFile(file,student.getFileurl());		//使用fastDFS写入
				newStudent.setFileurl(fileUrl);
				studentService.updateStudentByNameAndId(newStudent);
				return Result.success("你已经提交过了，上传成功！");
			}catch (Exception e){
				log.error(e.getMessage());
				return Result.fail(400,"文件上传失败！");
			}
		}

		try{
			String fileUrl = fastDFSClientUtils.uploadFile(file);
			newStudent.setFileurl(fileUrl);
			studentService.addStudent(newStudent);
			homeworkService.incHomeWorkCount(workid);       // +1 人
			return Result.success("文件上传成功！");
		}catch (Exception e){
			log.error(e.getMessage());
			return Result.fail(400,"文件上传失败！");
		}
	}
}
