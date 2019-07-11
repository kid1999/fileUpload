package kid1999.upload.service;

import kid1999.upload.dto.Projects;
import kid1999.upload.mapper.homeworkMapper;
import kid1999.upload.mapper.studentMapper;
import kid1999.upload.mapper.userworkMapper;
import kid1999.upload.model.HomeWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class homeworkService{

  @Autowired
  private homeworkMapper homeworkMapper;

  @Autowired
  private studentMapper studentMapper;

  @Autowired
  private userworkMapper userworkMapper;

  // 新建 work 项目
  public void addHomeWork(HomeWork homeWork) {
    homeworkMapper.addHomeWork(homeWork);
  }

  // 新建work项目并返回对象
  public HomeWork addAndfind(HomeWork homeWork){
    addHomeWork(homeWork);
    return homeworkMapper.findHK(homeWork.getTitle());
  }

  // 通过title寻找work对象
  public HomeWork findHK(String title) {
    return homeworkMapper.findHK(title);
  }

  // 返回一个user 的所有work项目
  public List<HomeWork> findByUserId(Integer userid){
    List<HomeWork> homeWorks = homeworkMapper.findByUserId(userid);
    return homeWorks;
  }

  // 辅助函数： 构造一个project
  public Projects makeProject(HomeWork homeWork){
    Projects project = new Projects();
    project.setTitle(homeWork.getTitle());
    // date -> stirng
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date create = new Date(homeWork.getCreatetime());
    Date end = new Date(homeWork.getEndtime());
    project.setCreatetime(format.format(create));
    project.setEndtime(format.format(end));
    project.setCount(studentMapper.countByWorkId(homeWork.getId()));
    project.setInfomation(homeWork.getInfomation());
    project.setType(homeWork.getType());
    project.setFilepath(homeWork.getAddr());
    project.setId(homeWork.getId());
    return project;
  }

  // user 当前正在ing 的项目
  public List<Projects> findDoingProjects(Integer userid){
    List<Projects> projects = new ArrayList<>();
    long now = new Date().getTime();
    List<HomeWork> homeWorks = findByUserId(userid);
    for (HomeWork homeWork:homeWorks) {
      if(homeWork.getEndtime() >= now){
        projects.add(makeProject(homeWork));
      }
    }
    return projects;
  }

  // user 当前已经结束的项目
  public List<Projects> findDoneProjects(Integer userid){
    List<Projects> projects = new ArrayList<>();
    long now = new Date().getTime();
    List<HomeWork> homeWorks = findByUserId(userid);
    for (HomeWork homeWork:homeWorks) {
      if(homeWork.getEndtime() < now){
        projects.add(makeProject(homeWork));
      }
    }
    return projects;
  }

  // 关联 work 和 user
  public void add(int workid, int userid) {
    userworkMapper.add(workid,userid);
  }

  public Projects getProByTitle(String worktitle, int userid) {
    HomeWork homeWork = studentMapper.getWorkByTitle(worktitle,userid);
    if(homeWork != null) return makeProject(homeWork);
    else return null;
  }

	public String findaddrBySid(int uid) {
    return homeworkMapper.findaddrBySid(uid);
	}
}
