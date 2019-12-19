package kid1999.upload.controller;

import kid1999.upload.dto.Projects;
import kid1999.upload.dto.Result;
import kid1999.upload.dto.Students;
import kid1999.upload.model.Student;
import kid1999.upload.model.User;
import kid1999.upload.service.homeworkService;
import kid1999.upload.service.studentService;
import kid1999.upload.utils.FastDFSClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
public class collection {

	@Autowired
	private homeworkService homeworkService;

	@Autowired
	private studentService studentService;

	@Autowired
	private FastDFSClientUtils fastDFSClientUtils;


	//　这是admin 某个项目详情页面
	@GetMapping("/deatils")
	String getcollection(HttpServletRequest request,
	                     @RequestParam(value = "worktitle") String worktitle,
	                     @RequestParam(value = "userid") int userid,
	                     Model model){
		Object session =  request.getSession().getAttribute("user");
		if(session == null){
			return "redirect:index";
		}
		User user = (User) session;
		if(user.getId() == userid){
			List<Student> students = studentService.getStudentsByTitle(worktitle,userid);
			List<Students> studentDto = new ArrayList<>();
			List<Students> remarks = new ArrayList<>();
			for (Student student:students) {
				Students stu = new Students();
				stu.setId(student.getId());
				stu.setName(student.getName());
				stu.setFilename(student.getFilename());
				stu.setClassname(student.getClassname());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				stu.setTime(format.format(student.getUptime()));
				stu.setFileurl(student.getFileurl());
				studentDto.add(stu);
				String remark = student.getRemarks();
				if(remark != null && !remark.equals("")){
					stu.setRemarks(remark);
					remarks.add(stu);
				}
			}
			model.addAttribute("studentDto",studentDto);
			model.addAttribute("count",students.size());
			model.addAttribute("worktitle",worktitle);
			model.addAttribute("remarks",remarks);
			if(studentDto.size() != 0){
				model.addAttribute("studentid",studentDto.get(0).getId());
			}
			return "homeworkList";
		}else{    // 这是访问用户
			model.addAttribute("info","访问权限错误!");
			return "error";
		}
	}

	// 文件上传页面
	@GetMapping("/upload")
	String postcollection( @RequestParam(value = "worktitle") String worktitle,
	                       @RequestParam(value = "userid") int userid,
	                       Model model){
		int count = studentService.getStudentsByTitle(worktitle,userid).size();
		Projects project = homeworkService.getProByTitle(worktitle,userid);
		model.addAttribute("count",count);
		model.addAttribute("project",project);
		return "upload";
	}

	// 文件上传处理
	@PostMapping("/upfile")
	@ResponseBody
	Result singleFileUpload(@RequestParam("file") MultipartFile file,
	                        @RequestParam("workid") int workid,
	                        @RequestParam("type") String type,
	                        @RequestParam("remarks") String remarks,
	                        @RequestParam("filepath") String filepath,
	                        @RequestParam("name") String sname,
	                        @RequestParam("studentClass") String classname,
	                        @RequestParam("studentId") String studentno,
	                        Model model,
	                        HttpServletRequest request) {

		Result result = new Result();
		if (file.isEmpty()){
			return Result.fail(400,"文件不允许为空");
		}

		// 获取来访页面url
		String referer = request.getHeader("referer");
		if(referer != null){
			model.addAttribute("referer",referer);
		}else{
			model.addAttribute("referer",request.getHeader("host"));
		}

		// 文件信息处理
		String fname = file.getOriginalFilename();
		String filename = "";
		//  姓名-项目名   学号-项目名   学号-姓名-项目名   班级-姓名-项目名   班级-姓名-学号-项目名
		// 处理保存的文件名
		switch (type){
			case "1" : filename = sname + "-" + fname;break;
			case "2" : filename = studentno + "-" + fname;break;
			case "3" : filename = studentno + "-" + sname + "-" + fname;break;
			case "4" : filename = classname + "-" + sname + "-" + fname;break;
			case "5" : filename = classname + "-" + sname + "-" + studentno + "-" + fname;break;
			case "6" : filename = sname + "." + fname.substring(fname.lastIndexOf(".") + 1);break;
			case "7" : filename = sname + "." + fname.substring(fname.lastIndexOf(".") + 1);break;
		}

		// 构造student
		Student newStudent = new Student();
		newStudent.setName(sname);
		newStudent.setClassname(classname);
		newStudent.setRemarks(remarks);
		newStudent.setUptime(System.currentTimeMillis());
		newStudent.setWorkid(workid);
		newStudent.setFilename(filename);

		// 先查看是否重复
		Student student = studentService.getStudentBySname(workid,sname);

		// 文件已提交过了
		if(student != null){
			try{
				newStudent.setId(student.getId());    // 把id带走
				String fileUrl = fastDFSClientUtils.updateFile(file,student.getFileurl());		//使用fastDFS写入
				newStudent.setFileurl(fileUrl);
				studentService.updateStudent(newStudent);
				return Result.success("你已经提交过了，上传成功！");
			}catch (Exception e){
				return Result.fail(400,"文件上传失败！");
			}
		}

		try{
			String fileUrl = fastDFSClientUtils.uploadFile(file);
			newStudent.setFileurl(fileUrl);
			studentService.addStudent(newStudent);
			return Result.success("文件上传成功！");
		}catch (Exception e){
			return Result.fail(400,"文件上传失败！");
		}
	}
}
