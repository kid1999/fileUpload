package kid1999.upload.service;

import cn.hutool.core.date.DateTime;
import kid1999.upload.dto.Projects;
import kid1999.upload.mapper.homeworkMapper;
import kid1999.upload.mapper.studentMapper;
import kid1999.upload.mapper.userworkMapper;
import kid1999.upload.model.HomeWork;
import kid1999.upload.model.Userwork;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class homeworkService{

	@Autowired
	private homeworkMapper homeworkMapper;

	@Autowired
	private studentMapper studentMapper;

	@Autowired
	private userworkMapper userworkMapper;

	// 新建work项目并返回对象
	public HomeWork addHomeWork(HomeWork homeWork){
		homeworkMapper.insert(homeWork);
		return homeWork;
	}

	// 通过title寻找work对象
	public HomeWork findHKByTitleAndUserID(String title,int userId) {
		return homeworkMapper.findHKByTitleAndUserID(title,userId);
	}

	// 返回一个user 的所有work项目
	public List<HomeWork> findByUserId(Integer userid){
		List<HomeWork> homeWorks = homeworkMapper.findByUserId(userid);
		return homeWorks;
	}

	// 查询当前的 已完成和未完成 项目
	public List<List<Projects>> findProjects(Integer userid){
		List<Projects> DoingProjects = new ArrayList<>();
		List<Projects> DoneProjects = new ArrayList<>();
		List<List<Projects>> res = new ArrayList<>();
		List<HomeWork> homeWorks = findByUserId(userid);
		log.info(homeWorks.toString());
		for (HomeWork homeWork:homeWorks) {
			if(homeWork.getEndtime().before(DateTime.now())){
				DoingProjects.add(new Projects(homeWork,studentMapper.countByWorkId(homeWork.getId())));
			}else{
				DoneProjects.add(new Projects(homeWork,studentMapper.countByWorkId(homeWork.getId())));
			}
		}
		log.info(DoingProjects.toString());
		log.info(DoneProjects.toString());
		res.add(DoingProjects);
		res.add(DoneProjects);
		return res;
	}


	// 关联 work 和 user
	public void add(int workid, int userid) {
		Userwork userwork = new Userwork();
		userwork.setWorkid(workid);
		userwork.setUserid(userid);
		userworkMapper.insert(userwork);
	}

	public Projects getProByTitle(String worktitle, int userid) {
		HomeWork homeWork = studentMapper.getWorkByTitle(worktitle,userid);
		if(homeWork != null){
			return new Projects(homeWork,studentMapper.countByWorkId(homeWork.getId()));
		} else{
			return null;
		}
	}
}
