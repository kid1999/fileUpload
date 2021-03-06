package kid1999.upload.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import kid1999.upload.mapper.userMapper;
import kid1999.upload.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class userService {

	@Autowired
	private userMapper userMapper;


	// 简单查询使用 QueryWrapper
	public User findUserByName(String name) {
		QueryWrapper wrapper = new QueryWrapper();
		wrapper.eq("name",name);
		return userMapper.selectOne(wrapper);
	}

	public User findUserByEmail(String email){
		QueryWrapper wrapper = new QueryWrapper();
		wrapper.eq("email",email);
		return userMapper.selectOne(wrapper);
	}


	// 增加 使用自带的 insert
	public int addUser(User user) {
		return userMapper.insert(user);
	}

	// 更新
	public int updateUser(User user){
		QueryWrapper wrapper = new QueryWrapper();
		wrapper.eq("name",user.getName());
		return userMapper.update(user,wrapper);
	}

	// selectOne
	public User login(User user) {
		QueryWrapper<User> wrapper = new QueryWrapper<>();
		wrapper.eq("name",user.getName())
				.eq("password",user.getPassword());
		return userMapper.selectOne(wrapper);
	}

	// 通过userid 查询user
	public User findUserById(int id) {
		return userMapper.selectById(id);
	}
}
