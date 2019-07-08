package kid1999.upload.service;

import kid1999.upload.mapper.studentMapper;
import kid1999.upload.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class studentService {

	@Autowired
	private studentMapper studentMapper;

	// 返回给user 的所有 remark
	public List<Student> getRemarksByUserid(int userid) {
		return studentMapper.getRemarks(userid);
	}

	public List<Student> getStudentsByTitle(String worktitle,int userid){
		return studentMapper.getStudentsByTitle(worktitle,userid);
	}

}
