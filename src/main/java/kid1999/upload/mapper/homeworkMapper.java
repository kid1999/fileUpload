package kid1999.upload.mapper;


import kid1999.upload.model.HomeWork;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface homeworkMapper {

  @Select("select * from homework where title = #{title}")
  HomeWork findHK(String title);

  @Insert("insert into homework(title, infomation, createtime, endtime, type, addr) values (#{title},#{infomation} ,#{createtime} ,#{endtime} ,#{type} ,#{addr} )")
  void addHomeWork(HomeWork homeWork);
}
