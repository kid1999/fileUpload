package kid1999.upload.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Collection {

  @GetMapping("/collection")
  String getcollection(){
    return "collection";
  }

  @PostMapping("/collection")
  String postcollection(){
    return "";
  }
}
