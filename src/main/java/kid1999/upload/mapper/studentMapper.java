package kid1999.upload.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import kid1999.upload.model.HomeWork;
import kid1999.upload.model.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface studentMapper extends BaseMapper<Student> {

	@Select("select * from student where remarks <> '' or remarks is null and  workid in (SELECT workid from userswork where userid = #{userid}) order by uptime desc limit 10 ")
	List<Student> getRemarks(int userid);

	@Select("select count(*) from student where workid = #{workid}")
	Integer countByWorkId(int workid);

	@Select("select * from student where workid in (select workid from userswork where userid = #{userid}) and workid in (select id from homework where title = #{worktitle})")
	public List<Student> getStudentsByTitle(String worktitle,int userid);

	@Select("select * from homework where title = #{worktitle} and id in (select workid from userswork where userid = #{userid})")
	HomeWork getWorkByTitle(String worktitle, int userid);


	@Select("select * from student where filename like CONCAT('%',#{filename},'%') ")
	List<Student> findStuByfilename(String filename);

	@Select("select * from student where workid = (select workid from student where id=#{id})")
	List<Student> getPathById(int id);
}
