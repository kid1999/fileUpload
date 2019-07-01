package kid1999.upload.service;

import kid1999.upload.mapper.homeworkMapper;
import kid1999.upload.model.HomeWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class homeworkService  {

  @Autowired
  private homeworkMapper homeworkMapper;

  public HomeWork addAndfind(HomeWork homeWork){
    homeworkMapper.addHomeWork(homeWork);
    return homeworkMapper.findHK(homeWork.getTitle());
  }

}
