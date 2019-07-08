package kid1999.upload.service;

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
    if(filePath.exists()) filePath.delete();
    filePath.mkdirs();  // 新建文件夹
    return path;
  }



  public User findUser(String name) {
    return userMapper.findUser(name);
  }


  public void addUser(User user) {
    userMapper.addUser(user);
  }


  public User login(User user) {
    return userMapper.login(user);
  }
}
