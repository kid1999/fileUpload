package kid1999.upload.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import kid1999.upload.dto.Project;
import kid1999.upload.mapper.homeworkMapper;
import kid1999.upload.mapper.studentMapper;
import kid1999.upload.model.HomeWork;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class homeworkService{

	@Autowired
	private homeworkMapper homeworkMapper;

	@Autowired
	private studentMapper studentMapper;


	// 新建work项目并返回对象
	public HomeWork addHomeWork(HomeWork homeWork){
		homeworkMapper.insert(homeWork);
		return homeWork;
	}

	// 通过title,userid 寻找work对象
	public HomeWork findHKByTitleAndUserID(String title,int userId) {
		QueryWrapper<HomeWork> wrapper = new QueryWrapper<>();
		wrapper.eq("title",title).eq("user_id",userId);
		return homeworkMapper.selectOne(wrapper);
	}

	// 返回一个user 的所有work项目
	public List<HomeWork> findWorksByUserId(Integer userId){
		QueryWrapper<HomeWork> wrapper = new QueryWrapper<>();
		wrapper.eq("user_id",userId);
		return homeworkMapper.selectList(wrapper);
	}

	// 查询当前的 已完成和未完成 项目
	public List<List<Project>> findProjects(Integer userId){
		List<Project> DoingProjects = new ArrayList<>();
		List<Project> DoneProjects = new ArrayList<>();
		List<List<Project>> res = new ArrayList<>();
		List<HomeWork> homeWorks = findWorksByUserId(userId);
		for (HomeWork homeWork:homeWorks) {
			if(homeWork.getEndtime().before(new Timestamp(System.currentTimeMillis()))){
				DoneProjects.add(new Project(homeWork,countByWorkId(homeWork.getId())));
			}else{
				DoingProjects.add(new Project(homeWork,countByWorkId(homeWork.getId())));
			}
		}
		res.add(DoingProjects);
		res.add(DoneProjects);
		return res;
	}

	// 统计student数量
	public int countByWorkId(Integer workId){
		QueryWrapper wrapper = new QueryWrapper();
		wrapper.eq("workid",workId);
		return studentMapper.selectCount(wrapper);
	}


	// 通过workId 查询 homework
	public HomeWork findUserIdByWorkId(int workId) {
		QueryWrapper wrapper = new QueryWrapper();
		wrapper.eq("id",workId);
		return homeworkMapper.selectOne(wrapper);
	}

	// 回传project 通过 workid
	public Project getProjectByworkId(int worktid) {
		HomeWork homeWork = homeworkMapper.selectById(worktid);
		if(homeWork != null){
			return new Project(homeWork,countByWorkId(homeWork.getId()));
		} else{
			return null;
		}
	}

	// 通过workId 查询homework
	public HomeWork findHomeWorkById(int worktid) {
		QueryWrapper wrapper = new QueryWrapper();
		wrapper.eq("id",worktid);
		return homeworkMapper.selectOne(wrapper);
	}

	// 汇总一个项目的文件夹空间
	public double CountHomeWorkCapacityById(int id) {
		return  studentMapper.getCapacity(id);
	}

	// 获取项目磁盘使用量
	public double getCapacity(int workid){
		return studentMapper.getCapacity(workid);
	}

	// 更新homework
	public void updateHomeWork(HomeWork homeWork) {
		homeworkMapper.updateById(homeWork);
	}

	// 增加一个人数
	public void incHomeWorkCount(int workId) {
		homeworkMapper.incHomeWorkCountById(workId);
	}

	// 减少一个人数
	public void minusHomeWorkCount(int workId) {
		homeworkMapper.minusHomeWorkCountById(workId);
	}


	// 删除一个作业
	public void deleteHomeWorkById(int id) {
		homeworkMapper.deleteById(id);
	}

    public void changeEncryption(int id) {
		HomeWork homeWork = findHomeWorkById(id);
		if(homeWork.getEncryption() == 0){
			String invitaionCode = UUID.randomUUID().toString().substring(0,8);
			homeWork.setInvitationCode(invitaionCode);
			homeWork.setEncryption(1);
		}else {
			homeWork.setInvitationCode("");
			homeWork.setEncryption(0);
		}
		homeworkMapper.updateById(homeWork);
    }
}
