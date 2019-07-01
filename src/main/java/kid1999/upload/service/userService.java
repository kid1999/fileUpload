package kid1999.upload.service;

import kid1999.upload.mapper.userMapper;
import kid1999.upload.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class userService {

  @Autowired
  private userMapper userMapper;

  // 新建用户并返回用户的信息
  public User addAndfindUser(User user){
    userMapper.login(user);
    return userMapper.findUser(user.getName());
  }

}
