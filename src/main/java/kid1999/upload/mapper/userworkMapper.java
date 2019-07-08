package kid1999.upload.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface userworkMapper {

  @Insert("insert into userswork (workid, userid) values (#{workid},#{userid})")
  void add(int workid, int userid);
}
