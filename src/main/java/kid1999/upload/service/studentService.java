package kid1999.upload.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import kid1999.upload.mapper.studentMapper;
import kid1999.upload.model.Student;
import kid1999.upload.utils.FastDFSClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class studentService {

	@Autowired
	private studentMapper studentMapper;

	@Autowired
	private FastDFSClientUtils fastDFSClientUtils;


	// 用workid 和 sname 查询是否已经提交过
	public Student getStudentBySname(int workid, String sname) {
		QueryWrapper<Student> wrapper = new QueryWrapper<>();
		wrapper.eq("workid",workid).eq("name",sname);
		return studentMapper.selectOne(wrapper);
	}

	// 添加student
	public int addStudent(Student newStudent) {
		return studentMapper.insert(newStudent);
	}

	// 更新student by id and name
	public int updateStudentByNameAndId(Student student) {
		QueryWrapper<Student> wrapper = new QueryWrapper<>();
		wrapper.eq("id",student.getId())
				.eq("name",student.getName());
		return studentMapper.update(student,wrapper);
	}

	// 更新student by id
	public void updateStudentById(Student student) {
		studentMapper.updateById(student);
	}


	// 删除一个学生提交信息
	public void deleteStudentById(int studentId) {
		studentMapper.deleteById(studentId);
	}

	// 查询提交信息 通过 workid
	public List<Student> getStudentsByWorkId(String workid, int pageNo, int pageSize) {
		IPage<Student> page = new Page<>(pageNo, pageSize);
		QueryWrapper<Student> wrapper = new QueryWrapper<>();
		wrapper.eq("workid",workid);
		IPage<Student> studentIPage = studentMapper.selectPage(page, wrapper);
		return studentIPage.getRecords();
	}

	// 按workid 全部删除
    public void deleteStudentByWorkId(int workId) {
		QueryWrapper<Student> wrapper = new QueryWrapper<>();
		wrapper.eq("workid",workId);
		List selectList = studentMapper.selectList(wrapper);		// 查询
		for (Object object: selectList) {
			String fileUrl = ((Student) object).getFileurl();		// 清空文件
			fastDFSClientUtils.deleteFile(fileUrl);
		}
		studentMapper.delete(wrapper);		// 删除所有
	}
}
