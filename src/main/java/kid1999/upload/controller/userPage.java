package kid1999.upload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import kid1999.upload.mapper.userMapper;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class userPage {

  @Autowired
  private userMapper userMapper;

  @GetMapping("/userpage")
  String userpage(){

    return "userpage";
  }

  @GetMapping("/createWork")
  String createWork(){

    return "";
  }


  @GetMapping("/showWork")
  String showWork(){


    return "";
  }
}
