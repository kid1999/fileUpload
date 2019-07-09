package kid1999.upload.mapper;

import kid1999.upload.model.HomeWork;
import kid1999.upload.model.Student;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface studentMapper {

	@Select("select * from student where remarks <> '' or remarks is null and  workid in (SELECT workid from userswork where userid = #{userid}) order by uptime desc limit 10 ")
	List<Student> getRemarks(int userid);

	@Select("select count(*) from student where workid = #{workid}")
	Integer countByWorkId(int workid);

	@Select("select * from student where workid in (select workid from userswork where userid = #{userid}) and workid in (select id from homework where title = #{worktitle})")
	public List<Student> getStudentsByTitle(String worktitle,int userid);

	@Select("select * from homework where title = #{worktitle} and id in (select workid from userswork where userid = #{userid})")
	HomeWork getWorkByTitle(String worktitle, int userid);

	@Select("select * from student where name = #{sname} and workid = #{workid}")
	Student getStudentBySname(int workid, String sname);

	@Insert("insert into student(name, classname, remarks, uptime, workid) values (" +
			        "#{name} ,#{classname} ,#{remarks} ,#{uptime} ,#{workid} )")
	void addStudent(Student newStudent);

	@Update("update student set name =#{name},classname=#{classname},remarks=#{remarks}," +
			        "uptime = #{uptime} ,workid=#{workid} where id=#{id} ")
	void updateStudent(Student student);
}
