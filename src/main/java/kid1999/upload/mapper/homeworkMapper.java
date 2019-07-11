package kid1999.upload.mapper;


import kid1999.upload.model.HomeWork;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface homeworkMapper {

  @Select("select * from homework where title = #{title}")
  HomeWork findHK(String title);

  @Insert("insert into homework(title, infomation, createtime, endtime, type, addr) values (#{title},#{infomation} ,#{createtime} ,#{endtime} ,#{type} ,#{addr} )")
  void addHomeWork(HomeWork homeWork);

  @Select("select * from homework where id in (select workid from userswork where userid = #{userid} ) ")
	List<HomeWork> findByUserId(Integer userid);

  @Select("select addr from homework where id = (select workid from student where student.id = #{uid})")
	String findaddrBySid(int uid);
}


