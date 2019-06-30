package kid1999.upload.service;

import kid1999.upload.mapper.userMapper;
import kid1999.upload.model.User;
import org.springframework.stereotype.Service;

@Service
public class userService implements userMapper {
  @Override
  public User findUser(String name) {
    return null;
  }

  @Override
  public void addUser(User user) {

  }

  @Override
  public User login(User user) {
    return null;
  }


  // 新建用户并返回用户的信息
  public User addAndfindUser(User user){
    login(user);
    return findUser(user.getName());
  }

}
