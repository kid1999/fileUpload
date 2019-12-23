package kid1999.upload.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;


@Data
public class Student {
  private int id;
  private String studentId;
  private String name;
  private String classname;
  private int workid;
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Timestamp uptime;
  private String remarks;
  private String filename;
  private String fileurl;
  private Double filesize;
}
