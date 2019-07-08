package kid1999.upload.mapper;

import kid1999.upload.model.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface studentMapper {

	@Select("select * from student where workid in (SELECT workid from userswork where userid = #{userid})")
	List<Student> getRemarks(int userid);


	@Select("select count(*) from student where workid = #{workid}")
	Integer countByWorkId(int workid);

	@Select("select * from student where workid in (select workid from userswork where userid = #{userid}) and workid in (select id from homework where title = #{worktitle})")
	public List<Student> getStudentsByTitle(String worktitle,int userid);
}
