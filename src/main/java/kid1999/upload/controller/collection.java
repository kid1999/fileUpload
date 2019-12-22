package kid1999.upload.controller;

import kid1999.upload.dto.Projects;
import kid1999.upload.dto.Result;
import kid1999.upload.model.Student;
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
public class collection {

	@Autowired
	private homeworkService homeworkService;

	@Autowired
	private studentService studentService;

	@Autowired
	private FastDFSClientUtils fastDFSClientUtils;


	//　这是admin 某个项目详情页面
	@GetMapping("/deatils")
	String Test(Model model,
				@RequestParam(value = "worktitle") String worktitle){
		model.addAttribute("worktitle",worktitle);
		return "homeworks/homeworkDetails";
	}


	// 文件上传页面
	@GetMapping("/upload")
	String postcollection( @RequestParam(value = "worktitle") String worktitle,
	                       @RequestParam(value = "userid") int userid,
	                       Model model){
		Projects project = homeworkService.getProByTitle(worktitle,userid);
		model.addAttribute("project",project);
		return "homeworks/upload";
	}

	// 文件上传处理
	@PostMapping("/upfile")
	@ResponseBody
	Result singleFileUpload(@RequestParam("file") MultipartFile file,
	                        @RequestParam("workid") int workid,
	                        @RequestParam("type") String type,
	                        @RequestParam("remarks") String remarks,
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
		log.info(sname+ "--" + studentno + "--" + filename );
		// 构造student
		Student newStudent = new Student();
		newStudent.setName(sname);
		newStudent.setClassname(classname);
		newStudent.setRemarks(remarks);
		newStudent.setUptime(new Timestamp(System.currentTimeMillis()));
		newStudent.setWorkid(workid);
		newStudent.setFilename(filename);
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
