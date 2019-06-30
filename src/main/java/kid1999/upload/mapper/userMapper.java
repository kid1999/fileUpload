package kid1999.upload.mapper;

import kid1999.upload.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface userMapper {

  @Select("select * from user where name = #{name} ")
  User findUser(String name);

  @Insert("insert into user (name,password) values (#{name},#{password})")
  void addUser(User user);

  @Select("select * from user where name = #{name} and password = #{password}")
  User login(User user);



}
