package kid1999.upload.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import kid1999.upload.model.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface studentMapper extends BaseMapper<Student> {


	@Select("select * from student where filename like CONCAT('%',#{filename},'%') ")
	List<Student> findStuByfilename(String filename);

	// 返回null 会抛错
	@Select("SELECT COALESCE(SUM(filesize),0) from student where workid = #{workid}")
	double getCapacity(int workid);

}
