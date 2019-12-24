package kid1999.upload.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import kid1999.upload.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface userMapper extends BaseMapper<User> {


  @Select("select * from user where name = #{name} and password = #{password}")
  User login(User user);



}
