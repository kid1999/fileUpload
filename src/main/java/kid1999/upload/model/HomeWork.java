package kid1999.upload.model;

import lombok.Data;

@Data
public class HomeWork {
  private int id;
  private String title;
  private String infomation;
  private long createtime;
  private long endtime;
  private String type;
  private String addr;
}
