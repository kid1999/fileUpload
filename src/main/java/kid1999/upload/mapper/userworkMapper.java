package kid1999.upload.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import kid1999.upload.model.Userwork;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface userworkMapper extends BaseMapper<Userwork> {

  @Insert("insert into userswork (workid, userid) values (#{workid},#{userid})")
  void add(int workid, int userid);
}
