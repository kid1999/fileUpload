package kid1999.upload.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import kid1999.upload.mapper.userMapper;
import kid1999.upload.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class userService {

  @Autowired
  private userMapper userMapper;

  @Value("${projectPath}")
  private String projectPath;


  //	生成一个文件路径保存一个项目的文件
  public String makePath(int userid,String workname){
    String path =  projectPath + File.separator + userid + File.separator + workname;
    File filePath = new File(path);
    if(filePath.exists()) {
      filePath.delete();
    }
    filePath.mkdirs();  // 新建文件夹
    return path;
  }


  // 简单查询使用 QueryWrapper
  public User findUserByName(String name) {
    QueryWrapper wrapper = new QueryWrapper();
    wrapper.eq("name",name);
    return userMapper.selectOne(wrapper);
  }

  // 增加 使用自带的 insert
  public int addUser(User user) {
    return userMapper.insert(user);
  }


  public User login(User user) {
    QueryWrapper<User> wrapper = new QueryWrapper<>();
    wrapper.eq("name",user.getName())
            .eq("password",user.getPassword());
    return userMapper.selectOne(wrapper);
  }
}
